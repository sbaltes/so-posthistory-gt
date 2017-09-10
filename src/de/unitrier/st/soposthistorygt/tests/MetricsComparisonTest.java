package de.unitrier.st.soposthistorygt.tests;

import de.unitrier.st.soposthistory.blocks.TextBlockVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;
import de.unitrier.st.soposthistorygt.metricsComparism.ConnectionsOfAllVersions;
import de.unitrier.st.soposthistorygt.metricsComparism.GroundTruthExtractionOfCSVs;
import de.unitrier.st.soposthistorygt.metricsComparism.MetricsComparator;
import de.unitrier.st.soposthistorygt.metricsComparism.PostVersionsListManagement;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MetricsComparisonTest {

    private static Path pathToCSVs = Paths.get("testdata", "comparison");

    @Test
    public void testExtraction(){

        int postId = 3758880;

        GroundTruthExtractionOfCSVs groundTruthExtractionOfCSVs = new GroundTruthExtractionOfCSVs(pathToCSVs.toString());
        PostVersionsListManagement postVersionsListManagement = new PostVersionsListManagement(pathToCSVs.toString());
        postVersionsListManagement.getPostVersionListWithID(postId).processVersionHistory();

        ConnectionsOfAllVersions connectionsOfAllVersionsGroundTruth_text = groundTruthExtractionOfCSVs.getAllConnectionsOfAllConsecutiveVersions_text(postId);
        ConnectionsOfAllVersions connectionsOfAllVersionsGroundTruth_code = groundTruthExtractionOfCSVs.getAllConnectionsOfAllConsecutiveVersions_code(postId);
        ConnectionsOfAllVersions connectionsOfAllVersionsComputedMetric_text = postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_text(postId);
        ConnectionsOfAllVersions connectionsOfAllVersionsComputedMetric_code = postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_code(postId);


        System.out.println("Ground Truth: ");
        System.out.println("All text blocks:");
        for(int i=0; i<connectionsOfAllVersionsGroundTruth_text.size(); i++){
            System.out.println(connectionsOfAllVersionsGroundTruth_text.get(i));
        }
        System.out.println("\nAll code blocks:");
        for(int i=0; i<connectionsOfAllVersionsGroundTruth_code.size(); i++){
            System.out.println(connectionsOfAllVersionsGroundTruth_code.get(i));
        }


        System.out.println("\n\nComputed Metric: ");
        System.out.println("All text blocks:");
        for(int i=0; i<connectionsOfAllVersionsComputedMetric_text.size(); i++){
            System.out.println(connectionsOfAllVersionsComputedMetric_text.get(i));
        }
        System.out.println("\n\nAll code blocks:");
        for(int i=0; i<connectionsOfAllVersionsComputedMetric_code.size(); i++){
            System.out.println(connectionsOfAllVersionsComputedMetric_code.get(i));
        }
    }

    @Test
    public void testMetricsComparism() throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.reset();
        stopWatch.start();

        MetricsComparator metricsComparator = new MetricsComparator(
                pathToCSVs.toString(),
                pathToCSVs.toString());

        metricsComparator.createStatisticsFiles();

        stopWatch.stop();
        System.out.println(stopWatch.getTime() + " milliseconds overall");
    }

    @Test
    public void testNumberOfPredecessors(){
        int postId = 3758880;
        TextBlockVersion.similarityMetric = de.unitrier.st.stringsimilarity.set.Variants::twoGramDiceVariant;

        GroundTruthExtractionOfCSVs groundTruthExtractionOfCSVs = new GroundTruthExtractionOfCSVs(pathToCSVs.toString());
        PostVersionsListManagement postVersionsListManagement = new PostVersionsListManagement(pathToCSVs.toString());
        postVersionsListManagement.getPostVersionListWithID(postId).processVersionHistory(PostVersionList.PostBlockTypeFilter.TEXT);


        List<TextBlockVersion> textBlocks = postVersionsListManagement.getPostVersionListWithID(postId).get(postVersionsListManagement.getPostVersionListWithID(postId).size()-1).getTextBlocks();
        for(int i=0; i<textBlocks.size(); i++){
            System.out.println(textBlocks.get(i).getLocalId() + " has pred " + textBlocks.get(i).getPred().getLocalId());
        }
        System.out.println();


        ConnectionsOfAllVersions connectionsOfAllVersionsGroundTruth_text = groundTruthExtractionOfCSVs.getAllConnectionsOfAllConsecutiveVersions_text(postId);
        ConnectionsOfAllVersions connectionsOfAllVersionsComputedMetric_text = postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_text(postId);


        System.out.println("Ground Truth: ");
        System.out.println("All text blocks:");
        for(int i=0; i<connectionsOfAllVersionsGroundTruth_text.size(); i++){
            System.out.println(connectionsOfAllVersionsGroundTruth_text.get(i));
        }

        System.out.println("\n\nComputed Metric: ");
        System.out.println("All text blocks:");
        for(int i=0; i<connectionsOfAllVersionsComputedMetric_text.size(); i++){
            System.out.println(connectionsOfAllVersionsComputedMetric_text.get(i));
        }
    }

    @Test
    public void checkWhetherPostVersionListConnectionsWillBeResetRight(){
        int postId = 3758880;
        TextBlockVersion.similarityMetric = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTokenDiceVariant;

        PostVersionsListManagement postVersionsListManagement = new PostVersionsListManagement(pathToCSVs.toString());
        postVersionsListManagement.getPostVersionListWithID(postId).processVersionHistory(PostVersionList.PostBlockTypeFilter.TEXT);


        ConnectionsOfAllVersions connectionsOfAllVersionsComputedMetric_text = postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_text(postId);

        System.out.println("\n\nComputed Metric after processVersionHistory: ");
        System.out.println("All text blocks:");
        for(int i=0; i<connectionsOfAllVersionsComputedMetric_text.size(); i++){
            System.out.println(connectionsOfAllVersionsComputedMetric_text.get(i));
        }


        postVersionsListManagement = new PostVersionsListManagement(pathToCSVs.toString());
        connectionsOfAllVersionsComputedMetric_text = postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_text(postId);

        System.out.println("\n\nComputed Metric after resetting of links: ");
        System.out.println("All text blocks:");
        for(int i=0; i<connectionsOfAllVersionsComputedMetric_text.size(); i++){
            System.out.println(connectionsOfAllVersionsComputedMetric_text.get(i));
        }
    }
}
