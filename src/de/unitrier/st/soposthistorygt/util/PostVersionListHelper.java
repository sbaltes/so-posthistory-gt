package de.unitrier.st.soposthistorygt.util;

import de.unitrier.st.soposthistory.blocks.PostBlockVersion;
import de.unitrier.st.soposthistory.version.PostVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;

import java.io.*;
import java.util.*;

public class PostVersionListHelper {

    public static void makeDocument(PostVersionList postVersionList){
        try {
            int version = 0;
            for(PostVersion postVersion : postVersionList){

                PrintWriter printWriter = new PrintWriter(new File("./testdata/every block in own document/" + postVersion.getPostId() + "_" + (version++)));

                printWriter.write("PostId: " + postVersion.getPostId() + "\n");
                printWriter.write("Id: " + postVersion.getPostHistoryId() + "\n");
                printWriter.write("Version: " + version + "\n");
                printWriter.write("Amount of text blocks: " + postVersion.getTextBlocks().size() + "\n");
                printWriter.write("Amount of code blocks: " + postVersion.getCodeBlocks().size() + "\n");
                printWriter.write("\n\n");

                for(int i=0; i<postVersion.getPostBlocks().size(); i++) {

                    PostBlockVersion tmpPostBlock = postVersion.getPostBlocks().get(i);
                    String[] lines = tmpPostBlock.getContent().split("&#xD;&#xA;");
                    for (String line : lines) {
                        printWriter.write(line + "\n");
                    }
                    printWriter.write("\n\n");
                }
                printWriter.write("\n\n");

                printWriter.flush();
                printWriter.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


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
                BlockLifeSpan blockLifeSpan = new BlockLifeSpan((postBlockTypeId == 1 ? BlockLifeSpan.Type.textblock : BlockLifeSpan.Type.codeblock));
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

        for(int k=0; k<lines.size(); k++){
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

            for(int i=listOfBlockLifeSpans.size()-1; i>=0; i--){
                if(listOfBlockLifeSpans.get(i).lastElement().getVersion() + 1 == mapPostHistoryId_to_version.get(postHistoryId)){
                    for(int j=0; j<listOfBlockLifeSpanSnapshots.size(); j++){

                        if(listOfBlockLifeSpanSnapshots.get(j).getPostHistoryId() == postHistoryId
                                && listOfBlockLifeSpanSnapshots.get(j).getLocalId() == predLocalId){
                            listOfBlockLifeSpans.get(i).add(listOfBlockLifeSpanSnapshots.get(j));
                            listOfBlockLifeSpanSnapshots.remove(j+0);
                            break;
                        }
                    }

                    break;
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

        List<BlockLifeSpan> listOfBlockLifeSpans = listOfBlockLifeSnapshots_to_listOfBlockLifeSpans(listOfBlockLifeSpanSnapshots, lines);

        System.out.println(listOfBlockLifeSpans);

        return listOfBlockLifeSpans;
    }

    public static List<List<BlockLifeSpan>> extractAllExportedCsvToListOfBlockLifeSpans(String directoryOf_postHistories_and_groundTruthCSVs){
        List<List<BlockLifeSpan>> groundTruth = new Vector<>();
        // TODO ...
        return null;
    }


    // TODO: remove
    public static void main(String[] args) throws IOException {
        extractExportedCsvToListOfBlockLifeSpans("C:\\Users\\Lorik\\Desktop\\completed_3758880.csv");
    }


}
