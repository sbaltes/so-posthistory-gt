package de.unitrier.st.soposthistorygt.metricsComparism;

import de.unitrier.st.soposthistory.blocks.CodeBlockVersion;
import de.unitrier.st.soposthistory.blocks.TextBlockVersion;
import de.unitrier.st.soposthistory.version.PostVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;
import de.unitrier.st.soposthistorygt.util.anchorsURLs.AnchorTextAndUrlHandler;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import static de.unitrier.st.soposthistorygt.GroundTruthApp.GroundTruthCreator.normalizeURLsInTextBlocksOfAllVersions;


public class PostVersionsListManagement {

    private String pathToDirectory;

    public static Pattern pattern_groundTruth = Pattern.compile("[0-9]+" + "\\.csv");
    List<PostVersionList> postVersionLists = new Vector<>();

    // constructor
    public PostVersionsListManagement(String pathToDirectoryOfPostHistories){
        parseAllPostVersionLists(pathToDirectoryOfPostHistories);

    }

    private void parseAllPostVersionLists(String pathToDirectoryOfPostHistories){
        this.pathToDirectory = pathToDirectoryOfPostHistories;

        File file = new File(pathToDirectoryOfPostHistories);
        File[] allPostHistoriesInFolder = file.listFiles((dir, name) -> name.matches(pattern_groundTruth.pattern())); // https://stackoverflow.com/questions/4852531/find-files-in-a-folder-using-java

        assert allPostHistoriesInFolder != null;
        for(File postHistory : allPostHistoriesInFolder){
            PostVersionList tmpPostVersionList = new PostVersionList();
            int postId = Integer.valueOf(postHistory.getName().substring(0, postHistory.getName().length()-4));

            tmpPostVersionList.readFromCSV(pathToDirectory, postId, 2);

            AnchorTextAndUrlHandler anchorTextAndUrlHandler = new AnchorTextAndUrlHandler();
            normalizeURLsInTextBlocksOfAllVersions(tmpPostVersionList, anchorTextAndUrlHandler);
            // removeEmptyTextAndCodeBlocks(tmpPostVersionList);

            postVersionLists.add(
                    tmpPostVersionList
            );
        }

        postVersionLists.sort(Comparator.comparingInt(o -> o.getFirst().getPostId()));
    }

    // access to post verion lists
    public PostVersionList getPostVersionListWithID(int postID){
        for(PostVersionList postVersionList : postVersionLists){
            if(postID == postVersionList.getFirst().getPostId()){
                return postVersionList;
            }
        }

        return null;
    }


    // converting post verion lists to make them easier to compare with ground truth
    private ConnectionsOfTwoVersions getAllConnectionsBetweenTwoVersions_text(int leftVersionId, PostVersion leftPostVersion, PostVersion rightPostVersion){
        ConnectionsOfTwoVersions connectionsOfTwoVersions = new ConnectionsOfTwoVersions(leftVersionId);

        for(int i=0; i<leftPostVersion.getPostBlocks().size(); i++){
            if(leftPostVersion.getPostBlocks().get(i) instanceof CodeBlockVersion)
                continue;

            Integer rightLocalId = null;
            for(int j=0; j<rightPostVersion.getPostBlocks().size(); j++) {
                if(rightPostVersion.getPostBlocks().get(j).getPred() != null && rightPostVersion.getPostBlocks().get(j).getPred().equals(leftPostVersion.getPostBlocks().get(i))){
                    rightLocalId = rightPostVersion.getPostBlocks().get(j).getLocalId();
                    break;
                }
            }

            connectionsOfTwoVersions.add(
                    new ConnectedBlocks(
                            leftPostVersion.getPostBlocks().get(i).getLocalId(),
                            rightLocalId,
                            leftPostVersion.getPostBlocks().get(i) instanceof TextBlockVersion ? 1 : 2
                    ));

        }

        return connectionsOfTwoVersions;
    }

    private ConnectionsOfTwoVersions getAllConnectionsBetweenTwoVersions_code(int leftVersionId, PostVersion leftPostVersion, PostVersion rightPostVersion){
        ConnectionsOfTwoVersions connectionsOfTwoVersions = new ConnectionsOfTwoVersions(leftVersionId);

        for(int i=0; i<leftPostVersion.getPostBlocks().size(); i++){
            if(leftPostVersion.getPostBlocks().get(i) instanceof TextBlockVersion)
                continue;

            Integer rightLocalId = null;
            for(int j=0; j<rightPostVersion.getPostBlocks().size(); j++) {
                if(rightPostVersion.getPostBlocks().get(j).getPred() != null && rightPostVersion.getPostBlocks().get(j).getPred().equals(leftPostVersion.getPostBlocks().get(i))){
                    rightLocalId = rightPostVersion.getPostBlocks().get(j).getLocalId();
                    break;
                }
            }

            connectionsOfTwoVersions.add(
                    new ConnectedBlocks(
                            leftPostVersion.getPostBlocks().get(i).getLocalId(),
                            rightLocalId,
                            leftPostVersion.getPostBlocks().get(i) instanceof TextBlockVersion ? 1 : 2
                    ));

        }

        return connectionsOfTwoVersions;
    }


    public ConnectionsOfAllVersions getAllConnectionsOfAllConsecutiveVersions_text(int postId){
        ConnectionsOfAllVersions connectionsOfAllVersions = new ConnectionsOfAllVersions(postId);

        for(int i=0; i<getPostVersionListWithID(postId).size()-1; i++){
            connectionsOfAllVersions.add(
                    getAllConnectionsBetweenTwoVersions_text(i, getPostVersionListWithID(postId).get(i), getPostVersionListWithID(postId).get(i+1))
            );
        }

        return connectionsOfAllVersions;
    }

    public ConnectionsOfAllVersions getAllConnectionsOfAllConsecutiveVersions_code(int postId){
        ConnectionsOfAllVersions connectionsOfAllVersions = new ConnectionsOfAllVersions(postId);

        for(int i=0; i<getPostVersionListWithID(postId).size()-1; i++){
            connectionsOfAllVersions.add(
                    getAllConnectionsBetweenTwoVersions_code(i, getPostVersionListWithID(postId).get(i), getPostVersionListWithID(postId).get(i+1))
            );
        }

        return connectionsOfAllVersions;
    }
}
