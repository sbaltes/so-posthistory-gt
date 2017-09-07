package de.unitrier.st.soposthistorygt.metricsComparism;

import de.unitrier.st.soposthistorygt.util.BlockLifeSpanSnapshot;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

public class GroundTruthExtractionOfCSVs {

    Vector<ConnectionsOfAllVersions> groundTruth = new Vector<>();
    Vector<ConnectionsOfAllVersions> groundTruth_text = new Vector<>();
    Vector<ConnectionsOfAllVersions> groundTruth_code = new Vector<>();


    // constructor
    public GroundTruthExtractionOfCSVs(String pathOfDirectoryOfCSVs){
        groundTruth = extractListOfConnectionsOfAllVersionsOfAllExportedCSVs(pathOfDirectoryOfCSVs);
        groundTruth.sort(Comparator.comparingInt(o -> o.postId));
        divideGroundTruthIntoTextAndCode();
    }


    public static List<String> parseLines(String pathToExportedCSV){

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(pathToExportedCSV));
        } catch (FileNotFoundException e) {
            System.err.println("Failed to read file with path '" + pathToExportedCSV + "'.");
            System.exit(0);
        }
        List<String> lines = new Vector<>();

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

    private List<BlockLifeSpanSnapshot> extractBlockLifeSpanSnapshotsUnordered(List<String> lines){

        List<BlockLifeSpanSnapshot> blockLifeSpanSnapshots = new Vector<>();

        for(String line : lines){
            StringTokenizer tokens = new StringTokenizer(line, "; ");
            int postId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            int postHistoryId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            int postBlockTypeId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            int localId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            Integer predLocalId = null;
            Integer succLocalId = null;

            try {
                predLocalId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            }catch (NumberFormatException ignored) {}

            try {
                succLocalId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            }catch (NumberFormatException ignored){}

            BlockLifeSpanSnapshot blockLifeSpanSnapshot = new BlockLifeSpanSnapshot(postId, postHistoryId, postBlockTypeId, -1, localId, predLocalId, succLocalId);
            blockLifeSpanSnapshots.add(blockLifeSpanSnapshot);
        }

        return blockLifeSpanSnapshots;
    }

    private Vector<Vector<BlockLifeSpanSnapshot>> orderBlockLifeSpanSnapshotsByPostHistoryId(List<BlockLifeSpanSnapshot> blockLifeSpanSnapshots){
        blockLifeSpanSnapshots.sort(Comparator.comparingInt(BlockLifeSpanSnapshot::getPostHistoryId));

        int count = 1;
        Vector<Vector<BlockLifeSpanSnapshot>> listOfListOfBlockLifeSnapshotsOrderedByVersions = new Vector<>();

        for (BlockLifeSpanSnapshot snapshot : blockLifeSpanSnapshots) {
            if (!listOfListOfBlockLifeSnapshotsOrderedByVersions.isEmpty()
                    && listOfListOfBlockLifeSnapshotsOrderedByVersions.lastElement().lastElement().getPostHistoryId() == snapshot.getPostHistoryId()) {
                listOfListOfBlockLifeSnapshotsOrderedByVersions.lastElement().add(snapshot);
            } else {
                listOfListOfBlockLifeSnapshotsOrderedByVersions.add(new Vector<>());
                listOfListOfBlockLifeSnapshotsOrderedByVersions.lastElement().add(snapshot);
                count++;
            }
            listOfListOfBlockLifeSnapshotsOrderedByVersions.lastElement().lastElement().setVersion(count);
        }

        return listOfListOfBlockLifeSnapshotsOrderedByVersions;
    }

    private ConnectionsOfTwoVersions getAllConnectionsBetweenTwoVersions(int leftVersionId, Vector<BlockLifeSpanSnapshot> leftVersionOfBlocks){
        ConnectionsOfTwoVersions connectionsOfTwoVersions = new ConnectionsOfTwoVersions(leftVersionId);
        for(int i=0; i<leftVersionOfBlocks.size(); i++){
            connectionsOfTwoVersions.add(
                    new ConnectedBlocks(
                            leftVersionOfBlocks.get(i).getLocalId(),
                            leftVersionOfBlocks.get(i).getSuccLocalId(),
                            leftVersionOfBlocks.get(i).getPostBlockTypeId()
                    ));
        }
        return connectionsOfTwoVersions;
    }

    private ConnectionsOfAllVersions getAllConnectionsOfAllConsecutiveVersions(String pathToCSV){
        List<String> lines = parseLines(pathToCSV);
        List<BlockLifeSpanSnapshot> listOfBlockLifeSpanSnapshots = extractBlockLifeSpanSnapshotsUnordered(lines);
        Vector<Vector<BlockLifeSpanSnapshot>> listOfListOfBlockLifeSpanSnapshots = orderBlockLifeSpanSnapshotsByPostHistoryId(listOfBlockLifeSpanSnapshots);

        ConnectionsOfAllVersions connectionsOfAllVersions = new ConnectionsOfAllVersions(listOfListOfBlockLifeSpanSnapshots.firstElement().firstElement().getPostId());

        for(int i=0; i<listOfListOfBlockLifeSpanSnapshots.size()-1; i++){
            connectionsOfAllVersions.add(
                    getAllConnectionsBetweenTwoVersions(i+1, listOfListOfBlockLifeSpanSnapshots.get(i))
            );
        }

        return connectionsOfAllVersions;
    }

    private Vector<ConnectionsOfAllVersions> extractListOfConnectionsOfAllVersionsOfAllExportedCSVs(String directoryOfGroundTruthCSVs){

        File file = new File(directoryOfGroundTruthCSVs);
        Pattern pattern = Pattern.compile("completed_" + "[0-9]+" + "\\.csv");
        File[] allCompletedPostVersionListsInFolder = file.listFiles((dir, name) -> name.matches(pattern.pattern())); // https://stackoverflow.com/questions/4852531/find-files-in-a-folder-using-java

        assert allCompletedPostVersionListsInFolder != null;
        for(File completedCSV : allCompletedPostVersionListsInFolder){
            try {
                groundTruth.add(getAllConnectionsOfAllConsecutiveVersions(completedCSV.getCanonicalPath()));
            } catch (IOException e) {
                System.err.println("Failed to read canonical path of data '" + completedCSV.getName() + "'.");
                System.exit(0);
            }
        }

        //groundTruth.sort(Comparator.comparingInt(o -> o.firstElement().firstElement()));

        return groundTruth;
    }

    private void divideGroundTruthIntoTextAndCode(){
        for(ConnectionsOfAllVersions allVersionsOfConnections : groundTruth){
            groundTruth_text.add(new ConnectionsOfAllVersions(allVersionsOfConnections.postId));
            groundTruth_code.add(new ConnectionsOfAllVersions(allVersionsOfConnections.postId));
            int count = 1;
            for(ConnectionsOfTwoVersions twoVersionsOfConnections : allVersionsOfConnections){
                groundTruth_text.lastElement().add(new ConnectionsOfTwoVersions(count));
                groundTruth_code.lastElement().add(new ConnectionsOfTwoVersions(count));
                for(ConnectedBlocks connectedBlock : twoVersionsOfConnections){
                    if(connectedBlock.postBlockTypeId == 1){
                        groundTruth_text.lastElement().lastElement().add(connectedBlock);
                    }else{
                        groundTruth_code.lastElement().lastElement().add(connectedBlock);
                    }
                }
                count++;
            }
        }
    }


    public ConnectionsOfAllVersions getAllConnectionsOfAllConsecutiveVersions_text(int postId){
        for (ConnectionsOfAllVersions groundTruth_text : groundTruth_text)
            if (groundTruth_text.postId == postId)
                return groundTruth_text;

        return null;
    }

    public ConnectionsOfAllVersions getAllConnectionsOfAllConsecutiveVersions_code(int postId){
        for (ConnectionsOfAllVersions groundTruth_code : groundTruth_code)
            if (groundTruth_code.postId == postId)
                return groundTruth_code;

        return null;
    }
}
