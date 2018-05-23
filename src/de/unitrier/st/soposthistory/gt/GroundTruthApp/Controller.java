package de.unitrier.st.soposthistory.gt.GroundTruthApp;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.sotorrent.posthistoryextractor.version.PostVersionList;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    /* GUI items */
    @FXML
    MenuItem MenuItem_loadPostViaPostID;
    @FXML
    Button buttonBack, buttonNext;

    @FXML
    private VBox leftVBox, rightVBox;
    @FXML
    private Pane connectionsPane;

    @FXML
    private Label bottomLabel;


    /* intern variables */
    private PostVersionList postVersionList;
    private int currentLeftVersion = 0;


    /* GUI */
    @FXML
    public void loadPostViaPostID() {
        loadPostVersionListViaFileChooser();
        loadPostVersionBlocksInGUI();
    }

    private void loadPostVersionListViaFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select post to set connections");
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        Path pathToSelectedFile = Paths.get(String.valueOf(selectedFile));

        Pattern pattern = Pattern.compile("(.*?)(\\d+)(\\.csv)");
        Matcher matcher = pattern.matcher(selectedFile.toString());

        if (matcher.find()) {
            Integer postId = Integer.valueOf(matcher.group(2));
            postVersionList = PostVersionList.readFromCSV(pathToSelectedFile.getParent(), postId, (byte) 2);
        }
    }

    private void loadPostVersionBlocksInGUI() {
        resetGUI();

        loadPostVersionBlocksInGUI(currentLeftVersion, leftVBox);

        if (currentLeftVersion < postVersionList.size() - 1) {
            loadPostVersionBlocksInGUI(currentLeftVersion + 1, rightVBox);
        }

        updateTextAtBottomLabel();
    }

    private void resetGUI () {
        leftVBox.getChildren().clear();
        rightVBox.getChildren().clear();
        connectionsPane.getChildren().clear();
    }

    private String convertToHTMLViaCommonmarkMark(String markdownText){   // https://github.com/atlassian/commonmark-java
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private void loadPostVersionBlocksInGUI(int positionOfPostVersion, VBox leftOrRightVBox) {
        for (int i=0; i<postVersionList.get(positionOfPostVersion).getPostBlocks().size(); i++) {
            WebView tmpWebView = new WebView();
            String markupFromMarkdown =
                    convertToHTMLViaCommonmarkMark(
                            postVersionList.get(positionOfPostVersion).getPostBlocks().get(i).getContent()
                    )
                    ;
            tmpWebView.getEngine().loadContent(markupFromMarkdown);
            leftOrRightVBox.getChildren().add(tmpWebView);
        }
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
        }
    }

    @FXML
    public void buttonNextClicked() {
        if (currentLeftVersion < postVersionList.size()-2) {
            currentLeftVersion++;
            loadPostVersionBlocksInGUI();
        }
    }
}
// C:\Users\Administrator\Documents\GitHub\so-posthistory-gt\postVersionLists
