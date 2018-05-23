package de.unitrier.st.soposthistory.gt.GroundTruthApp;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String absolutePathOfFXML = String.valueOf(Paths.get("src", "de", "unitrier", "st", "soposthistory", "gt", "groundTruthAppFX", "GroundTruthCreator.fxml").toAbsolutePath());
        Parent root = FXMLLoader.load(new URL("file:///" + absolutePathOfFXML));
        primaryStage.setTitle("Ground Truth Creator");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
