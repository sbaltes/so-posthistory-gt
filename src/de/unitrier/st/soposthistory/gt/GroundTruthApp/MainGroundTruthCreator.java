package de.unitrier.st.soposthistory.gt.GroundTruthApp;

public class MainGroundTruthCreator{


    public static void main(String[] args){

        /*
        // computes old ground truth to compare results with app's results
        StaticPostVersionsLists staticPostVersionsLists = new StaticPostVersionsLists();
        staticPostVersionsLists.init();
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();
        groundTruth.setPerfectPredAndSucc();


        StaticPostVersionsLists.PostVersionListEnum postVersionListEnumID_ToBeCompared =  p_3758880;


        System.out.println(BlockLifeSpan.getLifeSpansOfAllBlocks(groundTruth.getPostVersionListWithEnumID(postVersionListEnumID_ToBeCompared), BlockLifeSpan.Type.textblock));
        System.out.println(BlockLifeSpan.getLifeSpansOfAllBlocks(groundTruth.getPostVersionListWithEnumID(postVersionListEnumID_ToBeCompared), BlockLifeSpan.Type.codeblock));
        */

        new GroundTruthCreator(
                //staticPostVersionsLists.getPostVersionListWithEnumID(postVersionListEnumID_ToBeCompared),
                null,
                1400,
                800,
                null
        );
    }
}
