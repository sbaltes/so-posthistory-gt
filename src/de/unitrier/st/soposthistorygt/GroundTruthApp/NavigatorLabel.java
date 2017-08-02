package de.unitrier.st.soposthistorygt.GroundTruthApp;

import javax.swing.*;
import java.awt.*;

class NavigatorLabel extends JLabel{

    private GroundTruthCreator groundTruthCreator;

    NavigatorLabel(GroundTruthCreator groundTruthCreator){
        this.groundTruthCreator = groundTruthCreator;
        this.setFont(new Font("Courier New", Font.BOLD, 20)); // http://www.java2s.com/Code/JavaAPI/java.awt/newFontStringnameintstyleintsize.htm
        this.updateNavigatorText();
    }

    void updateNavigatorText() {
        try {
            this.setText(
                    "<html>" +
                            "<head><head/>" +
                            "<body>" +
                            "<font color='white'>post id: <font color='lime'>" + groundTruthCreator.postVersionList.getFirst().getPostId() + "</font> ### " +
                            "number of versions: <font color='lime'>" + groundTruthCreator.postVersionList.size() + "</font> ### " +
                            "you are now comparing the versions <font color='lime'>" + (groundTruthCreator.currentLeftVersion + 1) + "</font> and <font color='lime'>" + (groundTruthCreator.currentLeftVersion + 2) + "</font>" +
                            "</body>" +
                            "</html>");
        }catch (NullPointerException e){
            this.setText(
                    "<html>" +
                            "<head><head/>" +
                            "<body>" +
                            "<font color='white'>post id: <font color='lime'> - </font> ### " +
                            "number of versions: <font color='lime'> - </font> ### " +
                            "you are now comparing the versions <font color='lime'> - </font> and <font color='lime'> - </font>" +
                            "</body>" +
                            "</html>");
        }
    }
}
