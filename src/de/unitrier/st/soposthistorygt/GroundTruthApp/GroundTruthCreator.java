package de.unitrier.st.soposthistorygt.GroundTruthApp;

import de.unitrier.st.soposthistorygt.util.BlockLifeSpan;
import de.unitrier.st.soposthistorygt.util.BlockLifeSpanSnapshot;
import de.unitrier.st.soposthistorygt.util.anchorsURLs.AnchorTextAndUrlHandler;
import de.unitrier.st.soposthistorygt.util.anchorsURLs.AnchorTextAndUrlPair;
import de.unitrier.st.soposthistory.blocks.PostBlockVersion;
import de.unitrier.st.soposthistory.blocks.TextBlockVersion;
import de.unitrier.st.soposthistory.diffs.LineDiff;
import de.unitrier.st.soposthistory.diffs.diff_match_patch;
import de.unitrier.st.soposthistory.version.PostVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;
import net.miginfocom.swing.MigLayout;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

import static de.unitrier.st.soposthistory.history.PostHistoryIterator.logger;


class GroundTruthCreator implements Runnable{


    /***** Swing components *****/
    JFrame frame = new JFrame("Ground Truth Creator");

    JPanel mainPanel = new JPanel(new BorderLayout());
    private JScrollPane scrollPaneIncludingMainPanel = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    private JPanel buttonsAtTopPanel = null;

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


    /***** Intern variables *****/
    int currentLeftVersion = 0;

    final static String path = "postVersionLists";

    private int lastClickedInternVersion = -1;
    private int lastClickedPositionOfABlock = -1;
    private JLabel lastClickedBlock = null;
    private boolean lastClickedBlockIsInstanceOfTextBlockVersion;

    AnchorTextAndUrlHandler anchorTextAndUrlHandler = new AnchorTextAndUrlHandler();

    Vector<Vector<BlockPair>> allCreatedBlockPairsByClicks = new Vector<>();
    Vector<BlockLifeSpan> blockLifeSpansExtractedFromClicks = new Vector<>();


    /***** Constructor arguments *****/
    PostVersionList postVersionList;
    static int WIDTH;
    static int HEIGHT;
    static Point LOCATION;



    /***** Constructor *****/
    GroundTruthCreator(PostVersionList postVersionList, int initialWidth, int initialHeight, Point initialLocation){

        this.postVersionList = postVersionList;
        WIDTH = initialWidth;
        HEIGHT = initialHeight;
        LOCATION = initialLocation;

        if(postVersionList != null)
            for(int i=0; i<postVersionList.size(); i++){
                allCreatedBlockPairsByClicks.add(new Vector<>());
            }

        frame.setSize(new Dimension(initialWidth, initialHeight));


        buttonsAtTopPanel = new ButtonsAndInstructionsPanel(this);
        mainPanel.add(buttonsAtTopPanel, BorderLayout.NORTH);

        compareVersionsPanel = displayPlainTwoVersionsWithoutBlocks();
        mainPanel.add(compareVersionsPanel);

        navigatorAtBottomLabel = new NavigatorLabel(this);
        mainPanel.add(navigatorAtBottomLabel, BorderLayout.SOUTH);

        mainPanel.setBackground(Color.BLACK);

        removeTextBlocksThatWillBeEmptyAfterRenderingWithHTML();

        setListenersToFrameAndPanel();

        ((ButtonsAndInstructionsPanel)buttonsAtTopPanel).setEnablingOfNextAndBackButton();

        displayCurrentTwoVersionsAndNavigator();

        scrollPaneIncludingMainPanel.getVerticalScrollBar().setUnitIncrement(16);   // https://stackoverflow.com/a/5583571

        frame.add(scrollPaneIncludingMainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        if(initialLocation == null)
            frame.setLocationRelativeTo(null);
        else
            frame.setLocation(initialLocation);
        frame.setVisible(true);
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

        versionEdgesPanel.setMinimumSize(new Dimension(frame.getWidth()*10/100, compareVersionsPanel.getHeight()));

        return compareVersionsPanel;
    }

    // displays all blocks of one version
    private void displayAllBlocksOfOneVersionAndSetMouseListeners(JPanel versionLeftOrRight, boolean isVersionLeft){
        versionLeftOrRight.removeAll();
        int currentInternVersion = isVersionLeft ? currentLeftVersion : currentLeftVersion + 1;
        if(postVersionList == null)return;

        List<PostBlockVersion> blocks = postVersionList.get(currentInternVersion).getPostBlocks();
        StringBuilder textBlocksConcatenated = new StringBuilder();

        for (PostBlockVersion block : blocks) {
            if (block instanceof TextBlockVersion)
                textBlocksConcatenated.append(block.getContent());
        }

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

            currentBlockLabel.addMouseMotionListener(new MouseMotionAdapter() { // workaround to repaint
                @Override
                public void mouseMoved(MouseEvent e) {
                    paintAllEdgesBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
                }
            });

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
                                paintBorderOfBlock(tmpBlockPair.labelLeftBlock, clickedBlockIsInstanceOfTextBlockVersion, false);
                                paintBorderOfBlock(tmpBlockPair.labelRightBlock, clickedBlockIsInstanceOfTextBlockVersion, false);

                                borderCurrentBlock[0] = tmpBlockPair.labelRightBlock.getBorder();
                                unmarkLastClickedBlock();

                                allCreatedBlockPairsByClicks.get(currentLeftVersion).remove(tmpBlockPair);

                                displayCurrentTwoVersionsAndNavigator();
                                break;
                            }
                        }

                    }else{
                        if(lastClickedInternVersion == -1){ // click on a unmarked block
                            lastClickedInternVersion = currentInternVersion;
                            lastClickedPositionOfABlock = finalCurrentBlockPosition;
                            lastClickedBlock = currentBlockLabel;
                            lastClickedBlockIsInstanceOfTextBlockVersion = clickedBlockIsInstanceOfTextBlockVersion;

                            currentBlockLabel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 5, true));    // https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
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
                                // paintOneEdgeBetweenTwoBlocks(lastClickedBlock, currentBlockLabel, clickedBlockIsInstanceOfTextBlockVersion);
                            } else {
                                lastClickedBlock.setText(
                                        getDiffsOfClickedBlocks(
                                        currentInternVersion, finalCurrentBlockPosition,
                                        lastClickedInternVersion, lastClickedPositionOfABlock,
                                                clickedBlockIsInstanceOfTextBlockVersion));

                                newBlockPair = new BlockPair(currentBlockLabel, lastClickedBlock, clickedBlockIsInstanceOfTextBlockVersion, finalCurrentBlockPosition, lastClickedPositionOfABlock);
                                // paintOneEdgeBetweenTwoBlocks(currentBlockLabel, lastClickedBlock, clickedBlockIsInstanceOfTextBlockVersion);
                            }

/*
                            for(BlockPair tmpBlockPair : allCreatedBlockPairsByClicks.get(currentLeftVersion)){ // TODO: this could be superflous now. Check this.
                                if(tmpBlockPair.equals(newBlockPair))
                                    return;
                            }
*/

                            allCreatedBlockPairsByClicks.get(currentLeftVersion).add(newBlockPair);

                            paintBorderOfBlock(newBlockPair.labelLeftBlock, clickedBlockIsInstanceOfTextBlockVersion, true);
                            paintBorderOfBlock(newBlockPair.labelRightBlock, clickedBlockIsInstanceOfTextBlockVersion, true);
                            borderCurrentBlock[0] = currentBlockLabel.getBorder();

                            unmarkLastClickedBlock();
                        }else if(lastClickedBlockIsInstanceOfTextBlockVersion != clickedBlockIsInstanceOfTextBlockVersion){
                            String blockType_currentBlock = String.valueOf((clickedBlockIsInstanceOfTextBlockVersion) ? BlockLifeSpan.Type.textblock : BlockLifeSpan.Type.codeblock);
                            String blockType_lastClickedBlock = String.valueOf((lastClickedBlockIsInstanceOfTextBlockVersion) ? BlockLifeSpan.Type.textblock : BlockLifeSpan.Type.codeblock);
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
                    currentBlockLabel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5, true));    // https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    currentBlockLabel.setBorder(borderCurrentBlock[0]);
                }
            });
        }

    }



    private void removeTextBlocksThatWillBeEmptyAfterRenderingWithHTML(){
        if(postVersionList == null)
            return;

        for (PostVersion postVersion : postVersionList) {
            String textBlocksConcatenated = postVersion.getMergedTextBlockContent();
            Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(textBlocksConcatenated);

            for(TextBlockVersion textBlock : postVersion.getTextBlocks()){
                String markdownText = textBlock.getContent();
                markdownText = anchorTextAndUrlHandler.normalizeAnchorsRefsAndURLsForApp(markdownText, anchorTextAndUrlPairs);

                if (markdownText.trim().isEmpty()) { // https://stackoverflow.com/a/3745432
                    System.out.println(postVersion.getTextBlocks().size());
                    postVersion.getPostBlocks().remove(textBlock);
                    System.out.println(postVersion.getTextBlocks().size());
                }else
                    textBlock.setContent(markdownText);
            }
        }
    }


    private String getDiffsOfClickedBlocks(int leftVersion, int leftBlockPosition, int rightVersion, int rightBlockPosition, boolean blockIsInstanceOfTextBlockVersion){

        String string1 = postVersionList.get(leftVersion).getPostBlocks().get(leftBlockPosition).getContent();
        String string2 = postVersionList.get(rightVersion).getPostBlocks().get(rightBlockPosition).getContent();

        String uniqueLineDiffSeparator_left = "$$$$$$";
        String uniqueLineDiffSeparator_right = "§§§§§§";

        LineDiff lineDiff = new LineDiff();
        List<diff_match_patch.Diff> diffs = lineDiff.diff_lines_only(string1, string2);
        StringBuilder outputRight = new StringBuilder();

        for (diff_match_patch.Diff diff : diffs) {

            if (diff.operation == diff_match_patch.Operation.EQUAL) {
                if (!outputRight.toString().endsWith("\n"))
                    outputRight.append("\n");
                outputRight.append(diff.text);

            } else if (diff.operation == diff_match_patch.Operation.DELETE || diff.operation == diff_match_patch.Operation.INSERT) {
                String lineColor = (diff.operation == diff_match_patch.Operation.DELETE) ? "red" : (diff.operation == diff_match_patch.Operation.INSERT) ? "green" : "";

                StringTokenizer tokens = new StringTokenizer(diff.text, "\n");
                while (tokens.hasMoreTokens()) {
                    if (!outputRight.toString().endsWith("\n"))
                        outputRight.append("\n");
                    String tmpToken = tokens.nextToken();
                    int j = 0;
                    while (j < tmpToken.length() && tmpToken.charAt(j) == ' ') {
                        outputRight.append(" ");
                        j++;
                    }
                    outputRight.append(uniqueLineDiffSeparator_left).append("<span style=\"color:").append(lineColor).append("\">").append(tmpToken.substring(j)).append("</span>");
                }
            }

            if (!outputRight.toString().endsWith("\n")){
                if(blockIsInstanceOfTextBlockVersion)
                    outputRight.append("\n\n");
                else
                    outputRight.append("\n");
            }
        }


        outputRight = new StringBuilder("<html><head></head><body>" +
                commonmarkMarkUp(
                        outputRight.toString()
                )
                + "</body></html>");

        boolean inSpansToReplace = false;
        StringBuilder stringToBeReplaced = new StringBuilder();
        for(int i=0; i<outputRight.length(); i++){
            if(outputRight.substring(i).startsWith(uniqueLineDiffSeparator_left)){
                inSpansToReplace = true;
            } else if(outputRight.substring(i).endsWith(uniqueLineDiffSeparator_right)){
                inSpansToReplace = false;
            }

            if(inSpansToReplace) {
                stringToBeReplaced.append(outputRight.charAt(i));
            }
        }

        String replacingString = stringToBeReplaced.toString();
        replacingString = replacingString.replace("&quot;", "\"");
        replacingString = replacingString.replace("&lt;", "<");
        replacingString = replacingString.replace("&gt;", ">");

        replacingString = replacingString.replace(uniqueLineDiffSeparator_left, "");
        replacingString = replacingString.replace(uniqueLineDiffSeparator_right, "");

        outputRight = new StringBuilder(outputRight.toString().replace(stringToBeReplaced.toString(), replacingString));

        return outputRight.toString();
    }

    // displays all blocks of two versions and current linking
    void displayCurrentTwoVersionsAndNavigator(){

        displayAllBlocksOfOneVersionAndSetMouseListeners(versionLeftPanel, true);

        if(postVersionList != null && postVersionList.size() > 1)
            displayAllBlocksOfOneVersionAndSetMouseListeners(versionRightPanel, false);

        navigatorAtBottomLabel.updateNavigatorText();

        paintAllEdgesBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
        //frame.repaint(); // necessary because a version could only be repainted simple so that edges from older comparings could remain
    }

    private void paintBorderOfBlock(JLabel block, boolean blockIsOfInstanceText, boolean blockIsAlreadyMarkedWithBoarder){
        Color blockBoarderColor = null;

        if(blockIsOfInstanceText && blockIsAlreadyMarkedWithBoarder){
            blockBoarderColor = colorTextBlockMarked;  // markierter Textblock
        }else if (blockIsOfInstanceText && !blockIsAlreadyMarkedWithBoarder){
            blockBoarderColor = colorTextBlockUnMarked;  // nichtmarkierter TextBlock
        }else if (!blockIsOfInstanceText && blockIsAlreadyMarkedWithBoarder){
            blockBoarderColor = colorCodeBlockMarked;  // markierter CodeBlock
        }else if (!blockIsOfInstanceText && !blockIsAlreadyMarkedWithBoarder){
            blockBoarderColor = colorCodeBlockUnMarked;  // nicht markierter CodeBlock
        }

        block.setBorder(BorderFactory.createLineBorder(blockBoarderColor, 5, true)); // https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
    }

    private void paintOneEdgeBetweenTwoBlocks(JLabel leftBlock, JLabel rightBlock, boolean clickedBlockIsInstanceOfTextBlockVersion){
        Graphics tmpGraphics = mainPanel.getGraphics();
        tmpGraphics.setColor(clickedBlockIsInstanceOfTextBlockVersion ? colorTextBlockMarked : colorCodeBlockMarked);

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
                leftBlock.getY() + buttonsAtTopPanel.getHeight() + leftBlock.getHeight() / 2 + 5,
                versionEdgesPanel.getX() + versionEdgesPanel.getWidth(),
                rightBlock.getY() + buttonsAtTopPanel.getHeight() + rightBlock.getHeight() / 2 + 5);
    }

    private void paintAllEdgesBetweenClickedBlocksOfCurrentTwoVersions(int versionNumber){
        if(postVersionList != null)
            for (BlockPair tmpBlockPair : allCreatedBlockPairsByClicks.get(versionNumber)){
                paintOneEdgeBetweenTwoBlocks(tmpBlockPair.labelLeftBlock, tmpBlockPair.labelRightBlock, tmpBlockPair.clickedBlockIsInstanceOfTextBlockVersion);
            }
    }


    /***** functional stuff *****/
    void unmarkLastClickedBlock(){
        lastClickedInternVersion = -1;
        lastClickedPositionOfABlock = -1;
        lastClickedBlock = null;
    }

    private void setListenersToFrameAndPanel(){

        frame.addComponentListener(new ComponentAdapter() { // https://stackoverflow.com/questions/2303305/window-resize-event
            @Override
            public void componentResized(ComponentEvent e) { // TODO: This works but not so well. Is there a foolproof solution for this?
                super.componentResized(e);
                paintAllEdgesBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
                GroundTruthCreator.WIDTH = e.getComponent().getWidth();
                GroundTruthCreator.HEIGHT = e.getComponent().getHeight();
            }

            @Override
            public void componentMoved(ComponentEvent e){
                super.componentMoved(e);
                GroundTruthCreator.LOCATION = e.getComponent().getLocation();
            }
        });

        frame.setFocusable(true);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                    ((ButtonsAndInstructionsPanel)buttonsAtTopPanel).actionButtonNext();
                }else if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    ((ButtonsAndInstructionsPanel)buttonsAtTopPanel).actionButtonBack();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                    ((ButtonsAndInstructionsPanel)buttonsAtTopPanel).actionButtonNext();
                }else if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    ((ButtonsAndInstructionsPanel)buttonsAtTopPanel).actionButtonBack();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                paintAllEdgesBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
            }
        });

        buttonsAtTopPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                paintAllEdgesBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
            }
        });

        scrollPaneIncludingMainPanel.getVerticalScrollBar().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                paintAllEdgesBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
            }
        });

        scrollPaneIncludingMainPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                paintAllEdgesBetweenClickedBlocksOfCurrentTwoVersions(currentLeftVersion);
            }
        });
    }

    // TODO: write a script which reads all saved files and compares them with metrics
    // TODO for Sebastian: Is this ok? Saving each post version list in a separate file instead of saving them in one single file
    void writeFileOfPostVersionList(){
        try {
            File file = new File(path + "/completed_" + blockLifeSpansExtractedFromClicks.firstElement().firstElement().getPostId() + ".csv");
            if(file.exists())
                logger.log(Level.INFO, "File already exists. File with id=" + blockLifeSpansExtractedFromClicks.firstElement().firstElement().getPostId() + " will be overwritten.");

            PrintWriter pw = new PrintWriter(file);
            pw.write(exportBlockLinksToCSV());

            pw.flush();
            pw.close();

        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "Failed to create or to write file " + path);
        }
    }

    private String printBlockLifeSpans(){

        StringBuilder output = new StringBuilder();

        int numberOfSnapshots_text = 0;
        int numberOfSnapshots_code = 0;

        int numberOfBlockLifeSpans_text = 0;
        int numberOfBlockLifeSpans_code = 0;

        output.append("Format: (Version, Position)" + "\n");

        for (BlockLifeSpan blockLifeSpansCreatedByClick : blockLifeSpansExtractedFromClicks) {
            if (blockLifeSpansCreatedByClick.getType() == BlockLifeSpan.Type.textblock) {
                numberOfSnapshots_text += blockLifeSpansCreatedByClick.size();
                numberOfBlockLifeSpans_text++;
            } else if (blockLifeSpansCreatedByClick.getType() == BlockLifeSpan.Type.codeblock) {
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

    private String exportBlockLinksToCSV(){
        StringBuilder output = new StringBuilder("PostId; PostHistoryId; PostBlockTypeId; LocalId; PredLocalId; SuccLocalId; Comment" + "\n");

        Vector<String> comments = ((ButtonsAndInstructionsPanel)buttonsAtTopPanel).comments;

        for (String commentLine : comments) {
            StringTokenizer tokens = new StringTokenizer(commentLine, "|");

            int tmpVersion = Integer.parseInt(tokens.nextToken().replaceAll("\\s", "").replace("vers:", ""));
            int tmpPosition = Integer.parseInt(tokens.nextToken().replaceAll("\\s", "").replace("pos:", ""));
            String tmpComment = tokens.nextToken().replace("</font>", "").replaceAll("<font.*>", "");

            for (BlockLifeSpan blockLifeSpan : blockLifeSpansExtractedFromClicks) {
                for (BlockLifeSpanSnapshot blockLifeSpanSnapshot : blockLifeSpan) {
                    if (blockLifeSpanSnapshot.getVersion() == tmpVersion && blockLifeSpanSnapshot.getLocalId() == tmpPosition) {
                        blockLifeSpanSnapshot.setComment(tmpComment);
                    }
                }
            }
        }

        for (BlockLifeSpan blockLifeSpan : blockLifeSpansExtractedFromClicks) {
            for (int j = 0; j < blockLifeSpan.size(); j++) {

                if (j > 0)
                    blockLifeSpan.get(j).setPredLocalId(blockLifeSpan.get(j - 1).getLocalId());

                if (j < blockLifeSpan.size() - 1)
                    blockLifeSpan.get(j).setSuccLocalId(blockLifeSpan.get(j + 1).getLocalId());
            }
        }

        for (BlockLifeSpan blockLifeSpan : blockLifeSpansExtractedFromClicks) {
            for (int j = 0; j < blockLifeSpan.size(); j++) {
                output.append("\"").append(blockLifeSpan.firstElement().getPostId()).append("\"").append("; ");
                output.append("\"").append(blockLifeSpan.get(j).getPostHistoryId()).append("\"").append("; ");
                output.append("\"").append(blockLifeSpan.getType() == BlockLifeSpan.Type.textblock ? 1 : 2).append("\"").append("; ");
                output.append("\"").append(blockLifeSpan.get(j).getLocalId()).append("\"").append("; ");
                output.append("\"").append(blockLifeSpan.get(j).getPredLocalId()).append("\"").append("; ");
                output.append("\"").append(blockLifeSpan.get(j).getSuccLocalId()).append("\"").append("; ");
                output.append("\"").append(blockLifeSpan.get(j).getComment()).append("\"").append("; ");
                output.append("\n");
            }
        }

        return output.toString();
    }

    private String commonmarkMarkUp(String markdownText){   // https://github.com/atlassian/commonmark-java
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    @Override
    public void run(){
        while(true){
            Robot bot = null;
            try {
                Thread.sleep(200);
                bot = new Robot();
            } catch (AWTException | InterruptedException e) {
                e.printStackTrace();
            }
            assert bot != null;
            bot.mouseMove(MouseInfo.getPointerInfo().getLocation().x+1, MouseInfo.getPointerInfo().getLocation().y+1);
            bot.mouseMove(MouseInfo.getPointerInfo().getLocation().x-1, MouseInfo.getPointerInfo().getLocation().y-1);
        }
    }
}
