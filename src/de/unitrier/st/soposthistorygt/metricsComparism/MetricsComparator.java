package de.unitrier.st.soposthistorygt.metricsComparism;


import de.unitrier.st.soposthistory.blocks.CodeBlockVersion;
import de.unitrier.st.soposthistory.blocks.TextBlockVersion;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;
import java.util.function.BiFunction;


public class MetricsComparator{

    private String pathToDirectoryOfAllPostHistories;
    private String pathToDirectoryOfAllCompletedCSVs;

    public static PostVersionsListManagement postVersionsListManagement;
    public static GroundTruthExtractionOfCSVs groundTruthExtractionOfCSVs;


    // constructor and helping methods
    public MetricsComparator(String pathToDirectoryOfPostHistories, String pathToDirectoryOfCompletedCSVs){

        this.pathToDirectoryOfAllPostHistories = pathToDirectoryOfPostHistories;
        this.pathToDirectoryOfAllCompletedCSVs = pathToDirectoryOfCompletedCSVs;

        postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfPostHistories);
        groundTruthExtractionOfCSVs = new GroundTruthExtractionOfCSVs(pathToDirectoryOfCompletedCSVs);

        checkWhetherSetOfCompletedPostsIsSameAsSetOfPostHistories();

        checkWhetherNumberOfBlocksIsSame();
    }

    private void checkWhetherSetOfCompletedPostsIsSameAsSetOfPostHistories() {
        Vector<Integer> postIdsOfGroundTruth = new Vector<>();
        Vector<Integer> postIdsOfPostHistories = new Vector<>();
        for(int i=0; i<groundTruthExtractionOfCSVs.groundTruth.size(); i++){
            postIdsOfGroundTruth.add(groundTruthExtractionOfCSVs.groundTruth.get(i).postId);
        }

        for(int i=0; i<postVersionsListManagement.postVersionLists.size(); i++){
            postIdsOfPostHistories.add(postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId());
        }

        Collections.sort(postIdsOfGroundTruth);
        Collections.sort(postIdsOfPostHistories);

        if(!postIdsOfGroundTruth.equals(postIdsOfPostHistories)){
            Vector<Integer> invalidPosts = new Vector<>();
            for(int i=0; i<postIdsOfGroundTruth.size(); i++){
                if(!postIdsOfPostHistories.contains(postIdsOfGroundTruth.get(i))){
                    invalidPosts.add(postIdsOfGroundTruth.get(i));
                }
            }
            for(int i=0; i<postIdsOfPostHistories.size(); i++){
                if(!postIdsOfGroundTruth.contains(postIdsOfPostHistories.get(i))){
                    invalidPosts.add(postIdsOfPostHistories.get(i));
                }
            }

            System.err.println("Every post version list must have a corresponding completed csv, but doesn't.");
            System.err.println("Check the following post(s): " + invalidPosts);
            System.exit(0);
        }
    }

    private void checkWhetherNumberOfBlocksIsSame() {

        if(groundTruthExtractionOfCSVs.groundTruth_text.size() != postVersionsListManagement.postVersionLists.size()
                || groundTruthExtractionOfCSVs.groundTruth_code.size() != postVersionsListManagement.postVersionLists.size()){
            System.err.println("number of ground truths and number of post version lists are different");
        }

        for(int i=0; i<groundTruthExtractionOfCSVs.groundTruth_text.size(); i++){

            int currentPostId = groundTruthExtractionOfCSVs.groundTruth.get(i).postId;

            int numberOfTextBlocksOverallInGroundTruth = 0;
            for(int j=0; j<groundTruthExtractionOfCSVs.groundTruth_text.get(i).size(); j++){
                numberOfTextBlocksOverallInGroundTruth += groundTruthExtractionOfCSVs.groundTruth_text.get(i).get(j).size();
            }

            int numberOfTextBlocksOverallInComputedMetric = 0;
            for(int j=0; j<postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_text(currentPostId).size(); j++){
                numberOfTextBlocksOverallInComputedMetric += postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_text(currentPostId).get(j).size();
            }

            if(numberOfTextBlocksOverallInGroundTruth != numberOfTextBlocksOverallInComputedMetric){
                System.err.println(
                        "Number of text blocks that will be compared must be the same but are different in post with id "
                                + postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId() + ": "
                                + numberOfTextBlocksOverallInGroundTruth + " text blocks in ground truth and "
                                + numberOfTextBlocksOverallInComputedMetric + " text blocks in computed metric");
                System.err.println("ground truth:");
                System.err.println(groundTruthExtractionOfCSVs.getAllConnectionsOfAllConsecutiveVersions_text(postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId()));
                System.err.println("computed metric:");
                System.err.println(postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_text(postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId()));
                System.exit(0);
            }
        }

        for(int i=0; i<groundTruthExtractionOfCSVs.groundTruth_code.size(); i++){

            int currentPostId = groundTruthExtractionOfCSVs.groundTruth.get(i).postId;

            int numberOfCodeBlocksOverallInGroundTruth = 0;
            for(int j=0; j<groundTruthExtractionOfCSVs.groundTruth_code.get(i).size(); j++){
                numberOfCodeBlocksOverallInGroundTruth += groundTruthExtractionOfCSVs.groundTruth_code.get(i).get(j).size();
            }

            int numberOfCodeBlocksOverallInComputedMetric = 0;
            for(int j=0; j<postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_code(currentPostId).size(); j++){
                numberOfCodeBlocksOverallInComputedMetric += postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_code(currentPostId).get(j).size();
            }

            if(numberOfCodeBlocksOverallInGroundTruth != numberOfCodeBlocksOverallInComputedMetric){
                System.err.println(
                        "Number of code blocks that will be compared must be the same but are different in post with id "
                                + postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId() + ": "
                                + numberOfCodeBlocksOverallInGroundTruth + " code blocks in ground truth and "
                                + numberOfCodeBlocksOverallInComputedMetric + " code blocks in computed metric");
                System.err.println("ground truth:");
                System.err.println(groundTruthExtractionOfCSVs.getAllConnectionsOfAllConsecutiveVersions_code(postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId()));
                System.err.println("computed metric:");
                System.err.println(postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_code(postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId()));
                System.exit(0);
            }
        }
    }


    // computing similarity
    public MetricResult computeSimilarity_writeInResult_text(int postVersionListID, BiFunction<String, String, Double> metric) {

        MetricResult metricResult = new MetricResult();

        metricResult.stopWatch.reset();
        metricResult.stopWatch.start();
        TextBlockVersion.similarityMetric = metric;
        postVersionsListManagement.getPostVersionListWithID(postVersionListID).processVersionHistory();
        metricResult.stopWatch.stop();
        metricResult.stopWatch.getNanoTime();

        metricResult.connectionsOfAllVersions_text = postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_text(postVersionListID);
        metricResult.compareMetricResults_text(groundTruthExtractionOfCSVs.getAllConnectionsOfAllConsecutiveVersions_text(postVersionListID));

        metricResult.updateMetricResult_text();

        return metricResult;
    }

    public MetricResult computeSimilarity_writeInResult_code(int postVersionListID, BiFunction<String, String, Double> metric) {

        MetricResult metricResult = new MetricResult();

        metricResult.stopWatch.reset();
        metricResult.stopWatch.start();
        CodeBlockVersion.similarityMetric = metric;
        postVersionsListManagement.getPostVersionListWithID(postVersionListID).processVersionHistory();
        metricResult.stopWatch.stop();
        metricResult.stopWatch.getNanoTime();

        metricResult.connectionsOfAllVersions_code = postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_code(postVersionListID);
        metricResult.compareMetricResults_code(groundTruthExtractionOfCSVs.getAllConnectionsOfAllConsecutiveVersions_code(postVersionListID));

        metricResult.updateMetricResult_code();

        return metricResult;
    }



    public void createStatisticsFiles() throws IOException {

        MetricsComparator metricsComparator = new MetricsComparator(pathToDirectoryOfAllPostHistories, pathToDirectoryOfAllCompletedCSVs);


        Metric.Type[] metrics = Metric.Type.values().clone();

        for(int i=0; i<metrics.length; i++){
            Metric.Type tmp = metrics[i];
            int rnd = (int)(Math.random()*metrics.length);
            metrics[i] = metrics[rnd];
            metrics[rnd] = tmp;
        }

        //for test
        //Type[] metrics = new Type[1];
        //metrics[0] = Type.overlapNormalizedTokens;
        //

        PrintWriter[] printWriters = new PrintWriter[10];
        printWriters[0] = new PrintWriter(new File("./metric results/total time for all PostVersionLists measured (text)" + ".csv"));
        printWriters[1] = new PrintWriter(new File("./metric results/similarity (true positives) (text)" + ".csv"));
        printWriters[2] = new PrintWriter(new File("./metric results/similarity (false positives) (text)" + ".csv"));
        printWriters[3] = new PrintWriter(new File("./metric results/similarity (true negatives) (text)" + ".csv"));
        printWriters[4] = new PrintWriter(new File("./metric results/similarity (false negatives) (text)" + ".csv"));

        printWriters[5] = new PrintWriter(new File("./metric results/total time for 10 PostVersionLists measured (code)" + ".csv"));
        printWriters[6] = new PrintWriter(new File("./metric results/similarity (true positives) (code)" + ".csv"));
        printWriters[7] = new PrintWriter(new File("./metric results/similarity (false positives) (code)" + ".csv"));
        printWriters[8] = new PrintWriter(new File("./metric results/similarity (true negatives) (code)" + ".csv"));
        printWriters[9] = new PrintWriter(new File("./metric results/similarity (false negatives) (code)" + ".csv"));

        for(int i=0; i<metrics.length+1; i++) {
//        for(int i=0; i<=3; i++) {             // TODO : use this for testing

            for (int j = 0; j < postVersionsListManagement.postVersionLists.size()+1; j++) {

                if(i == 0 && j == 0){
                    printWriters[0].write("total time for all PostVersionLists measured (text)");
                    printWriters[1].write("similarity (true positives) (text)");
                    printWriters[2].write("similarity (false positives) (text)");
                    printWriters[3].write("similarity (true negatives) (text)");
                    printWriters[4].write("similarity (false negatives) (text)");

                    printWriters[5].write("total time for all PostVersionLists measured (code)");
                    printWriters[6].write("similarity (true positives) (code)");
                    printWriters[7].write("similarity (false positives) (code)");
                    printWriters[8].write("similarity (true negatives) (code)");
                    printWriters[9].write("similarity (false negatives) (code)");
                }else if(i == 0){
                    for (PrintWriter printWriter : printWriters) {
                        printWriter.write(postVersionsListManagement.postVersionLists.get(j-1).getFirst().getPostId().toString());
                    }
                }else if(j == 0){
                    for (PrintWriter printWriter : printWriters) {
                        printWriter.write(metrics[i - 1].toString());
                    }
                }else{
                    try {
                        // postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfAllPostHistories);
                        // TODO? : metricsComparator.init();
                        // postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfAllPostHistories);
                        MetricResult tmpMetricResult
                                = metricsComparator.computeSimilarity_writeInResult_text(
                                postVersionsListManagement.postVersionLists.get(j-1).getFirst().getPostId(),
                                Metric.getBiFunctionMetric(metrics[i-1]));

                        printWriters[0].write(tmpMetricResult.totalTimeMeasured_text + "");
                        printWriters[1].write(tmpMetricResult.truePositives_text + "");
                        printWriters[2].write(tmpMetricResult.falsePositives_text + "");
                        printWriters[3].write(tmpMetricResult.trueNegatives_text + "");
                        printWriters[4].write(tmpMetricResult.falseNegatives_text + "");

                        // postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfAllPostHistories);
                        // TODO? : metricsComparator.init();
                        // postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfAllPostHistories);
                        tmpMetricResult
                                = metricsComparator.computeSimilarity_writeInResult_code(
                                postVersionsListManagement.postVersionLists.get(j-1).getFirst().getPostId(),
                                Metric.getBiFunctionMetric(metrics[i - 1]));
                        printWriters[5].write(tmpMetricResult.totalTimeMeasured_code + "");
                        printWriters[6].write(tmpMetricResult.truePositives_code + "");
                        printWriters[7].write(tmpMetricResult.falsePositives_code + "");
                        printWriters[8].write(tmpMetricResult.trueNegatives_code + "");
                        printWriters[9].write(tmpMetricResult.falseNegatives_code + "");

                    }catch (Exception e){
                        for (PrintWriter printWriter : printWriters) {
                            printWriter.write("no result");
                        }
                    }
                }

                if(j < postVersionsListManagement.postVersionLists.size()){
                    for (PrintWriter printWriter : printWriters) {
                        printWriter.write(", ");
                    }
                }

            }
            for (PrintWriter printWriter : printWriters) {
                printWriter.write("\n");
            }

            for (PrintWriter printWriter : printWriters) {
                printWriter.flush();
            }

            if(i>0)
                System.out.println("metric " + metrics[i-1] + " completed (" + i + " of " + metrics.length + ")");
        }

        for (PrintWriter printWriter : printWriters) {
            printWriter.close();
        }
    }
}
