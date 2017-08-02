package de.unitrier.st.soposthistorygt.GroundTruthApp;

import java.util.Vector;

class Toolkit {

    static boolean blockIsAlreadyInPairWithEdge_atPositionLeft(Vector<Vector<BlockPair>> allCreatedBlockPairsByClicks, int currentLeftVersion, int blockPositionToBeChecked){
        if(currentLeftVersion >= allCreatedBlockPairsByClicks.size())
            return false;

        for(int j=0; j<allCreatedBlockPairsByClicks.get(currentLeftVersion).size(); j++){
            if(allCreatedBlockPairsByClicks.get(currentLeftVersion).get(j).leftBlockPosition == blockPositionToBeChecked){
                return true;
            }
        }
        return false;
    }

    static boolean blockIsAlreadyInPairWithEdge_atPositionRight(Vector<Vector<BlockPair>> allCreatedBlockPairsByClicks, int currentLeftVersion, int blockPositionToBeChecked){
        if(currentLeftVersion >= allCreatedBlockPairsByClicks.size())
            return false;

        for(int j=0; j<allCreatedBlockPairsByClicks.get(currentLeftVersion).size(); j++){
            if(allCreatedBlockPairsByClicks.get(currentLeftVersion).get(j).rightBlockPosition == blockPositionToBeChecked){
                return true;
            }
        }
        return false;
    }

    static Integer getPositionOfLeftBlockRelatedToRightBlockOfSameBlockPair(Vector<Vector<BlockPair>> allCreatedBlockPairsByClicks, int currentLeftVersion, int blockPositionOfRightBlock){
        if(currentLeftVersion >= allCreatedBlockPairsByClicks.size())
            return null;

        for(int j=0; j<allCreatedBlockPairsByClicks.get(currentLeftVersion).size(); j++){
            if(allCreatedBlockPairsByClicks.get(currentLeftVersion).get(j).rightBlockPosition == blockPositionOfRightBlock){
                return allCreatedBlockPairsByClicks.get(currentLeftVersion).get(j).leftBlockPosition;
            }
        }
        return null;
    }
}
