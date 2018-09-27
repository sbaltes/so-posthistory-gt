package de.unitrier.st.soposthistory.gt.GroundTruthMetricComparatorApp;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.sotorrent.posthistoryextractor.blocks.PostBlockVersion;
import org.sotorrent.posthistoryextractor.gt.PostBlockLifeSpanVersion;
import org.sotorrent.posthistoryextractor.version.PostVersionList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    public MenuItem MenuItem_selectRootOfPostVersionLists;
    public TextField textFieldPostId;

    @FXML
    private CheckBox checkBoxShowConnectionsOfGroundTruth;
    @FXML
    private CheckBox checkBoxShowConnectionsOfComputedMetric;

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
    private Map<Integer, File> postVersionLists;


    private List<BlockPair> blockPairs_groundTruth = new LinkedList<>();
    private List<BlockPair> blockPairs_metric = new LinkedList<>();


    private enum BlockBorderColorStatus {blockConnectionNotSet, blockConnectionSet, blockMarked}

    private final Color colorForTextNotClicked = new Color(128. / 255, 212. / 255, 255. / 255, 1.0);
    private final Color colorForCodeNotClicked = new Color(255. / 255, 204. / 255, 128. / 255, 1.0);
    private final Color colorForTextWithSetConnection = new Color(196. / 255, 236. / 255, 255. / 255, 0.5);
    private final Color colorForCodeWithSetConnection = new Color(255. / 255, 233. / 255, 199. / 255, 0.5);
    private final Color colorForClickedBlock = new Color(255. / 255, 114. / 255, 252. / 255, 1.0);



    /* GUI */

    @FXML
    private void selectRootOfPostVersionLists() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        try {
            directoryChooser.setInitialDirectory(pathToSelectedRootOfPostVersionLists.toFile());
        } catch (Exception ignored) {
        }
        directoryChooser.setTitle("Select directory of your Post Version Lists");
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        pathToSelectedRootOfPostVersionLists = Paths.get(String.valueOf(selectedDirectory));

        File[] postVersionListsInCSVFiles = pathToSelectedRootOfPostVersionLists.toFile().listFiles((directory, name) -> name.matches("\\d+\\.csv"));
        if (postVersionListsInCSVFiles == null) {
            return;
        }

        postVersionLists = new HashMap<>();
        for (File postVersionListsInCSVFile : postVersionListsInCSVFiles) {
            postVersionLists.put(Integer.valueOf(postVersionListsInCSVFile.getName().replace(".csv", "")), postVersionListsInCSVFile);
        }
    }

    private void updateTextAtBottomLabel() {
        bottomLabel.setText(
                "Post ID: " + postVersionList.getFirst().getPostId()
                        + " ### number of versions: " + postVersionList.size()
                        + " ### you are now comparing the versions " + (currentLeftVersion + 1) + " and " + (currentLeftVersion + 2));
    }

    private void resetGUI() {
        leftVBox.getChildren().clear();
        rightVBox.getChildren().clear();
        connectionsPane.getChildren().clear();
        checkBoxShowConnectionsOfGroundTruth.setSelected(false);
        checkBoxShowConnectionsOfComputedMetric.setSelected(false);
    }

    private void visualizeInGUI() {
        resetGUI();

        visualizeLeftSide();
        visualizeRightSide();

        updateTextAtBottomLabel();
    }

    private void visualizeLeftSide() {
        List<PostBlockVersion> leftPostBlocks = postVersionList.get(currentLeftVersion).getPostBlocks();
        for (PostBlockVersion leftPostBlock : leftPostBlocks) {
            PostBlockWebView postBlockWebView = new PostBlockWebView(leftPostBlock, true, leftPostBlock.getPostHistoryId());

            leftVBox.getChildren().add(postBlockWebView.webViewFitContent.webview);

            String convertedMarkdownText = convertMarkdownToHTML(
                    leftPostBlock,
                    BlockBorderColorStatus.blockConnectionNotSet);

            postBlockWebView.webViewFitContent.setContent(convertedMarkdownText);
            postBlockWebView.webViewFitContent.webEngine.loadContent(convertedMarkdownText);

            for (BlockPair blockPair : blockPairs_groundTruth) {
                if (blockPair.leftBlock.postBlock.getPostHistoryId().equals(leftPostBlock.getPostHistoryId())
                        && blockPair.leftBlock.postBlock.getLocalId().equals(leftPostBlock.getLocalId())) {
                    blockPair.leftBlock = postBlockWebView;
                }
            }
            for (BlockPair blockPair : blockPairs_metric) {
                if (blockPair.leftBlock.postBlock.getPostHistoryId().equals(leftPostBlock.getPostHistoryId())
                        && blockPair.leftBlock.postBlock.getLocalId().equals(leftPostBlock.getLocalId())) {
                    blockPair.leftBlock = postBlockWebView;
                }
            }
        }
    }

    private void visualizeRightSide() {
        List<PostBlockVersion> rightPostBlocks = postVersionList.get(currentLeftVersion + 1).getPostBlocks();
        for (PostBlockVersion rightPostBlock : rightPostBlocks) {
            PostBlockWebView postBlockWebView = new PostBlockWebView(rightPostBlock, false, rightPostBlock.getPostHistoryId());

            rightVBox.getChildren().add(postBlockWebView.webViewFitContent.webview);

            String convertedMarkdownText = convertMarkdownToHTML(
                    rightPostBlock,
                    BlockBorderColorStatus.blockConnectionNotSet);

            postBlockWebView.webViewFitContent.setContent(convertedMarkdownText);
            postBlockWebView.webViewFitContent.webEngine.loadContent(convertedMarkdownText);

            for (BlockPair blockPair : blockPairs_groundTruth) {
                if (blockPair.rightBlock.postBlock.getPostHistoryId().equals(rightPostBlock.getPostHistoryId())
                        && blockPair.rightBlock.postBlock.getLocalId().equals(rightPostBlock.getLocalId())) {
                    blockPair.rightBlock = postBlockWebView;
                }
            }
            for (BlockPair blockPair : blockPairs_metric) {
                if (blockPair.rightBlock.postBlock.getPostHistoryId().equals(rightPostBlock.getPostHistoryId())
                        && blockPair.rightBlock.postBlock.getLocalId().equals(rightPostBlock.getLocalId())) {
                    blockPair.rightBlock = postBlockWebView;
                }
            }
        }
    }

    @FXML
    private void showConnectionsOfGroundTruth() {

        if (!checkBoxShowConnectionsOfGroundTruth.isSelected()) {
            connectionsPane.getChildren().clear();
            visualizeInGUI();
            return;
        }

        for (BlockPair blockPair : blockPairs_groundTruth) {
            if (blockPair.leftVersion == currentLeftVersion) {
                blockPair.leftBlock.webViewFitContent.setContent(convertMarkdownToHTML(blockPair.leftBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));
                blockPair.leftBlock.webViewFitContent.webview.getEngine().loadContent(convertMarkdownToHTML(blockPair.leftBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));
                blockPair.rightBlock.webViewFitContent.setContent(convertMarkdownToHTML(blockPair.rightBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));
                blockPair.rightBlock.webViewFitContent.webview.getEngine().loadContent(convertMarkdownToHTML(blockPair.rightBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));

                /* with diffs
                blockPair.rightBlock.webViewFitContent.setContent(wrapPostBlockWithBorderColor(
                        Util.computeDiffs(blockPair),
                        blockPair.rightBlock.postBlock.getPostBlockTypeId() == 1 ? colorForTextWithSetConnection : blockPair.rightBlock.postBlock.getPostBlockTypeId() == 2 ? colorForCodeWithSetConnection : Color.gray(0)));
                blockPair.rightBlock.webViewFitContent.webview.getEngine().loadContent(
                        wrapPostBlockWithBorderColor(
                                Util.computeDiffs(blockPair),
                                blockPair.rightBlock.postBlock.getPostBlockTypeId() == 1 ? colorForTextWithSetConnection : blockPair.rightBlock.postBlock.getPostBlockTypeId() == 2 ? colorForCodeWithSetConnection : Color.gray(0)));
                 */

                Polygon polygon = paintPolygonOfConnections(blockPair.rightBlock, blockPair.leftBlock);
                connectionsPane.getChildren().add(polygon);
            }
        }
    }

    @FXML
    private void showConnectionsOfComputedMetric() {

        if (!checkBoxShowConnectionsOfComputedMetric.isSelected()) {
            connectionsPane.getChildren().clear();
            visualizeInGUI();
            return;
        }

        for (BlockPair blockPair : blockPairs_metric) {
            if (blockPair.leftVersion == currentLeftVersion) {

                blockPair.leftBlock.webViewFitContent.setContent(convertMarkdownToHTML(blockPair.leftBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));
                blockPair.leftBlock.webViewFitContent.webview.getEngine().loadContent(convertMarkdownToHTML(blockPair.leftBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));
                blockPair.rightBlock.webViewFitContent.setContent(convertMarkdownToHTML(blockPair.rightBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));
                blockPair.rightBlock.webViewFitContent.webview.getEngine().loadContent(convertMarkdownToHTML(blockPair.rightBlock.postBlock, BlockBorderColorStatus.blockConnectionSet));
                Line line = paintSimpleConnections(blockPair.rightBlock, blockPair.leftBlock);

                connectionsPane.getChildren().add(line);
            }
        }
    }

    private Polygon paintPolygonOfConnections(PostBlockWebView leftBlock, PostBlockWebView rightBlock) {
        Polygon polygon = new Polygon();

        polygon.getPoints().addAll(
                leftBlock.webViewFitContent.webview.getLayoutX() + leftBlock.webViewFitContent.webview.getWidth(),
                leftBlock.webViewFitContent.webview.getLayoutY(),

                rightBlock.webViewFitContent.webview.getLayoutX(),
                rightBlock.webViewFitContent.webview.getLayoutY(),

                rightBlock.webViewFitContent.webview.getLayoutX(),
                rightBlock.webViewFitContent.webview.getLayoutY() + rightBlock.webViewFitContent.webview.getHeight(),

                leftBlock.webViewFitContent.webview.getLayoutX() + leftBlock.webViewFitContent.webview.getWidth(),
                leftBlock.webViewFitContent.webview.getLayoutY() + leftBlock.webViewFitContent.webview.getHeight()
        );


        polygon.setFill(leftBlock.postBlock.getPostBlockTypeId() == 1 ? colorForTextWithSetConnection : leftBlock.postBlock.getPostBlockTypeId() == 2 ? colorForCodeWithSetConnection : null);

        return polygon;
    }

    private Line paintSimpleConnections(PostBlockWebView leftBlock, PostBlockWebView rightBlock) {
        Line line = new Line(
                leftBlock.webViewFitContent.webview.getLayoutX() + leftBlock.webViewFitContent.webview.getWidth(),
                leftBlock.webViewFitContent.webview.getLayoutY() + leftBlock.webViewFitContent.webview.getHeight() / 2,
                rightBlock.webViewFitContent.webview.getLayoutX(),
                rightBlock.webViewFitContent.webview.getLayoutY() + rightBlock.webViewFitContent.webview.getHeight() / 2
        );

        line.setStrokeDashOffset(0.2);
        line.setStrokeWidth(10);
        line.setStroke(Color.GRAY);

        return line;
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

    static String convertMarkdownToHTMLViaCommonmarkMark(String markdownText) {   // https://github.com/atlassian/commonmark-java
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
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255)) + ";\n" +
                        "}\n" +
                        //"body {\n" +                                                     // no scrollbars for webviews
                        //"    overflow-x: hidden;\n" +
                        //"    overflow-y: hidden;\n" +
                        //"}" +
                        "</style>\n" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"borderColor\">\n" +
                        text +
                        "</div>\n" +
                        "</body>";
    }


    /* Buttons */
    @FXML
    private void loadButtonClicked() {
        try {
            blockPairs_groundTruth.clear();
            currentLeftVersion = 0;

            int postId = Integer.valueOf(textFieldPostId.getText());
            this.postVersionList = PostVersionList.readFromCSV(Paths.get(String.valueOf(postVersionLists.get(postId))).getParent(), postId, (byte) 2);

            importConnectionsOfGroundTruth();
            importConnectionsOfMetrics();
            visualizeInGUI();

        } catch (Exception e) {
            System.err.println("Post ID is either invalid or does not exist in the selected (sub)folders");
        }
    }


    @FXML
    public void buttonBackClicked() {
        if (currentLeftVersion > 0) {
            currentLeftVersion--;
            visualizeInGUI();
        }
    }

    @FXML
    public void buttonNextClicked() {
        if (currentLeftVersion < postVersionList.size() - 2) {
            currentLeftVersion++;
            visualizeInGUI();
        }
    }

    @FXML
    private void importConnectionsOfGroundTruth() {

        try {
            // extract connections from CSV
            String completedFileCSV = new String(Files.readAllBytes(Paths.get(pathToSelectedRootOfPostVersionLists.toString(), "completed_" + postVersionList.getPostId() + ".csv")), StandardCharsets.UTF_8);

            Pattern groundTruthCSVRegex = Pattern.compile("(\\d+);(\\d+);([12]);(\\d+);(null|\\d+);(null|\\d+);");
            Matcher matcher = groundTruthCSVRegex.matcher(completedFileCSV);

            List<PostBlockLifeSpanVersion> postBlockLifeSpanVersionList = new LinkedList<>();

            while (matcher.find()) {
                int postId = Integer.parseInt(matcher.group(1));
                int postHistoryId = Integer.parseInt(matcher.group(2));
                byte postBlockTypeId = Byte.parseByte(matcher.group(3));
                int localId = Integer.parseInt(matcher.group(4));

                Integer predLocalId = null;
                Integer succLocalId = null;

                try {
                    predLocalId = Integer.parseInt(matcher.group(5));
                } catch (Exception ignored) {
                }

                try {
                    succLocalId = Integer.parseInt(matcher.group(6));
                } catch (Exception ignored) {
                }


                postBlockLifeSpanVersionList.add(
                        new PostBlockLifeSpanVersion(
                                postId,
                                postHistoryId,
                                postBlockTypeId,
                                localId,
                                predLocalId,
                                succLocalId
                        )
                );
            }

            // sort lines for easier assignment
            postBlockLifeSpanVersionList.sort((o1, o2) -> {
                if (o1.getPostId() < o2.getPostId()) {
                    return -1;
                } else if (o1.getPostId() > o2.getPostId()) {
                    return 1;
                } else if (o1.getPostHistoryId() < o2.getPostHistoryId()) {
                    return -1;
                } else if (o1.getPostHistoryId() > o2.getPostHistoryId()) {
                    return 1;
                }

                return Integer.compare(o1.getLocalId(), o2.getLocalId());
            });

            // add connections to block pairs
            int version = 0;
            Integer lastPostHistoryId = null;
            for (PostBlockLifeSpanVersion postBlockLifeSpanVersion : postBlockLifeSpanVersionList) {

                if (lastPostHistoryId != null && postBlockLifeSpanVersion.getPostHistoryId() > lastPostHistoryId)
                    version++;

                if (postBlockLifeSpanVersion.getSuccLocalId() != null) {
                    blockPairs_groundTruth.add(
                            new BlockPair(
                                    new PostBlockWebView(
                                            postVersionList.get(version).getPostBlocks().get(postBlockLifeSpanVersion.getLocalId() - 1),
                                            true,
                                            version
                                    ),
                                    new PostBlockWebView(
                                            postVersionList.get(version + 1).getPostBlocks().get(postBlockLifeSpanVersion.getSuccLocalId() - 1),
                                            false,
                                            version + 1
                                    ),
                                    version
                            )
                    );

                    lastPostHistoryId = postBlockLifeSpanVersion.getPostHistoryId();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void importConnectionsOfMetrics() {
        postVersionList.processVersionHistory();

        for (int i=0; i<postVersionList.size()-1 ; i++) {
            for (int j=0; j<postVersionList.get(i).getPostBlocks().size(); j++) {
                if (postVersionList.get(i).getPostBlocks().get(j).getSucc() != null) {
                    blockPairs_metric.add(
                            new BlockPair(
                                    new PostBlockWebView(
                                            postVersionList.get(i).getPostBlocks().get(j),
                                            true,
                                            i
                                    ),
                                    new PostBlockWebView(
                                            postVersionList.get(i).getPostBlocks().get(j).getSucc(),
                                            false,
                                            i + 1
                                    ),
                                    i
                            )
                    );
                }
            }
        }
    }
}
