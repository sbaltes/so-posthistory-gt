package de.unitrier.st.soposthistorygt.GroundTruthApp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Toolkit {

    static boolean blockIsAlreadyInPairWithEdge_atPositionLeft(LinkedList<LinkedList<BlockPair>> allCreatedBlockPairsByClicks, int currentLeftVersion, int blockPositionToBeChecked){
        if(currentLeftVersion >= allCreatedBlockPairsByClicks.size())
            return false;

        for(int j=0; j<allCreatedBlockPairsByClicks.get(currentLeftVersion).size(); j++){
            if(allCreatedBlockPairsByClicks.get(currentLeftVersion).get(j).leftBlockPosition == blockPositionToBeChecked){
                return true;
            }
        }
        return false;
    }

    static boolean blockIsAlreadyInPairWithEdge_atPositionRight(LinkedList<LinkedList<BlockPair>> allCreatedBlockPairsByClicks, int currentLeftVersion, int blockPositionToBeChecked){
        if(currentLeftVersion >= allCreatedBlockPairsByClicks.size())
            return false;

        for(int j=0; j<allCreatedBlockPairsByClicks.get(currentLeftVersion).size(); j++){
            if(allCreatedBlockPairsByClicks.get(currentLeftVersion).get(j).rightBlockPosition == blockPositionToBeChecked){
                return true;
            }
        }
        return false;
    }

    static Integer getPositionOfLeftBlockRelatedToRightBlockOfSameBlockPair(LinkedList<LinkedList<BlockPair>> allCreatedBlockPairsByClicks, int currentLeftVersion, int blockPositionOfRightBlock){
        if(currentLeftVersion >= allCreatedBlockPairsByClicks.size())
            return null;

        for(int j=0; j<allCreatedBlockPairsByClicks.get(currentLeftVersion).size(); j++){
            if(allCreatedBlockPairsByClicks.get(currentLeftVersion).get(j).rightBlockPosition == blockPositionOfRightBlock){
                return allCreatedBlockPairsByClicks.get(currentLeftVersion).get(j).leftBlockPosition;
            }
        }
        return null;
    }

    static LinkedList<String> parseLines(String pathToExportedCSV){

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(pathToExportedCSV));
        } catch (FileNotFoundException e) {
            System.err.println("Failed to read file with path '" + pathToExportedCSV + "'.");
            System.exit(0);
        }
        LinkedList<String> lines = new LinkedList<>();

        String line;
        try {
            while((line = bufferedReader.readLine()) != null){
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Failed to parse line from data at path " + pathToExportedCSV + ".");
            System.exit(0);
        }

        lines.remove(0); // first line contains header

        return lines;
    }
}
