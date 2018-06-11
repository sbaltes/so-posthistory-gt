package de.unitrier.st.soposthistory.gt.GroundTruthApp;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.web.WebEngine;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.sotorrent.posthistoryextractor.blocks.PostBlockVersion;
import org.sotorrent.posthistoryextractor.version.PostVersionList;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Controller {

    // TODO: Tooltips
    // TODO: button next: pop up if not all connections are set
    // TODO: button prev: scroll to top if necessary
    // TODO: Add function: random post
    // TODO: Add and remove comment
    // TODO: save: question if sure, remind if not all connections are set; extract block pairs
    // TODO: connect blocks automatically

    public MenuItem MenuItem_selectRootOfPostVersionLists;
    public TextField textFieldPostId;

    /* GUI items */
    @FXML
    Button buttonBack,
            buttonNext;

    @FXML
    private VBox leftVBox,
            rightVBox;

    @FXML
    private Pane connectionsPane;

    @FXML
    private Label bottomLabel;


    /* intern variables */
    private PostVersionList postVersionList;
    private int currentLeftVersion = 0;
    private Path pathToSelectedRootOfPostVersionLists;
    private File[] postVersionListsInCSVFiles;

    private PostBlockWebView lastClickedBlock1 = null,
                             lastClickedBlock2 = null;

    private List<BlockPair> blockPairs = new LinkedList<>();


    private enum BlockBorderColorStatus {blockConnectionNotSet, blockConnectionSet, blockMarked}

    private final Color colorForTextNotClicked = new Color(128./255, 212./255, 255./255, 1.0);
    private final Color colorForCodeNotClicked = new Color(255./255, 204./255, 128./255, 1.0);
    private final Color colorForTextWithSetConnection = new Color(196./255, 236./255, 255./255, 1.0);
    private final Color colorForCodeWithSetConnection = new Color(255./255, 233./255, 199./255, 1.0);
    private final Color colorForClickedBlock = new Color(255./255, 114./255, 252./255, 1.0);



    /* GUI */

    @FXML
    public void selectRootOfPostVersionLists() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        try {
            directoryChooser.setInitialDirectory(pathToSelectedRootOfPostVersionLists.toFile());
        } catch (Exception ignored){}
        directoryChooser.setTitle("Select directory of your Post Version Lists");
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        pathToSelectedRootOfPostVersionLists = Paths.get(String.valueOf(selectedDirectory));

        postVersionListsInCSVFiles = pathToSelectedRootOfPostVersionLists.toFile().listFiles((directory, name) -> name.matches("\\d+\\.csv"));

    }

    @FXML
    private void loadButtonClicked() {
        try {
            blockPairs.clear();
            lastClickedBlock1 = null;
            lastClickedBlock2 = null;

            String postId = textFieldPostId.getText();
            loadPostViaPostID(Integer.valueOf(postId));

        } catch (Exception e) {
            System.err.println("Post ID is either invalid or does not exist in the selected (sub)folders");
        }
    }

    @FXML
    private void loadPostViaPostID(int postId) {
        for (File file : postVersionListsInCSVFiles) {
            if (postId == Integer.valueOf(file.getName().replace(".csv", ""))) {
                this.postVersionList = PostVersionList.readFromCSV(Paths.get(file.getParent()), Integer.valueOf(file.getName().replace(".csv", "")), (byte) 2);
                loadPostVersionBlocksInGUI();
                return;
            }
        }
    }


    private void loadPostVersionBlocksInGUI() {
        resetGUI();

        loadPostVersionBlocksInGUI(true);

        if (currentLeftVersion < postVersionList.size() - 1) {
            loadPostVersionBlocksInGUI(false);
        }

        updateTextAtBottomLabel();
    }

    private void resetGUI () {
        leftVBox.getChildren().clear();
        rightVBox.getChildren().clear();
        connectionsPane.getChildren().clear();
    }


    private void loadPostVersionBlocksInGUI(boolean isLeftSide) {

        int positionOfPostVersion = isLeftSide ? currentLeftVersion : currentLeftVersion + 1;
        VBox leftOrRightVBox = isLeftSide ? leftVBox : rightVBox;

        List<PostBlockVersion> postBlocks = postVersionList.get(positionOfPostVersion).getPostBlocks();
        for (PostBlockVersion postBlock : postBlocks) {
            PostBlockWebView postBlockWebView = new PostBlockWebView(postBlock, isLeftSide, postBlock.getPostHistoryId());

            String convertedMarkdownText = convertMarkdownToHTML(postBlock, getBlockPairOfClickedPostBlock(postBlockWebView) == -1 ? BlockBorderColorStatus.blockConnectionNotSet : BlockBorderColorStatus.blockConnectionSet);

            WebEngine webEngine = postBlockWebView.webView.getEngine();
            webEngine.loadContent(convertedMarkdownText);

            leftOrRightVBox.getChildren().add(postBlockWebView.webView);

            postBlockWebView.webView.setOnMouseReleased(event -> setAndPaintConnectionOfBlockPairsIfPossible(postBlockWebView));
        }
    }



    private void setAndPaintConnectionOfBlockPairsIfPossible(PostBlockWebView postBlockWebView) {

        // delete an existing connection
        int positionOfBlockPairOfClickedPostBlock = getBlockPairOfClickedPostBlock(postBlockWebView);
        if (positionOfBlockPairOfClickedPostBlock != -1) {
            blockPairs.get(positionOfBlockPairOfClickedPostBlock).leftBlock.webView.getEngine().loadContent(
                    convertMarkdownToHTML(blockPairs.get(positionOfBlockPairOfClickedPostBlock).leftBlock.postBlock, BlockBorderColorStatus.blockConnectionNotSet)
            );
            blockPairs.get(positionOfBlockPairOfClickedPostBlock).rightBlock.webView.getEngine().loadContent(
                    convertMarkdownToHTML(blockPairs.get(positionOfBlockPairOfClickedPostBlock).rightBlock.postBlock, BlockBorderColorStatus.blockConnectionNotSet)
            );

            blockPairs.remove(positionOfBlockPairOfClickedPostBlock);

            lastClickedBlock1 = null;
            lastClickedBlock2 = null;

            paintAllConnectionsOfComparedVersions();

            return;
        }

        // mark an unassigned block
        if (lastClickedBlock1 == null) {
            lastClickedBlock1 = postBlockWebView;
            lastClickedBlock1.webView.getEngine().loadContent(convertMarkdownToHTML(lastClickedBlock1.postBlock, BlockBorderColorStatus.blockMarked));
            return;
        }

        lastClickedBlock2 = postBlockWebView;

        // clicking the same block twice means the user wants to unmark the block again
        if (lastClickedBlock1 == lastClickedBlock2) {
            resetLastClickedBlocksAndTheirBorders();
            return;
        }

        // if blocks are of the same type and in different versions the connection will be set.
        if ((lastClickedBlock1.postBlock.getPostBlockTypeId().equals(lastClickedBlock2.postBlock.getPostBlockTypeId()))
                && (lastClickedBlock1.isLeftVersion && !lastClickedBlock2.isLeftVersion
                ||  lastClickedBlock2.isLeftVersion && !lastClickedBlock1.isLeftVersion)) {

            // swap to make internal use easier
            if (lastClickedBlock1.postBlock.getPostHistoryId() > lastClickedBlock2.postBlock.getPostHistoryId()) {
                PostBlockWebView tmp = lastClickedBlock1;
                lastClickedBlock1 = lastClickedBlock2;
                lastClickedBlock2 = tmp;
            }

            blockPairs.add(new BlockPair(lastClickedBlock1, lastClickedBlock2, currentLeftVersion));

            resetLastClickedBlocksAndTheirBorders();

            paintAllConnectionsOfComparedVersions();
        }

    }

    private int getBlockPairOfClickedPostBlock (PostBlockWebView postBlockWebView) {
        for (int i=0; i<blockPairs.size(); i++) {
            if (blockPairs.get(i).leftVersion == currentLeftVersion
                    && (postBlockWebView.equals(blockPairs.get(i).leftBlock) || postBlockWebView.equals(blockPairs.get(i).rightBlock))) {
                return i;
            }
        }
        return -1;
    }

    private Polygon paintPolygonOfConnections(PostBlockWebView leftBlock, PostBlockWebView rightBlock) {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(
                leftBlock.webView.getLayoutX() + leftBlock.webView.getWidth(),
                leftBlock.webView.getLayoutY(),

                rightBlock.webView.getLayoutX(),
                rightBlock.webView.getLayoutY(),

                rightBlock.webView.getLayoutX(),
                rightBlock.webView.getLayoutY() + rightBlock.webView.getHeight(),

                leftBlock.webView.getLayoutX() + leftBlock.webView.getWidth(),
                leftBlock.webView.getLayoutY() + leftBlock.webView.getHeight()
        );

        polygon.setFill(leftBlock.postBlock.getPostBlockTypeId() == 1 ? colorForTextWithSetConnection : leftBlock.postBlock.getPostBlockTypeId() == 2 ? colorForCodeWithSetConnection : null);

        return polygon;
    }

    private void resetLastClickedBlocksAndTheirBorders() {
        if (lastClickedBlock1 != null) lastClickedBlock1.webView.getEngine().loadContent(convertMarkdownToHTML(lastClickedBlock1.postBlock, BlockBorderColorStatus.blockConnectionNotSet));
        if (lastClickedBlock2 != null) lastClickedBlock2.webView.getEngine().loadContent(convertMarkdownToHTML(lastClickedBlock2.postBlock, BlockBorderColorStatus.blockConnectionNotSet));
        lastClickedBlock1 = null;
        lastClickedBlock2 = null;
    }

    private String convertMarkdownToHTML(PostBlockVersion postBlock, BlockBorderColorStatus blockBorderColorStatus) {
        String convertedMarkdownText = convertMarkdownToHTMLViaCommonmarkMark(
                postBlock.getContent()
        );

        switch (blockBorderColorStatus) {
            case blockMarked:
                convertedMarkdownText = wrapPostBlockWithBorderColor(convertedMarkdownText, colorForClickedBlock);
                break;
            case blockConnectionNotSet:
                convertedMarkdownText = wrapPostBlockWithBorderColor(convertedMarkdownText, postBlock.getPostBlockTypeId() == 1 ? colorForTextNotClicked : postBlock.getPostBlockTypeId() == 2 ? colorForCodeNotClicked : Color.gray(0));
                break;
            case blockConnectionSet:
                convertedMarkdownText = wrapPostBlockWithBorderColor(convertedMarkdownText, postBlock.getPostBlockTypeId() == 1 ? colorForTextWithSetConnection : postBlock.getPostBlockTypeId() == 2 ? colorForCodeWithSetConnection : Color.gray(0));
                break;
        }

        return convertedMarkdownText;
    }

    static String convertMarkdownToHTMLViaCommonmarkMark(String markdownText){   // https://github.com/atlassian/commonmark-java
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private String wrapPostBlockWithBorderColor(String text, Color color) {

        return
                "<head>\n" +
                    "<style>\n" +
                        "div.borderColor {\n" +
                        "    border-style: solid;\n" +
                        "    border-radius: 5px;\n" +
                        "    border-width: medium;\n" +
                        "    border-color: " + String.format("#%02x%02x%02x",
                                                (int)(color.getRed()*255),
                                                (int)(color.getGreen()*255),
                                                (int)(color.getBlue()*255)) + ";\n" +
                        "}\n" +
                        "body {\n" +                                                     // no scrollbars for webviews
                        "    overflow-x: hidden;\n" +
                        "    overflow-y: hidden;\n" +
                        "}" +
                    "</style>\n" +
                "</head>" +
                "<body>" +
                    "<div class=\"borderColor\">\n" +
                        text +
                    "</div>\n" +
                "</body>";
    }

    private void updateTextAtBottomLabel() {
        bottomLabel.setText(
                "Post ID: " + postVersionList.getFirst().getPostId()
                        + " ### number of versions: " + postVersionList.size()
                        + " ### you are now comparing the versions " + (currentLeftVersion+1) + " and " + (currentLeftVersion+2));
    }


    /* Buttons */
    @FXML
    public void buttonBackClicked() {
        if (currentLeftVersion > 0) {
            currentLeftVersion--;
            loadPostVersionBlocksInGUI();

            paintAllConnectionsOfComparedVersions();
        }
    }

    @FXML
    public void buttonNextClicked() {
        if (currentLeftVersion < postVersionList.size()-2) {
            currentLeftVersion++;
            loadPostVersionBlocksInGUI();

            paintAllConnectionsOfComparedVersions();
        }
    }

    private void paintAllConnectionsOfComparedVersions() {
        connectionsPane.getChildren().clear();
        for (BlockPair blockPair : blockPairs) {
            if (blockPair.leftVersion == currentLeftVersion) {
                blockPair.leftBlock.webView.getEngine().loadContent(convertMarkdownToHTML(blockPair.leftBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));
                // blockPair.rightBlock.webView.getEngine().loadContent(convertMarkdownToHTML(blockPair.rightBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));
                blockPair.rightBlock.webView.getEngine().loadContent(wrapPostBlockWithBorderColor(blockPair.computeDiffs(), blockPair.rightBlock.postBlock.getPostBlockTypeId() == 1 ? colorForTextWithSetConnection : blockPair.rightBlock.postBlock.getPostBlockTypeId() == 2 ? colorForCodeWithSetConnection : Color.gray(0)));
                Polygon polygon = paintPolygonOfConnections(blockPair.rightBlock, blockPair.leftBlock);
                connectionsPane.getChildren().add(polygon);
            }
        }
    }
}
