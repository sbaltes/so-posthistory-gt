package de.unitrier.st.soposthistory.gt.GroundTruthApp;

import de.unitrier.st.soposthistory.blocks.CodeBlockVersion;
import de.unitrier.st.soposthistory.blocks.PostBlockVersion;
import de.unitrier.st.soposthistory.blocks.TextBlockVersion;
import de.unitrier.st.soposthistory.diffs.LineDiff;
import de.unitrier.st.soposthistory.diffs.diff_match_patch;
import de.unitrier.st.soposthistory.gt.PostBlockLifeSpan;
import de.unitrier.st.soposthistory.gt.PostBlockLifeSpanVersion;
import de.unitrier.st.soposthistory.version.PostVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;
import de.unitrier.st.util.Util;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GroundTruthCreator extends JFrame{
    private static Logger logger;

    static {
        try {
            logger = Util.getClassLogger(GroundTruthCreator.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** Swing components *****/
    JPanel mainPanel = new JPanel(new BorderLayout());
    JScrollPane scrollPaneIncludingMainPanel = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    ButtonsAndInstructionsPanel buttonsAtTopPanel = null;

    private JPanel compareVersionsPanel = null;
    JPanel versionLeftPanel = new JPanel(new MigLayout());
    JPanel versionRightPanel = new JPanel(new MigLayout());
    JPanel versionEdgesPanel = new JPanel();
    NavigatorLabel navigatorAtBottomLabel = null;

    /***** colors *****/
    private Color colorTextBlockMarked = new Color(30f/255,120f/255,1, 0.25f);
    private Color colorTextBlockUnMarked = new Color(30f/255,120f/255,1, 1f);
    private Color colorCodeBlockMarked = new Color(1,127f/255,0f/255, 0.25f);
    private Color colorCodeBlockUnMarked = new Color(1,127f/255,0f/255, 1f);

    private int borderThickness = 5;


    /***** Intern variables *****/
    int currentLeftVersion = 0;

    final static Path path = Paths.get("postVersionLists");

    private int lastClickedInternVersion = -1;
    private int lastClickedPositionOfABlock = -1;
    private JLabel lastClickedBlock = null;
    private boolean lastClickedBlockIsInstanceOfTextBlockVersion;

    enum LinkConnectionDisplayModes {edges, films}
    static LinkConnectionDisplayModes linkConnectionDisplayMode = LinkConnectionDisplayModes.films;

    LinkedList<LinkedList<BlockPair>> allCreatedBlockPairsByClicks = new LinkedList<>();
    LinkedList<LinkedList<BlockPair>> allAutomaticSetBlockPairs = new LinkedList<>();
    LinkedList<PostBlockLifeSpan> blockLifeSpansExtractedFromClicks = new LinkedList<>();

    Robot bot = null;

    Polygon film = new Polygon();

    List<Integer> postHistoryIDs;
    Map<Integer, Integer> postHistoryIdToVersion;


    /***** Constructor arguments *****/
    PostVersionList postVersionList;
    static int WIDTH;
    static int HEIGHT;
    static Point LOCATION;



    /***** Constructor *****/
    GroundTruthCreator(PostVersionList postVersionList, int initialWidth, int initialHeight, Point initialLocation){
        this.setTitle("Ground Truth Creator");
        this.postVersionList = postVersionList;

        WIDTH = initialWidth;
        HEIGHT = initialHeight;
        LOCATION = initialLocation;

        try {
            bot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        if(postVersionList != null){
            //postHistoryIDs = postVersionList.getPostHistoryIds();
            postHistoryIdToVersion = new HashMap<>();
            for (int i=0; i<postVersionList.size(); i++){
                postHistoryIdToVersion.put(postVersionList.get(i).getPostHistoryId(), i+1);
            }


            for(int i=0; i<postVersionList.size(); i++){
                allCreatedBlockPairsByClicks.add(new LinkedList<>());
                allAutomaticSetBlockPairs.add(new LinkedList<>());
            }

            postVersionList.normalizeLinks();
        }

        this.setSize(new Dimension(initialWidth, initialHeight));


        buttonsAtTopPanel = new ButtonsAndInstructionsPanel(this);
        buttonsAtTopPanel.setFocusable(true);
        mainPanel.add(buttonsAtTopPanel, BorderLayout.NORTH);

        compareVersionsPanel = displayPlainTwoVersionsWithoutBlocks();
        mainPanel.add(compareVersionsPanel);

        navigatorAtBottomLabel = new NavigatorLabel(this);
        mainPanel.add(navigatorAtBottomLabel, BorderLayout.SOUTH);

        mainPanel.setBackground(Color.BLACK);

        setListenersToFrameAndPanel();

        (buttonsAtTopPanel).setEnablingOfNextAndBackButton();

        displayCurrentTwoVersionsAndNavigator();

        scrollPaneIncludingMainPanel.getVerticalScrollBar().setUnitIncrement(16);   // https://stackoverflow.com/a/5583571
        scrollPaneIncludingMainPanel.setFocusable(false);

        this.add(scrollPaneIncludingMainPanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        if(initialLocation == null)
            this.setLocationRelativeTo(null);
        else
            this.setLocation(initialLocation);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // https://stackoverflow.com/a/11570414
        this.setFocusable(true);
        this.setVisible(true);


        collectAllBlockPairsWithEqualContent();


        SwingUtilities.invokeLater(this::repaint);
    }


    /***** graphical stuff *****/

    // mask of two versions
    private JPanel displayPlainTwoVersionsWithoutBlocks(){

        compareVersionsPanel = new JPanel(new MigLayout(
                "",
                "[grow][grow][grow]",
                ""));

        versionLeftPanel.setBackground(Color.WHITE);
        versionRightPanel.setBackground(Color.WHITE);
        versionEdgesPanel.setBackground(Color.LIGHT_GRAY);

        compareVersionsPanel.add(versionLeftPanel, "align left, grow");
        compareVersionsPanel.add(versionEdgesPanel, "align center, grow 10 100");
        compareVersionsPanel.add(versionRightPanel, "align right, grow");
        compareVersionsPanel.setBackground(Color.GRAY);

        versionLeftPanel.setPreferredSize(new Dimension(compareVersionsPanel.getWidth()*42/100, compareVersionsPanel.getHeight()));
        versionRightPanel.setPreferredSize(new Dimension(compareVersionsPanel.getWidth()*42/100, compareVersionsPanel.getHeight()));

        versionEdgesPanel.setMinimumSize(new Dimension(this.getWidth()*10/100, compareVersionsPanel.getHeight()));

        return compareVersionsPanel;
    }

    // displays all blocks of one version
    private void displayAllBlocksOfOneVersionAndSetMouseListeners(JPanel versionLeftOrRight, boolean isVersionLeft){
        versionLeftOrRight.removeAll();
        int currentInternVersion = isVersionLeft ? currentLeftVersion : currentLeftVersion + 1;
        if(postVersionList == null)return;

        List<PostBlockVersion> blocks = postVersionList.get(currentInternVersion).getPostBlocks();

        for(int currentBlockPosition=0; currentBlockPosition<blocks.size(); currentBlockPosition++){

            final Border[] borderCurrentBlock = new Border[1];
            int finalCurrentBlockPosition = currentBlockPosition;
            boolean clickedBlockIsInstanceOfTextBlockVersion = blocks.get(currentBlockPosition) instanceof TextBlockVersion;

            boolean blockIsAlreadyInAPair
                    = (currentLeftVersion == currentInternVersion) ?
                    Toolkit.blockIsAlreadyInPairWithEdge_atPositionLeft(allCreatedBlockPairsByClicks, currentLeftVersion, finalCurrentBlockPosition)
                    : Toolkit.blockIsAlreadyInPairWithEdge_atPositionRight(allCreatedBlockPairsByClicks, currentLeftVersion, finalCurrentBlockPosition);

            String markupFromMarkdown =
                    "<html><head></head><body>" +
                            commonmarkMarkUp(
                                    blocks.get(currentBlockPosition).getContent()
                            )
                            + "</body></html>"
            ;

            JLabel currentBlockLabel = new JLabel(markupFromMarkdown);
            versionLeftOrRight.add(currentBlockLabel, "wrap");

            // set links automatically when content is equal
            for(int i=0; i<allAutomaticSetBlockPairs.get(currentLeftVersion).size(); i++){
                if(currentLeftVersion == currentInternVersion){
                    if(allAutomaticSetBlockPairs.get(currentLeftVersion).get(i).leftBlockPosition == currentBlockPosition){
                        allAutomaticSetBlockPairs.get(currentLeftVersion).get(i).labelLeftBlock = currentBlockLabel;
                        blockIsAlreadyInAPair = true;
                    }
                }else{
                    if(allAutomaticSetBlockPairs.get(currentLeftVersion).get(i).rightBlockPosition == currentBlockPosition){
                        allAutomaticSetBlockPairs.get(currentLeftVersion).get(i).labelRightBlock = currentBlockLabel;
                        blockIsAlreadyInAPair = true;
                    }
                }

                if(allAutomaticSetBlockPairs.get(currentLeftVersion).get(i).labelLeftBlock != null
                        && allAutomaticSetBlockPairs.get(currentLeftVersion).get(i).labelRightBlock != null){
                    allCreatedBlockPairsByClicks.get(currentLeftVersion).add(allAutomaticSetBlockPairs.get(currentLeftVersion).get(i));
                    allAutomaticSetBlockPairs.get(currentLeftVersion).remove(i+0);
                    break;
                }
            }

            if(currentLeftVersion+1 == currentInternVersion && Toolkit.blockIsAlreadyInPairWithEdge_atPositionRight(allCreatedBlockPairsByClicks, currentLeftVersion, finalCurrentBlockPosition)){
                currentBlockLabel.setText(
                        getDiffsOfClickedBlocks(
                                currentLeftVersion,
                                Toolkit.getPositionOfLeftBlockRelatedToRightBlockOfSameBlockPair(allCreatedBlockPairsByClicks, currentLeftVersion, finalCurrentBlockPosition),
                                currentLeftVersion+1,
                                finalCurrentBlockPosition,
                                clickedBlockIsInstanceOfTextBlockVersion
                        )
                );
            }

            paintBorderOfBlock(currentBlockLabel, clickedBlockIsInstanceOfTextBlockVersion, blockIsAlreadyInAPair);

            currentBlockLabel.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);

                    boolean blockIsAlreadyInAPair
                            = (currentLeftVersion == currentInternVersion) ?
                            Toolkit.blockIsAlreadyInPairWithEdge_atPositionLeft(allCreatedBlockPairsByClicks, currentLeftVersion, finalCurrentBlockPosition)
                            : Toolkit.blockIsAlreadyInPairWithEdge_atPositionRight(allCreatedBlockPairsByClicks, currentLeftVersion, finalCurrentBlockPosition);

                    if(blockIsAlreadyInAPair){ // clicked block was in a pair. This connection will be deleted

                        for (int j = 0; j < allCreatedBlockPairsByClicks.get(currentLeftVersion).size(); j++) {
                            BlockPair tmpBlockPair = allCreatedBlockPairsByClicks.get(currentLeftVersion).get(j);
                            if (currentLeftVersion == currentInternVersion && tmpBlockPair.leftBlockPosition == finalCurrentBlockPosition
                                    || currentLeftVersion != currentInternVersion && tmpBlockPair.rightBlockPosition == finalCurrentBlockPosition) {
                                paintBorderOfBlock(tmpBlockPair.labelLeftBlock, tmpBlockPair.clickedBlockIsInstanceOfTextBlockVersion, false);
                                paintBorderOfBlock(tmpBlockPair.labelRightBlock, tmpBlockPair.clickedBlockIsInstanceOfTextBlockVersion, false);

                                // if(lastClickedBlock != null) paintBorderOfBlock(lastClickedBlock, lastClickedBlockIsInstanceOfTextBlockVersion, false);

                                borderCurrentBlock[0] = tmpBlockPair.labelRightBlock.getBorder();
                                unmarkLastClickedBlock();

                                allCreatedBlockPairsByClicks.get(currentLeftVersion).remove(tmpBlockPair);

                                tmpBlockPair.labelRightBlock.setText(
                                        "<html><head></head><body>" +
                                        commonmarkMarkUp(
                                            postVersionList.get(Math.max(currentInternVersion, currentLeftVersion+1)).getPostBlocks().get(tmpBlockPair.rightBlockPosition).getContent()
                                        )
                                        + "</body></html>"
                                );

                                // displayCurrentTwoVersionsAndNavigator();
                                paintAllConnectionsBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
                                mainPanel.validate();
                                mainPanel.repaint();
                                break;
                            }
                        }

                    }else{
                        if(lastClickedInternVersion == -1){ // click on a unmarked block
                            lastClickedInternVersion = currentInternVersion;
                            lastClickedPositionOfABlock = finalCurrentBlockPosition;
                            lastClickedBlock = currentBlockLabel;
                            lastClickedBlockIsInstanceOfTextBlockVersion = clickedBlockIsInstanceOfTextBlockVersion;

                            currentBlockLabel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, borderThickness, true));    // https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
                            borderCurrentBlock[0] = currentBlockLabel.getBorder();

                        }else if(lastClickedBlock.equals(currentBlockLabel)){ // second click on same block. Removes temporary saved data. Now another block can be marked.
                            paintBorderOfBlock(currentBlockLabel, clickedBlockIsInstanceOfTextBlockVersion, false);
                            borderCurrentBlock[0] = currentBlockLabel.getBorder();
                            unmarkLastClickedBlock();

                        }else if(lastClickedBlockIsInstanceOfTextBlockVersion == clickedBlockIsInstanceOfTextBlockVersion && lastClickedInternVersion != currentInternVersion){
                            // second click on anouther block. Now those two blocks will be set as linked.
                            BlockPair newBlockPair;

                            if(lastClickedInternVersion < currentInternVersion) {
                                currentBlockLabel.setText(
                                        getDiffsOfClickedBlocks(
                                        lastClickedInternVersion, lastClickedPositionOfABlock,
                                        currentInternVersion, finalCurrentBlockPosition,
                                                clickedBlockIsInstanceOfTextBlockVersion));

                                newBlockPair = new BlockPair(lastClickedBlock, currentBlockLabel, clickedBlockIsInstanceOfTextBlockVersion, lastClickedPositionOfABlock, finalCurrentBlockPosition);
                                // paintOneConnectionBetweenTwoBlocks(lastClickedBlock, currentBlockLabel, clickedBlockIsInstanceOfTextBlockVersion);
                            } else {
                                lastClickedBlock.setText(
                                        getDiffsOfClickedBlocks(
                                        currentInternVersion, finalCurrentBlockPosition,
                                        lastClickedInternVersion, lastClickedPositionOfABlock,
                                                clickedBlockIsInstanceOfTextBlockVersion));

                                newBlockPair = new BlockPair(currentBlockLabel, lastClickedBlock, clickedBlockIsInstanceOfTextBlockVersion, finalCurrentBlockPosition, lastClickedPositionOfABlock);
                                // paintOneConnectionBetweenTwoBlocks(currentBlockLabel, lastClickedBlock, clickedBlockIsInstanceOfTextBlockVersion);
                            }


                            allCreatedBlockPairsByClicks.get(currentLeftVersion).add(newBlockPair);
                            paintAllConnectionsBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);

                            paintBorderOfBlock(newBlockPair.labelLeftBlock, clickedBlockIsInstanceOfTextBlockVersion, true);
                            paintBorderOfBlock(newBlockPair.labelRightBlock, clickedBlockIsInstanceOfTextBlockVersion, true);
                            borderCurrentBlock[0] = currentBlockLabel.getBorder();

                            unmarkLastClickedBlock();

                        }else if(lastClickedBlockIsInstanceOfTextBlockVersion != clickedBlockIsInstanceOfTextBlockVersion){
                            String blockType_currentBlock = String.valueOf((clickedBlockIsInstanceOfTextBlockVersion) ? TextBlockVersion.postBlockTypeId : CodeBlockVersion.postBlockTypeId);
                            String blockType_lastClickedBlock = String.valueOf((lastClickedBlockIsInstanceOfTextBlockVersion) ? TextBlockVersion.postBlockTypeId : CodeBlockVersion.postBlockTypeId);
                            logger.log(
                                    Level.WARNING,
                                    "User tried to link a block of type " + blockType_currentBlock + " in version " + (currentInternVersion+1) + " at position " + (finalCurrentBlockPosition+1)
                                            + " with a block of type " + blockType_lastClickedBlock + " in version " + (lastClickedInternVersion+1) + " at position " + (lastClickedPositionOfABlock+1));
                        }else if(lastClickedInternVersion == currentInternVersion){
                            logger.log(
                                    Level.WARNING,
                                    "User tried to link two blocks at position " + (finalCurrentBlockPosition+1) + " and " + (lastClickedPositionOfABlock+1) + " in version " + (currentInternVersion+1));
                        }else{
                            System.err.println("A case happened that should not happen ...");
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    borderCurrentBlock[0] = currentBlockLabel.getBorder();
                    currentBlockLabel.setBorder(BorderFactory.createLineBorder(Color.GREEN, borderThickness, true));    // https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    currentBlockLabel.setBorder(borderCurrentBlock[0]);
                }
            });

            currentBlockLabel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    paintAllConnectionsBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
                }
            });
        }
    }

/*
    public static void removeEmptyTextAndCodeBlocks(PostVersionList postVersionList){
        if(postVersionList == null)
            return;

        for (PostVersion postVersion : postVersionList) {
            for(TextBlockVersion block : postVersion.getTextBlocks()){
                if (block.getContent().trim().isEmpty()) { // https://stackoverflow.com/a/3745432
                    postVersion.getPostBlocks().remove(block);
                }
            }

            for(CodeBlockVersion block : postVersion.getCodeBlocks()){
                if (block.getContent().trim().isEmpty()) { // https://stackoverflow.com/a/3745432
                    postVersion.getPostBlocks().remove(block);
                }
            }
        }
    }
*/

    private void mergeConsecutiveBlocksOfSameType(){
        if(postVersionList == null)
            return;

        for (PostVersion postVersion : postVersionList) {
            for (int j = 1; j < postVersion.getPostBlocks().size(); j++) {
                if(postVersion.getPostBlocks().get(j-1) instanceof TextBlockVersion && postVersion.getPostBlocks().get(j) instanceof TextBlockVersion){
                    postVersion.getPostBlocks().get(j-1).setContent(
                            postVersion.getPostBlocks().get(j-1).getContent() + "\n" + postVersion.getPostBlocks().get(j).getContent()
                    );
                    postVersion.getPostBlocks().remove(j);
                }
                else
                if(postVersion.getPostBlocks().get(j-1) instanceof CodeBlockVersion && postVersion.getPostBlocks().get(j) instanceof CodeBlockVersion){
                    postVersion.getPostBlocks().get(j-1).setContent(
                            postVersion.getPostBlocks().get(j-1).getContent() + "\n" + postVersion.getPostBlocks().get(j).getContent()
                    );
                    postVersion.getPostBlocks().remove(j);
                }
            }
        }
    }

    private String getDiffsOfClickedBlocks(int leftVersion, int leftBlockPosition, int rightVersion, int rightBlockPosition, boolean blockIsInstanceOfTextBlockVersion){

        String string1 = postVersionList.get(leftVersion).getPostBlocks().get(leftBlockPosition).getContent();
        String string2 = postVersionList.get(rightVersion).getPostBlocks().get(rightBlockPosition).getContent();

        if(string1.trim().equals(string2.trim())){
            return "<html><head></head><body>" +
                    commonmarkMarkUp(
                            string2
                    )
                    + "</body></html>";
        }

        String uniqueLineDiffSeparator_left_start = "§§§§§§1";
        String uniqueLineDiffSeparator_right_start = "§§§§§§2";
        String uniqueLineDiffSeparator_left_end = "§§§§§§3";
        String uniqueLineDiffSeparator_right_end = "§§§§§§4";

        LineDiff lineDiff = new LineDiff();
        List<diff_match_patch.Diff> diffs = lineDiff.diff_lines_only(string1, string2);
        StringBuilder outputRightSb = new StringBuilder();

        for (diff_match_patch.Diff diff : diffs) {

            if (diff.operation == diff_match_patch.Operation.EQUAL) {
                if (!outputRightSb.toString().endsWith("\n"))
                    outputRightSb.append("\n");
                outputRightSb.append(diff.text);

            } else if (diff.operation == diff_match_patch.Operation.DELETE || diff.operation == diff_match_patch.Operation.INSERT) {
                String lineColor = (diff.operation == diff_match_patch.Operation.DELETE) ? "red" : (diff.operation == diff_match_patch.Operation.INSERT) ? "green" : "";

                StringTokenizer tokens = new StringTokenizer(diff.text, "\n");
                while (tokens.hasMoreTokens()) {
                    if (!outputRightSb.toString().endsWith("\n"))
                        outputRightSb.append("\n");
                    String tmpToken = tokens.nextToken();
                    int j = 0;
                    tmpToken = tmpToken.replace("\t", "    ");
                    while (j < tmpToken.length() && (tmpToken.charAt(j) == ' ' || tmpToken.charAt(j) == '\t')) {
                        outputRightSb.append(" ");
                        j++;
                    }
                    outputRightSb
                            .append(uniqueLineDiffSeparator_left_start)
                            .append("<span style=\"color:")
                            .append(lineColor).append("\">")
                            .append(uniqueLineDiffSeparator_left_end)
                            .append(tmpToken.substring(j))
                            .append(uniqueLineDiffSeparator_right_start)
                            .append("</span>")
                            .append(uniqueLineDiffSeparator_right_end)
                    ;
                }
            }

            if (!outputRightSb.toString().endsWith("\n")){
                if(blockIsInstanceOfTextBlockVersion)
                    outputRightSb.append("\n\n");
                else
                    outputRightSb.append("\n");
            }
        }


        outputRightSb = new StringBuilder("<html><head></head><body>" +
                commonmarkMarkUp(
                        outputRightSb.toString()
                )
                + "</body></html>");



        String output = outputRightSb.toString();


        Pattern pattern_left = Pattern.compile(uniqueLineDiffSeparator_left_start + ".*" + uniqueLineDiffSeparator_left_end);
        Matcher matcher_left = pattern_left.matcher(output);

        while(matcher_left.find()) {
            String newStringLeft = matcher_left.group();
            newStringLeft = newStringLeft.replace("&lt;", "<");
            newStringLeft = newStringLeft.replace("&gt;", ">");
            newStringLeft = newStringLeft.replace("&quot;", "\"");

            output = output.replace(matcher_left.group(), newStringLeft);
        }



        Pattern pattern_right = Pattern.compile(uniqueLineDiffSeparator_right_start + ".*" + uniqueLineDiffSeparator_right_end);
        Matcher matcher_right = pattern_right.matcher(output);

        while(matcher_right.find()) {
            String newStringRight = matcher_right.group();

            newStringRight = newStringRight.replace("&lt;", "<");
            newStringRight = newStringRight.replace("&gt;", ">");
            newStringRight = newStringRight.replace("&quot;", "\"");

            output = output.replace(matcher_right.group(), newStringRight);
        }




        output = output.replace(uniqueLineDiffSeparator_left_start, "");
        output = output.replace(uniqueLineDiffSeparator_left_end, "");
        output = output.replace(uniqueLineDiffSeparator_right_start, "");
        output = output.replace(uniqueLineDiffSeparator_right_end, "");

        return output;
    }

    // displays all blocks of two versions and current linking
    void displayCurrentTwoVersionsAndNavigator(){

        displayAllBlocksOfOneVersionAndSetMouseListeners(versionLeftPanel, true);

        if(postVersionList != null && postVersionList.size() > 1)
            displayAllBlocksOfOneVersionAndSetMouseListeners(versionRightPanel, false);

        navigatorAtBottomLabel.updateNavigatorText();

        paintAllConnectionsBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
    }


    private void paintBorderOfBlock(JLabel block, boolean blockIsOfInstanceText, boolean blockIsAlreadyMarkedWithBorder){
        Color blockBorderColor = null;

        if(blockIsOfInstanceText && blockIsAlreadyMarkedWithBorder){
            blockBorderColor = colorTextBlockMarked;  // markierter Textblock
        }else if (blockIsOfInstanceText && !blockIsAlreadyMarkedWithBorder){
            blockBorderColor = colorTextBlockUnMarked;  // nichtmarkierter TextBlock
        }else if (!blockIsOfInstanceText && blockIsAlreadyMarkedWithBorder){
            blockBorderColor = colorCodeBlockMarked;  // markierter CodeBlock
        }else if (!blockIsOfInstanceText && !blockIsAlreadyMarkedWithBorder){
            blockBorderColor = colorCodeBlockUnMarked;  // nicht markierter CodeBlock
        }

        block.setBorder(BorderFactory.createLineBorder(blockBorderColor, borderThickness, true)); // https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
    }


    private void paintOneEdgeBetweenTwoBlocks(JLabel leftBlock, JLabel rightBlock, Boolean clickedBlockIsInstanceOfTextBlockVersion){
        Graphics tmpGraphics = mainPanel.getGraphics();
        tmpGraphics.setColor(clickedBlockIsInstanceOfTextBlockVersion == null ? Color.LIGHT_GRAY : clickedBlockIsInstanceOfTextBlockVersion ? colorTextBlockMarked : colorCodeBlockMarked);

        tmpGraphics.fillOval(
                versionEdgesPanel.getX() + 0,
                leftBlock.getY() + buttonsAtTopPanel.getHeight() + leftBlock.getHeight()/2,
                10,
                10);

        tmpGraphics.fillOval(
                versionEdgesPanel.getX() + versionEdgesPanel.getWidth() - 10,
                rightBlock.getY() + buttonsAtTopPanel.getHeight() + rightBlock.getHeight()/2,
                10,
                10);

        tmpGraphics.drawLine(
                versionEdgesPanel.getX() + 0,
                leftBlock.getY() + buttonsAtTopPanel.getHeight() + leftBlock.getHeight() / 2 + borderThickness,
                versionEdgesPanel.getX() + versionEdgesPanel.getWidth(),
                rightBlock.getY() + buttonsAtTopPanel.getHeight() + rightBlock.getHeight() / 2 + borderThickness);
    }

    private void paintOneFilmBetweenTwoBlocks(JLabel leftBlock, JLabel rightBlock, Boolean clickedBlockIsInstanceOfTextBlockVersion){
        Graphics tmpGraphics = mainPanel.getGraphics();
        tmpGraphics.setColor(clickedBlockIsInstanceOfTextBlockVersion == null ? Color.LIGHT_GRAY : clickedBlockIsInstanceOfTextBlockVersion ? colorTextBlockMarked : colorCodeBlockMarked);

        film.reset();
        film.addPoint(versionEdgesPanel.getX(),                                versionLeftPanel.getComponent(0).getY() + buttonsAtTopPanel.getHeight() + leftBlock.getY());
        film.addPoint(versionEdgesPanel.getX(),                                versionLeftPanel.getComponent(0).getY() + buttonsAtTopPanel.getHeight() + leftBlock.getY() + leftBlock.getHeight());
        film.addPoint(versionEdgesPanel.getX() + versionEdgesPanel.getWidth(), versionRightPanel.getComponent(0).getY() + buttonsAtTopPanel.getHeight() + rightBlock.getY() + rightBlock.getHeight());
        film.addPoint(versionEdgesPanel.getX() + versionEdgesPanel.getWidth(), versionRightPanel.getComponent(0).getY() + buttonsAtTopPanel.getHeight() + rightBlock.getY());

        tmpGraphics.fillPolygon(film);
    }

    private void paintOneConnectionBetweenTwoBlocks(JLabel leftBlock, JLabel rightBlock, Boolean clickedBlockIsInstanceOfTextBlockVersion){
        switch (linkConnectionDisplayMode){
            case edges:
                paintOneEdgeBetweenTwoBlocks(leftBlock, rightBlock, clickedBlockIsInstanceOfTextBlockVersion);
                break;

            case films:
                paintOneFilmBetweenTwoBlocks(leftBlock, rightBlock, clickedBlockIsInstanceOfTextBlockVersion);
                break;
        }
    }

    private void clearPanelFromAllConnectionsBetweenBlocks(){
        Graphics tmpGraphics = versionEdgesPanel.getGraphics();

        if(tmpGraphics == null)
            return;

        tmpGraphics.setColor(Color.LIGHT_GRAY);
        tmpGraphics.fillRect(0, 0, versionEdgesPanel.getWidth(), versionEdgesPanel.getHeight());
    }

    void paintAllConnectionsBetweenClickedBlocksOfCurrentTwoVersions(int versionNumber){
        if(postVersionList != null) {
            clearPanelFromAllConnectionsBetweenBlocks();
            for (BlockPair tmpBlockPair : allCreatedBlockPairsByClicks.get(versionNumber)) {
                paintOneConnectionBetweenTwoBlocks(tmpBlockPair.labelLeftBlock, tmpBlockPair.labelRightBlock, tmpBlockPair.clickedBlockIsInstanceOfTextBlockVersion);
            }
            buttonsAtTopPanel.paintCommentPanel();
        }
    }

    /***** functional stuff *****/
    void unmarkLastClickedBlock(){
        lastClickedInternVersion = -1;
        lastClickedPositionOfABlock = -1;
        lastClickedBlock = null;
    }

    private void setListenersToFrameAndPanel(){

        this.addComponentListener(new ComponentAdapter() { // https://stackoverflow.com/questions/2303305/window-resize-event
            @Override
            public void componentResized(ComponentEvent e) { // TODO: Connections are not redrawn on resize
                super.componentResized(e);
                paintAllConnectionsBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
                GroundTruthCreator.WIDTH = e.getComponent().getWidth();
                GroundTruthCreator.HEIGHT = e.getComponent().getHeight();
            }

            @Override
            public void componentMoved(ComponentEvent e){
                super.componentMoved(e);
                GroundTruthCreator.LOCATION = e.getComponent().getLocation();
            }
        });

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                    (buttonsAtTopPanel).actionButtonNext();
                }else if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    (buttonsAtTopPanel).actionButtonBack();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                    (buttonsAtTopPanel).actionButtonNext();
                }else if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    (buttonsAtTopPanel).actionButtonBack();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        scrollPaneIncludingMainPanel.getVerticalScrollBar().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                paintAllConnectionsBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
            }
        });

        scrollPaneIncludingMainPanel.getHorizontalScrollBar().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                paintAllConnectionsBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
            }
        });

        scrollPaneIncludingMainPanel.addMouseWheelListener(e -> paintAllConnectionsBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion));
    }

    private String printBlockLifeSpans(){

        StringBuilder output = new StringBuilder();

        int numberOfSnapshots_text = 0;
        int numberOfSnapshots_code = 0;

        int numberOfBlockLifeSpans_text = 0;
        int numberOfBlockLifeSpans_code = 0;

        output.append("Format: (Version, Position)" + "\n");

        for (PostBlockLifeSpan blockLifeSpansCreatedByClick : blockLifeSpansExtractedFromClicks) {
            if (blockLifeSpansCreatedByClick.getPostBlockTypeId() == TextBlockVersion.postBlockTypeId) {
                numberOfSnapshots_text += blockLifeSpansCreatedByClick.size();
                numberOfBlockLifeSpans_text++;
            } else if (blockLifeSpansCreatedByClick.getPostBlockTypeId() == CodeBlockVersion.postBlockTypeId) {
                numberOfSnapshots_code += blockLifeSpansCreatedByClick.size();
                numberOfBlockLifeSpans_code++;
            }
            output.append(blockLifeSpansCreatedByClick);
        }


        // show other statistics
        output.append("\n" + "----------------------------------------------------------" + "\n");
        output.append("number of text snapshots: ").append(numberOfSnapshots_text).append("\n");
        output.append("number of text block life spans: ").append(numberOfBlockLifeSpans_text).append("\n");
        output.append("\n");
        output.append("number of code snapshots: ").append(numberOfSnapshots_code).append("\n");
        output.append("number of code block life spans: ").append(numberOfBlockLifeSpans_code).append("\n");
        output.append("###########################################################" + "\n");
        output.append("\n");

        return output.toString();
    }

    void exportBlockLinksToCSV(Path outputPath){

        LinkedList<String> comments = buttonsAtTopPanel.comments;

        for (String commentLine : comments) {
            StringTokenizer tokens = new StringTokenizer(commentLine, "|");

            int tmpVersion = Integer.parseInt(tokens.nextToken().replaceAll("\\s", "").replace("vers:", ""));
            int tmpPosition = Integer.parseInt(tokens.nextToken().replaceAll("\\s", "").replace("pos:", ""));
            String tmpComment = tokens.nextToken().replace("</font>", "").replaceAll("<font.*>", "");

            for (PostBlockLifeSpan blockLifeSpan : blockLifeSpansExtractedFromClicks) {
                for (PostBlockLifeSpanVersion blockLifeSpanSnapshot : blockLifeSpan) {
                    if (postHistoryIdToVersion.get(blockLifeSpanSnapshot.getPostHistoryId()) == tmpVersion && blockLifeSpanSnapshot.getLocalId() == tmpPosition) {
                        if(blockLifeSpanSnapshot.getComment().isEmpty())
                            blockLifeSpanSnapshot.setComment(tmpComment);
                        else
                            blockLifeSpanSnapshot.setComment(blockLifeSpanSnapshot.getComment() + ". " + tmpComment);
                    }
                }
            }
        }

        for (PostBlockLifeSpan blockLifeSpan : blockLifeSpansExtractedFromClicks) {
            for (int j = 0; j < blockLifeSpan.size(); j++) {

                if (j > 0)
                    blockLifeSpan.get(j).setPredLocalId(blockLifeSpan.get(j - 1).getLocalId());

                if (j < blockLifeSpan.size() - 1)
                    blockLifeSpan.get(j).setSuccLocalId(blockLifeSpan.get(j + 1).getLocalId());
            }
        }

        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(outputPath.toString()), CSVFormat.DEFAULT
                .withHeader("PostId", "PostHistoryId", "PostBlockTypeId", "LocalId", "PredLocalId", "SuccLocalId", "Comment")
                .withDelimiter(';')
                .withQuote('"')
                .withQuoteMode(QuoteMode.MINIMAL) // TODO: Adjust with right quote mode
                .withEscape('\\')
                .withNullString("null"))) {

            for (PostBlockLifeSpan blockLifeSpan : blockLifeSpansExtractedFromClicks) {

                for (PostBlockLifeSpanVersion postBlockLifeSpan : blockLifeSpan) {
                    csvPrinter.printRecord(
                            blockLifeSpan.getFirst().getPostId(),
                            postBlockLifeSpan.getPostHistoryId(),
                            blockLifeSpan.getPostBlockTypeId(),
                            postBlockLifeSpan.getLocalId(),
                            postBlockLifeSpan.getPredLocalId(),
                            postBlockLifeSpan.getSuccLocalId(),
                            postBlockLifeSpan.getComment()
                    );
                }
            }

            csvPrinter.flush();
            csvPrinter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String commonmarkMarkUp(String markdownText){   // https://github.com/atlassian/commonmark-java
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private void collectAllBlockPairsWithEqualContent(){
        if(postVersionList != null) {

            for(int i=0; i<postVersionList.size()-1; i++){
                for(int j=0; j<postVersionList.get(i).getPostBlocks().size(); j++){

                    String contentLeft = postVersionList.get(i).getPostBlocks().get(j).getContent();
                    boolean skip = false;

                    for(int k=0; k<postVersionList.get(i).getPostBlocks().size(); k++){
                        if(k == j)
                            continue;

                        String anotherContentLeft = postVersionList.get(i).getPostBlocks().get(k).getContent();
                        if(contentLeft.equals(anotherContentLeft))
                            skip = true;
                    }

                    if(skip)
                        continue;

                    int positionOfRightBlockWithEqualContent = -1;
                    int numberOfBlocksWithEqualContent = 0;
                    for(int k=0; k<postVersionList.get(i+1).getPostBlocks().size(); k++){
                        String contentRight = postVersionList.get(i+1).getPostBlocks().get(k).getContent();
                        if(postVersionList.get(i).getPostBlocks().get(j) instanceof TextBlockVersion
                                == postVersionList.get(i+1).getPostBlocks().get(k) instanceof TextBlockVersion
                                && (contentRight.trim().equals(contentLeft.trim()))){
                            numberOfBlocksWithEqualContent++;
                            positionOfRightBlockWithEqualContent = k;
                            if(numberOfBlocksWithEqualContent>1){
                                positionOfRightBlockWithEqualContent = -1;
                                break;
                            }
                        }
                    }

                    if(numberOfBlocksWithEqualContent == 1 && positionOfRightBlockWithEqualContent != -1){
                        allAutomaticSetBlockPairs.get(i).add(new BlockPair(
                                null,
                                null,
                                postVersionList.get(i).getPostBlocks().get(j) instanceof TextBlockVersion,
                                j,
                                positionOfRightBlockWithEqualContent));
                    }
                }
            }
        }
    }

}
