package de.unitrier.st.soposthistory.gt.GroundTruthApp;

import de.unitrier.st.soposthistory.blocks.CodeBlockVersion;
import de.unitrier.st.soposthistory.blocks.TextBlockVersion;
import de.unitrier.st.soposthistory.util.PostBlockLifeSpan;
import de.unitrier.st.soposthistory.util.PostBlockLifeSpanVersion;
import de.unitrier.st.soposthistory.gt.util.GTLogger;
import de.unitrier.st.soposthistory.version.PostVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

class ButtonsAndInstructionsPanel extends JPanel {

    private GroundTruthCreator groundTruthCreator;

    /***** Swing components *****/
    private JButton buttonRequestSpecialPost = new JButton("request post");
    private JTextField textFieldRequestSpecialPost = new JTextField("");
    private JButton buttonRequestRandomPost = new JButton("random post");
    private JButton buttonLoadPost = new JButton("load post");
    private JButton buttonSaveAllAndCloseThisVersion = new JButton("save/close");
    private JButton buttonResetAll = new JButton("reset all/start");
    private JButton buttonNext = new JButton("next");
    private JButton buttonBack = new JButton("back");

    private JPanel buttonsIntern = new JPanel(new MigLayout());

    private JButton buttonSwitchConnectionDisplayMode = new JButton("switch link GUI");

    private JButton buttonAddComment = new JButton("add comment");
    private JButton buttonRemoveComment = new JButton("remove comment");
    private JScrollPane savedCommentsScrollPane;
    private JLabel labelSavedComments = new JLabel("");
    static LinkedList<String> comments = new LinkedList<>();

    private static final Color tooltipColor = new Color(255, 255, 150);


    /***** Internal variables *****/
    Robot bot = null;


    /***** Constructor *****/
    ButtonsAndInstructionsPanel(GroundTruthCreator groundTruthCreator) {
        this.setLayout(new MigLayout());
        this.groundTruthCreator = groundTruthCreator;
        this.getButtonsAndInstructionsOnPanel();
        this.setToolTips();
        this.setListenersToButtons();

        this.buttonNext.setFocusable(false);    // https://stackoverflow.com/a/8074326
        this.buttonBack.setFocusable(false);    // https://stackoverflow.com/a/8074326

        try {
            bot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        paintCommentPanel();
    }



    /***** Methods *****/
    private void getButtonsAndInstructionsOnPanel(){
        textFieldRequestSpecialPost.setColumns(10); // https://stackoverflow.com/questions/14805124/how-to-set-the-height-and-the-width-of-a-textfield-in-java

        buttonsIntern.add(buttonRequestSpecialPost);
        buttonsIntern.add(textFieldRequestSpecialPost, "wrap");
        buttonsIntern.add(buttonRequestRandomPost);
        buttonsIntern.add(buttonLoadPost, "wrap");
        buttonsIntern.add(buttonSwitchConnectionDisplayMode, "wrap");
        buttonsIntern.add(buttonResetAll);
        buttonsIntern.add(buttonSaveAllAndCloseThisVersion, "wrap");
        buttonsIntern.add(buttonBack);
        buttonsIntern.add(buttonNext);
        buttonsIntern.setLocation(groundTruthCreator.mainPanel.getWidth()/2, groundTruthCreator.mainPanel.getHeight()/2);
        this.add(buttonsIntern);

        JLabel instructionsLabel = new JLabel(
                "<html>" +
                        "<head/>" +
                        "<body>" +
                        "<ul>" +
                        "<li>If you click at a block b (text blocks are blue, code blocks are orange) of one version you mark b</li>" +
                        "<li>If you click this (now pink) block again you umark it.</li>" +
                        "<li>If a block is marked and you click another block of the opposite version<br>" +
                        "you create a link between blocks of two versions.</li>" +
                        "<li>If you click 'next' but there are some blocks left unmarked (orange) those blocks will be set as new/deleted.</li>" +
                        "</ul>" +
                        "</body>" +
                        "</html>.");
        instructionsLabel.setFont(new Font("courier new", Font.PLAIN, 12));
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setBackground(new Color(205, 255, 220));
        instructionsPanel.add(instructionsLabel);
        this.add(instructionsPanel);


        JPanel commentsContainer = new JPanel(new MigLayout(
                "",
                "[grow]",
                "[][grow]"));

        commentsContainer.setPreferredSize(new Dimension(GroundTruthCreator.WIDTH*100/1000, GroundTruthCreator.HEIGHT*150/1000));
        savedCommentsScrollPane = new JScrollPane(labelSavedComments, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        savedCommentsScrollPane.getViewport().setBackground(Color.WHITE);   // https://stackoverflow.com/a/18362310
        savedCommentsScrollPane.setOpaque(true);

        commentsContainer.add(buttonAddComment);
        commentsContainer.add(buttonRemoveComment, "wrap");
        commentsContainer.add(savedCommentsScrollPane, "grow, span 2");

        this.add(commentsContainer);
    }

    private void setToolTips(){
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE); // https://stackoverflow.com/questions/1190290/set-the-tooltip-delay-time-for-a-particular-component-in-java-swing
        UIManager.put("ToolTip.background", tooltipColor); // https://stackoverflow.com/questions/7807411/how-to-change-the-background-color-of-a-jtooltip-using-nimbus-lookandfeel

        buttonRequestRandomPost.setToolTipText("A random post from stack overflow will be displayed.");
        buttonLoadPost.setToolTipText("Loads a post with its set links");
        buttonSwitchConnectionDisplayMode.setToolTipText("Switch the gui for block links from egdes to films");
        buttonAddComment.setToolTipText("Add a comment to block e.g. because of multiple choices.");
        buttonRemoveComment.setToolTipText("Remove a comment by choosing its id.");
        buttonRequestSpecialPost.setToolTipText("Requests the post with the id on the right.");
        textFieldRequestSpecialPost.setToolTipText("You can enter here the id of the post you want to work with. Request it with the button 'request post.'");
        buttonSaveAllAndCloseThisVersion.setToolTipText("Your links will be saved and this post will be closed.");
        buttonResetAll.setToolTipText("Your links of this post will be reset. Then you can start it from beginning again.");
        buttonNext.setToolTipText("The next two versions of this post will be shown.");
        buttonBack.setToolTipText("The previous two versions of this post will be shown.");
    }

    void setEnablingOfNextAndBackButton(){

        buttonResetAll.setEnabled(groundTruthCreator.postVersionList != null);

        buttonSaveAllAndCloseThisVersion.setEnabled(
                groundTruthCreator.postVersionList != null
                        && !(groundTruthCreator.currentLeftVersion + 1 < groundTruthCreator.postVersionList.size() - 1)
        );

        buttonNext.setEnabled(
                groundTruthCreator.postVersionList != null &&
                        groundTruthCreator.currentLeftVersion + 1 != groundTruthCreator.postVersionList.size() - 1
        );

        buttonBack.setEnabled(
                groundTruthCreator.postVersionList != null &&
                        groundTruthCreator.currentLeftVersion != 0
        );
    }

    void actionButtonNext(){
        if(!buttonNext.isEnabled())
            return;

        if(groundTruthCreator.allCreatedBlockPairsByClicks.get(groundTruthCreator.currentLeftVersion).size()
                < Math.min(
                groundTruthCreator.postVersionList.get(groundTruthCreator.currentLeftVersion).getPostBlocks().size(),
                groundTruthCreator.postVersionList.get(groundTruthCreator.currentLeftVersion+1).getPostBlocks().size())){

            // https://stackoverflow.com/a/1395844
            Object[] options = {"Yes", "No"};
            int procedure = JOptionPane.showOptionDialog(
                    null,
                    "There are still some text and/or code blocks that are NOT linked yet.\n" +
                            "Blocks on the left side will be marked as last blocks.\n" +
                            "Blocks on the right side will be marked as new appearing blocks.\n" +
                            "Do you wish to continue and compare next two version?",
                    "More blocks could be linked",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[1]);

            if(procedure != JOptionPane.YES_OPTION)
                return;
        }


        if (groundTruthCreator.currentLeftVersion + 1 < groundTruthCreator.postVersionList.size() - 1) {
            groundTruthCreator.currentLeftVersion++;
            groundTruthCreator.unmarkLastClickedBlock();
            setEnablingOfNextAndBackButton();
            groundTruthCreator.displayCurrentTwoVersionsAndNavigator();
        }

        scrollUpToTop();
    }

    void actionButtonBack(){
        if(!buttonBack.isEnabled())
            return;

        if (groundTruthCreator.currentLeftVersion > 0) {
            groundTruthCreator.currentLeftVersion--;
            groundTruthCreator.unmarkLastClickedBlock();
            setEnablingOfNextAndBackButton();
            groundTruthCreator.displayCurrentTwoVersionsAndNavigator();
        }

        scrollUpToTop();
    }

    private void scrollUpToTop(){
        groundTruthCreator.scrollPaneIncludingMainPanel.getVerticalScrollBar().setValue(0); // https://stackoverflow.com/a/291753
    }

    private void loadPost(int postId){
        try {

            groundTruthCreator.dispose();
            System.gc();

            groundTruthCreator.postVersionList = new PostVersionList();
            groundTruthCreator.postVersionList.readFromCSV(GroundTruthCreator.path + "/", postId, 2);

            groundTruthCreator = new GroundTruthCreator(
                    groundTruthCreator.postVersionList,
                    GroundTruthCreator.WIDTH,
                    GroundTruthCreator.HEIGHT,
                    GroundTruthCreator.LOCATION);

            comments.clear();

            setEnablingOfNextAndBackButton();

            paintCommentPanel();
            groundTruthCreator.displayCurrentTwoVersionsAndNavigator();

        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to load post with post-id " + groundTruthCreator.postVersionList.getFirst().getPostId());
            System.exit(0);
        }
    }

    private void setListenersToButtons(){

        buttonNext.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                actionButtonNext();
            }
        });

        buttonBack.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                actionButtonBack();
            }
        });

        buttonResetAll.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if(!buttonResetAll.isEnabled())
                    return;

                loadPost(groundTruthCreator.postVersionList.get(0).getPostId());
            }
        });

        buttonRequestSpecialPost.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int requestedPostId = Integer.parseInt(textFieldRequestSpecialPost.getText());
                loadPost(requestedPostId);
            }
        });


        buttonRequestRandomPost.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                File file = new File(GroundTruthCreator.path);

                File[] allPostVersionListsInFolder = file.listFiles(new FilenameFilter() {   //https://stackoverflow.com/questions/4852531/find-files-in-a-folder-using-java
                    public boolean accept(File dir, String name) {
                        return !name.contains("completed") && name.endsWith(".csv");
                    }
                });

                File[] allCompletedPostVersionListsInFolder = file.listFiles(new FilenameFilter() {  // https://stackoverflow.com/questions/4852531/find-files-in-a-folder-using-java
                    public boolean accept(File dir, String name) {
                        return name.contains("completed") && name.endsWith(".csv");
                    }
                });

                LinkedList<Integer> postVersionListCandidatesThatNeedToBeDone = new LinkedList<>();
                for (File tmpCsvFile : allPostVersionListsInFolder) {
                    int tmpPostId_postVersionLists = Integer.parseInt(tmpCsvFile.toString().substring(17, tmpCsvFile.toString().length() - 4));
                    boolean fileIsAlreadyCompleted = false;
                    for (File tmpCompletedCsvFile : allCompletedPostVersionListsInFolder) {
                        int tmpPostId_completed = Integer.parseInt(tmpCompletedCsvFile.toString().substring(16 + 11, tmpCompletedCsvFile.toString().length() - 4));
                        if (tmpPostId_postVersionLists == tmpPostId_completed) {
                            fileIsAlreadyCompleted = true;
                            break;
                        }
                    }
                    if (!fileIsAlreadyCompleted)
                        postVersionListCandidatesThatNeedToBeDone.add(tmpPostId_postVersionLists);
                }

                if(postVersionListCandidatesThatNeedToBeDone.isEmpty()){
                    GTLogger.logger.log(Level.INFO, "All post version lists has been linked.");
                    JOptionPane.showMessageDialog(null, "All post version lists has been linked.");
                    return;
                }

                Collections.shuffle(postVersionListCandidatesThatNeedToBeDone);

                loadPost(postVersionListCandidatesThatNeedToBeDone.getFirst());
            }
        });


        buttonAddComment.addMouseListener(new MouseInputAdapter() { // https://stackoverflow.com/a/6555051 http://home.wlu.edu/~lambertk/BreezySwing/radiobuttons.html
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if(groundTruthCreator.postVersionList == null){
                    JOptionPane.showMessageDialog(null, "You need to select a post version list first.");
                    return;
                }

                JPanel dialog = new JPanel(new MigLayout());
                JComboBox<String> leftOrRightVersion = new JComboBox<>();
                JComboBox<Integer> positionOfBlock = new JComboBox<>();
                positionOfBlock.addItem(1);
                final int[] version = {0};
                final Integer[] lastSelectedItem = {1};

                leftOrRightVersion.addItem("left");
                leftOrRightVersion.addItem("right");


                dialog.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        super.mouseMoved(e);

                        lastSelectedItem[0] = (Integer)positionOfBlock.getSelectedItem();
                        version[0] = (Objects.equals(leftOrRightVersion.getSelectedItem(), "left")) ? groundTruthCreator.currentLeftVersion : groundTruthCreator.currentLeftVersion + 1;
                        positionOfBlock.removeAllItems();
                        for(int i = 0; i<groundTruthCreator.postVersionList.get(version[0]).getPostBlocks().size(); i++){
                            positionOfBlock.addItem(i+1);
                        }
                        if(lastSelectedItem[0] != null)positionOfBlock.setSelectedItem(lastSelectedItem[0]);
                    }
                });


                JTextField comment = new JTextField(20);

                dialog.add(new JLabel("side:"));
                dialog.add(leftOrRightVersion);
                dialog.add(Box.createHorizontalStrut(5)); // a spacer
                dialog.add(new JLabel("position:"));
                dialog.add(positionOfBlock);
                dialog.add(Box.createHorizontalStrut(5)); // a spacer
                dialog.add(new JLabel("comment:"));
                dialog.add(comment);


                int procedure = JOptionPane.showConfirmDialog(null, dialog,
                        "Please side and position of block", JOptionPane.OK_CANCEL_OPTION);

                int positionOfBlockToAddComment = (int)positionOfBlock.getSelectedItem();


                if(comment.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "You need to enter a comment in the field 'comment.'");
                    return;
                }

                String newComment = "vers: " + (version[0] +1) + " | " + "pos: " + positionOfBlockToAddComment + " | " + "<font color=\"gray\">" + comment.getText() + "</font>";

                if(procedure == JOptionPane.YES_OPTION && !comments.contains(newComment)){
                    comments.add(newComment);
                    paintCommentPanel();
                }
            }
        });

        buttonRemoveComment.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                JComboBox<Integer> commentIds = new JComboBox<>();
                for(int i=0; i<comments.size(); i++){
                    commentIds.addItem(i+1);
                }

                JPanel myPanel = new JPanel();  // https://stackoverflow.com/a/6555051
                myPanel.add(new JLabel("delete a comment by choosing its id:"));
                myPanel.add(commentIds);

                int result = JOptionPane.showConfirmDialog(
                        null, myPanel,
                        "Delete Comment", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    comments.remove((int)commentIds.getSelectedItem()-1);
                    paintCommentPanel();
                }
            }
        });


        buttonLoadPost.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                Integer requestedPostId = null;
                try {
                    requestedPostId = Integer.parseInt(textFieldRequestSpecialPost.getText());
                }catch (Exception ignored){
                    return;
                }
                Integer finalRequestedPostId = requestedPostId;


                File file = null;
                File[] postVersionList = null;
                File[] completedCSV = null;
                try {
                    file = new File(GroundTruthCreator.path);
                    postVersionList = file.listFiles((dir, name) -> name.matches(finalRequestedPostId + "\\.csv"));  // //https://stackoverflow.com/questions/4852531/find-files-in-a-folder-using-java
                    completedCSV = file.listFiles((dir, name) -> name.matches("completed_" + finalRequestedPostId + "\\.csv")); // //https://stackoverflow.com/questions/4852531/find-files-in-a-folder-using-java
                }catch (Exception ignored){
                    JOptionPane.showMessageDialog(null, "the files completed_" + requestedPostId + ".csv and " + requestedPostId + ".csv both needs to be in same folder postVersionLists.");
                    return;
                }

                if(postVersionList == null || completedCSV == null || postVersionList.length == 0 || completedCSV.length == 0){
                    JOptionPane.showMessageDialog(null, "the files completed_" + requestedPostId + ".csv and " + requestedPostId + ".csv both needs to be in same folder postVersionLists.");
                    return;
                }

                loadPost(requestedPostId);




                Path pathToCSV = FileSystems.getDefault().getPath("postVersionLists", completedCSV[0].getName());
                List<String> lines = Toolkit.parseLines(pathToCSV.toString());

                lines.sort((o1, o2) -> {
                    StringTokenizer tokens_o1 = new StringTokenizer(o1, "; ");
                    int postId_o1 = Integer.valueOf(tokens_o1.nextToken().replaceAll("\"", ""));
                    int postHistoryId_o1 = Integer.valueOf(tokens_o1.nextToken().replaceAll("\"", ""));
                    int postBlockTypeId_o1 = Integer.valueOf(tokens_o1.nextToken().replaceAll("\"", ""));
                    int localId_o1 = Integer.valueOf(tokens_o1.nextToken().replaceAll("\"", ""));

                    Integer predLocalId_o1 = null;
                    Integer succLocalId_o1 = null;
                    try {
                        predLocalId_o1 = Integer.valueOf(tokens_o1.nextToken().replaceAll("\"", ""));
                    } catch (Exception e1) {
                    }

                    try {
                        succLocalId_o1 = Integer.valueOf(tokens_o1.nextToken().replaceAll("\"", ""));
                    } catch (Exception e1) {
                    }


                    StringTokenizer tokens_o2 = new StringTokenizer(o2, "; ");
                    int postId_o2 = Integer.valueOf(tokens_o2.nextToken().replaceAll("\"", ""));
                    int postHistoryId_o2 = Integer.valueOf(tokens_o2.nextToken().replaceAll("\"", ""));
                    int postBlockTypeId_o2 = Integer.valueOf(tokens_o2.nextToken().replaceAll("\"", ""));
                    int localId_o2 = Integer.valueOf(tokens_o2.nextToken().replaceAll("\"", ""));

                    Integer predLocalId_o2 = null;
                    Integer succLocalId_o2 = null;
                    try {
                        predLocalId_o2 = Integer.valueOf(tokens_o2.nextToken().replaceAll("\"", ""));
                    } catch (Exception e1) {
                    }

                    try {
                        succLocalId_o2 = Integer.valueOf(tokens_o2.nextToken().replaceAll("\"", ""));
                    } catch (Exception e1) {
                    }


                    if (postHistoryId_o1 != postHistoryId_o2)
                        return postHistoryId_o1 - postHistoryId_o2;
                    else
                        return localId_o1 - localId_o2;
                });


                Integer version = 0;
                Integer lastPostHistoryId = null;
                for(String line : lines){
                    StringTokenizer tokens = new StringTokenizer(line, ";");

                    int postId = Integer.valueOf(tokens.nextToken().replaceAll("\"", "").replaceAll("\\s+", ""));
                    int postHistoryId = Integer.valueOf(tokens.nextToken().replaceAll("\"", "").replaceAll("\\s+", ""));
                    int postBlockTypeId = Integer.valueOf(tokens.nextToken().replaceAll("\"", "").replaceAll("\\s+", ""));
                    int localId = Integer.valueOf(tokens.nextToken().replaceAll("\"", "").replaceAll("\\s+", ""));


                    Integer predLocalId = null;
                    Integer succLocalId = null;
                    try {
                        predLocalId = Integer.valueOf(tokens.nextToken().replaceAll("\"", "").replaceAll("\\s+", ""));
                    }catch (Exception e1){}

                    try {
                        succLocalId = Integer.valueOf(tokens.nextToken().replaceAll("\"", "").replaceAll("\\s+", ""));
                    }catch (Exception e1){}


                    String comment = tokens.nextToken();
                    comment = comment.substring(1,comment.length()-1);
                    if(comment.length() > 1){

                        comment = "vers: " + (version+1) + " | " + "pos: " + localId + " | " + "<font color=\"gray\">" + comment + "</font>";
                        comments.add(comment);
                    }


                    if(lastPostHistoryId != null && postHistoryId > lastPostHistoryId)
                        version++;

                    if(succLocalId != null){
                        groundTruthCreator.allAutomaticSetBlockPairs.get(version).add(
                                new BlockPair(
                                        null,
                                        null,
                                        postBlockTypeId == 1,
                                        localId-1,
                                        succLocalId-1));
                    }

                    lastPostHistoryId = postHistoryId;
                }
            }
        });


        buttonSwitchConnectionDisplayMode.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(groundTruthCreator.linkConnectionDisplayMode == GroundTruthCreator.LinkConnectionDisplayModes.edges)
                    groundTruthCreator.linkConnectionDisplayMode = GroundTruthCreator.LinkConnectionDisplayModes.films;
                else if(groundTruthCreator.linkConnectionDisplayMode == GroundTruthCreator.LinkConnectionDisplayModes.films)
                    groundTruthCreator.linkConnectionDisplayMode = GroundTruthCreator.LinkConnectionDisplayModes.edges;

                groundTruthCreator.versionEdgesPanel.removeAll();
                groundTruthCreator.versionEdgesPanel.validate();
                groundTruthCreator.versionEdgesPanel.repaint();
            }
        });

        buttonSaveAllAndCloseThisVersion.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if(!buttonSaveAllAndCloseThisVersion.isEnabled())
                    return;

                Object[] options = {"Yes", "No"};
                int procedure = JOptionPane.showOptionDialog(
                        null,
                        "Saving will close this post version list. Continue?",
                        "Finish linking?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[1]);

                if(procedure != JOptionPane.YES_OPTION)
                    return;

                groundTruthCreator.currentLeftVersion = 0;

                // extracts links from clicked block pair edges
                for(int i = 0; i<groundTruthCreator.allCreatedBlockPairsByClicks.size(); i++){
                    for(int j = 0; j<groundTruthCreator.allCreatedBlockPairsByClicks.get(i).size(); j++){

                        int postBlockTypeId = groundTruthCreator.allCreatedBlockPairsByClicks.get(i).get(j).clickedBlockIsInstanceOfTextBlockVersion ? TextBlockVersion.postBlockTypeId : CodeBlockVersion.postBlockTypeId;

                        PostVersion leftPostVersion = groundTruthCreator.postVersionList.get(i);
                        int leftVersion = i+1;
                        int leftLocalId = groundTruthCreator.allCreatedBlockPairsByClicks.get(i).get(j).leftBlockPosition+1;

                        PostVersion rightPostVersion = groundTruthCreator.postVersionList.get(i+1);
                        int rightVersion = i+2;
                        int rightLocalId = groundTruthCreator.allCreatedBlockPairsByClicks.get(i).get(j).rightBlockPosition+1;

                        PostBlockLifeSpanVersion leftBlockLifeSpanSnapshot = new PostBlockLifeSpanVersion(
                                leftPostVersion.getPostId(),
                                leftPostVersion.getPostHistoryId(),
                                postBlockTypeId,
                                leftVersion,
                                leftLocalId
                        );

                        PostBlockLifeSpanVersion rightBlockLifeSpanSnapshot = new PostBlockLifeSpanVersion(
                                rightPostVersion.getPostId(),
                                rightPostVersion.getPostHistoryId(),
                                postBlockTypeId,
                                rightVersion,
                                rightLocalId
                        );

                        boolean leftSnapshotfoundInAChain = false;
                        for(int k = 0; k<groundTruthCreator.blockLifeSpansExtractedFromClicks.size(); k++){
                            if(groundTruthCreator.blockLifeSpansExtractedFromClicks.get(k).getLast().equals(leftBlockLifeSpanSnapshot)){
                                groundTruthCreator.blockLifeSpansExtractedFromClicks.get(k).add(rightBlockLifeSpanSnapshot);
                                leftSnapshotfoundInAChain = true;
                                break;
                            }
                        }

                        if(!leftSnapshotfoundInAChain){
                            PostBlockLifeSpan newBlockLifeSpan = new PostBlockLifeSpan(leftPostVersion.getPostId(), postBlockTypeId);
                            newBlockLifeSpan.add(leftBlockLifeSpanSnapshot);
                            newBlockLifeSpan.add(rightBlockLifeSpanSnapshot);
                            groundTruthCreator.blockLifeSpansExtractedFromClicks.add(newBlockLifeSpan);
                        }

                    }
                }


                // handles blocks that were not clicked
                //if(groundTruthCreator.postVersionList != null)
                for(int i=0; i<groundTruthCreator.postVersionList.size(); i++){
                    for(int j=0; j<groundTruthCreator.postVersionList.get(i).getPostBlocks().size(); j++){
                        int postId = groundTruthCreator.postVersionList.get(i).getPostBlocks().get(j).getPostId();
                        int postBlockTypeId = groundTruthCreator.postVersionList.get(i).getPostBlocks().get(j) instanceof TextBlockVersion ? TextBlockVersion.postBlockTypeId : CodeBlockVersion.postBlockTypeId;

                        PostBlockLifeSpanVersion tmpBlockLifeSpanSnapshot
                                = new PostBlockLifeSpanVersion(
                                groundTruthCreator.postVersionList.get(i).getPostId(),
                                groundTruthCreator.postVersionList.get(i).getPostHistoryId(),
                                postBlockTypeId,
                                i+1,
                                j+1
                        );

                        boolean tmpBlockLifeSpanSnapshotHasBeenFound = false;
                        for(int k = 0; k<groundTruthCreator.blockLifeSpansExtractedFromClicks.size(); k++){
                            for(int l = 0; l<groundTruthCreator.blockLifeSpansExtractedFromClicks.get(k).size(); l++) {

                                if (groundTruthCreator.blockLifeSpansExtractedFromClicks.get(k).get(l).getVersion() == tmpBlockLifeSpanSnapshot.getVersion()
                                        && groundTruthCreator.blockLifeSpansExtractedFromClicks.get(k).get(l).getLocalId() == tmpBlockLifeSpanSnapshot.getLocalId()) {
                                    tmpBlockLifeSpanSnapshotHasBeenFound = true;
                                    break;
                                }
                            }
                        }

                        if(!tmpBlockLifeSpanSnapshotHasBeenFound){
                            PostBlockLifeSpan tmpBlockLifeSpan
                                    = new PostBlockLifeSpan(
                                    postId,
                                    postBlockTypeId
                            );
                            tmpBlockLifeSpan.add(tmpBlockLifeSpanSnapshot);
                            groundTruthCreator.blockLifeSpansExtractedFromClicks.add(tmpBlockLifeSpan);
                        }
                    }
                }


                groundTruthCreator.postVersionList = new PostVersionList();

                groundTruthCreator.versionLeftPanel.removeAll();
                groundTruthCreator.versionRightPanel.removeAll();
                groundTruthCreator.versionEdgesPanel.removeAll();

                groundTruthCreator.versionLeftPanel.validate();
                groundTruthCreator.versionLeftPanel.repaint();

                groundTruthCreator.versionRightPanel.validate();
                groundTruthCreator.versionRightPanel.repaint();

                groundTruthCreator.versionEdgesPanel.validate();
                groundTruthCreator.versionEdgesPanel.repaint();

                groundTruthCreator.navigatorAtBottomLabel.validate();
                groundTruthCreator.navigatorAtBottomLabel.repaint();

                labelSavedComments.setText("");

                savedCommentsScrollPane.validate();
                savedCommentsScrollPane.repaint();

                groundTruthCreator.blockLifeSpansExtractedFromClicks.sort((PostBlockLifeSpan b1, PostBlockLifeSpan b2) -> {
                    if (b1.getFirst().getVersion() < b2.getFirst().getVersion()) {
                        return -1;
                    } else if (b1.getFirst().getVersion() > b2.getFirst().getVersion()) {
                        return 1;
                    } else {
                        return Integer.compare(b1.getFirst().getLocalId(), b2.getFirst().getLocalId());
                    }
                });

                groundTruthCreator.writeFileOfPostVersionList();
                groundTruthCreator.postVersionList = null;
                comments.clear();
                groundTruthCreator.displayCurrentTwoVersionsAndNavigator();


                setEnablingOfNextAndBackButton();
            }
        });
    }

    void paintCommentPanel(){
        StringBuilder text = new StringBuilder("<html></head><body>");
        for(int i=0; i<comments.size(); i++){
            text.append("<font color=\"orange\">").append(i + 1).append("</font>").append("): ").append(comments.get(i).replace("\"", "")).append("<br>");
        }
        text.append("</body></html>");
        labelSavedComments.setText(text.toString());
        labelSavedComments.validate();
        labelSavedComments.repaint();
        //savedCommentsScrollPane.validate();
        //savedCommentsScrollPane.repaint();
    }
}
