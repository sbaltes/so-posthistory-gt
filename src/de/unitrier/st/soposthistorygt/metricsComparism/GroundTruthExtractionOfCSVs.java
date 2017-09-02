package de.unitrier.st.soposthistorygt.metricsComparism;

import de.unitrier.st.soposthistorygt.util.BlockLifeSpan;
import de.unitrier.st.soposthistorygt.util.BlockLifeSpanSnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class GroundTruthExtractionOfCSVs {

    private static HashMap<Integer, Integer> mapPostHistoryId_to_version = new HashMap<>();


    private static List<String> parseLines(String pathToExportedCSV) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToExportedCSV));
        List<String> lines = new Vector<>();

        String line;
        while((line = bufferedReader.readLine()) != null){
            lines.add(line);
        }

        lines.remove(0); // first line contains header

        return lines;
    }

    private static List<BlockLifeSpanSnapshot> extractBlockLifeSpanSnapshotsUnordered(List<String> lines){

        List<BlockLifeSpanSnapshot> blockLifeSpanSnapshots = new Vector<>();

        for(String line : lines){
            StringTokenizer tokens = new StringTokenizer(line, "; ");
            int postId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            int postHistoryId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            int postBlockTypeId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            int localId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
            Integer predLocalId = null;
            Integer succLocalId = null;

            BlockLifeSpanSnapshot blockLifeSpanSnapshot = new BlockLifeSpanSnapshot(postId, postHistoryId, -1, localId);
            blockLifeSpanSnapshots.add(blockLifeSpanSnapshot);
        }

        return blockLifeSpanSnapshots;
    }

    private static void orderBlockLifeSpanSnapshotsByPostHistoryId(List<BlockLifeSpanSnapshot> blockLifeSpanSnapshots){
        blockLifeSpanSnapshots.sort(Comparator.comparingInt(BlockLifeSpanSnapshot::getPostHistoryId));
    }

    private static void setVersionsForAllBlockLifeSnapshots(List<BlockLifeSpanSnapshot> listOfBlockLifeSpanSnapshots){
        int count = 1;
        for(int i=0; i<listOfBlockLifeSpanSnapshots.size(); i++){
            listOfBlockLifeSpanSnapshots.get(i).setVersion(count);

            mapPostHistoryId_to_version.put(listOfBlockLifeSpanSnapshots.get(i).getPostHistoryId(), count);

            int j=i+1;
            while(j < listOfBlockLifeSpanSnapshots.size() && listOfBlockLifeSpanSnapshots.get(j).getPostHistoryId() == listOfBlockLifeSpanSnapshots.get(i).getPostHistoryId()){
                listOfBlockLifeSpanSnapshots.get(j).setVersion(count);
                j++;
            }
            i = j-1;
            count++;
        }
    }

    private static List<BlockLifeSpan> listOfBlockLifeSnapshots_to_listOfBlockLifeSpans(List<BlockLifeSpanSnapshot> listOfBlockLifeSpanSnapshots, List<String> lines){

        List<BlockLifeSpan> listOfBlockLifeSpans = new Vector<>();

        for(int k=lines.size()-1; k>=0; k--){
            StringTokenizer tokens = new StringTokenizer(lines.get(k), "; ");
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

            if(predLocalId == null){
                BlockLifeSpan blockLifeSpan = new BlockLifeSpan((postBlockTypeId == 1 ? BlockLifeSpan.Type.textblock : postBlockTypeId == 2 ? BlockLifeSpan.Type.codeblock : null));
                for(int i=listOfBlockLifeSpanSnapshots.size()-1; i>=0; i--){
                    if(listOfBlockLifeSpanSnapshots.get(i).getPostHistoryId() == postHistoryId && listOfBlockLifeSpanSnapshots.get(i).getLocalId() == localId){
                        blockLifeSpan.add(listOfBlockLifeSpanSnapshots.get(i));
                        listOfBlockLifeSpanSnapshots.remove(i+0);
                        lines.remove(k);
                        break;
                    }
                }

                listOfBlockLifeSpans.add(blockLifeSpan);
            }
        }

        while(!lines.isEmpty()){
            for (int k = 0; k < lines.size(); k++) {
                StringTokenizer tokens = new StringTokenizer(lines.get(k), "; ");
                int postId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
                int postHistoryId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
                int postBlockTypeId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
                int localId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
                Integer predLocalId = null;
                Integer succLocalId = null;

                try {
                    predLocalId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
                } catch (NumberFormatException ignored) {}

                try {
                    succLocalId = Integer.valueOf(tokens.nextToken().replace("\"", ""));
                } catch (NumberFormatException ignored) {}

                for(int j=0; j<listOfBlockLifeSpans.size(); j++){
                    if(listOfBlockLifeSpans.get(j).lastElement().getVersion()+1 == mapPostHistoryId_to_version.get(postHistoryId)){
                        if(listOfBlockLifeSpans.get(j).lastElement().getLocalId() == predLocalId){

                            for(int i=listOfBlockLifeSpanSnapshots.size()-1; i>=0; i--){
                                if(listOfBlockLifeSpanSnapshots.get(i).getPostHistoryId() == postHistoryId && listOfBlockLifeSpanSnapshots.get(i).getLocalId() == localId){
                                    listOfBlockLifeSpans.get(j).add(listOfBlockLifeSpanSnapshots.get(i));
                                    listOfBlockLifeSpanSnapshots.remove(i+0);
                                    lines.remove(k);
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        }

        Collections.reverse(listOfBlockLifeSpans);
        return listOfBlockLifeSpans;
    }


    public static List<BlockLifeSpan> extractExportedCsvToListOfBlockLifeSpans(String pathToCSV) throws IOException {
        List<String> lines = parseLines(pathToCSV);
        List<BlockLifeSpanSnapshot> listOfBlockLifeSpanSnapshots = extractBlockLifeSpanSnapshotsUnordered(lines);
        orderBlockLifeSpanSnapshotsByPostHistoryId(listOfBlockLifeSpanSnapshots);
        setVersionsForAllBlockLifeSnapshots(listOfBlockLifeSpanSnapshots);

        return listOfBlockLifeSnapshots_to_listOfBlockLifeSpans(listOfBlockLifeSpanSnapshots, lines);
    }

    public static List<List<BlockLifeSpan>> extractListOfListsOfBlockLifeSpansOfAllExportedCSVs(String directoryOfGroundTruthCSVs) throws IOException {

        List<List<BlockLifeSpan>> groundTruth = new Vector<>();

        File file = new File(directoryOfGroundTruthCSVs);
        Pattern pattern = Pattern.compile("completed_" + "[0-9]+" + "\\.csv");
        File[] allCompletedPostVersionListsInFolder = file.listFiles((dir, name) -> name.matches(pattern.pattern())); // https://stackoverflow.com/questions/4852531/find-files-in-a-folder-using-java

        assert allCompletedPostVersionListsInFolder != null;
        for(File completedCSV : allCompletedPostVersionListsInFolder){
            groundTruth.add(extractExportedCsvToListOfBlockLifeSpans(completedCSV.getCanonicalPath()));
        }

        groundTruth.sort(Comparator.comparingInt(o -> o.get(0).firstElement().getPostId()));

        return groundTruth;
    }

    public static List<List<BlockLifeSpan>> filterListOfListOfBlockLifeSpansByType(List<List<BlockLifeSpan>> groundTruth, BlockLifeSpan.Type type){
        List<List<BlockLifeSpan>> groundTruth_text = new Vector<>();
        for (List<BlockLifeSpan> blockLifeSpans : groundTruth) {
            List<BlockLifeSpan> tmpListOfBlockLifeSpans = new Vector<>();
            for (int j = 0; j < blockLifeSpans.size(); j++) {
                if (blockLifeSpans.get(j).getType() == type) {
                    tmpListOfBlockLifeSpans.add(blockLifeSpans.get(j));
                }
            }
            groundTruth_text.add(tmpListOfBlockLifeSpans);
        }
        return groundTruth_text;
    }
}
