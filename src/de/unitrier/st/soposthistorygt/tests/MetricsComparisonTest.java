package de.unitrier.st.soposthistorygt.tests;

import de.unitrier.st.soposthistorygt.metricsComparism.ConnectionsOfAllVersions;
import de.unitrier.st.soposthistorygt.metricsComparism.GroundTruthExtractionOfCSVs;
import de.unitrier.st.soposthistorygt.metricsComparism.MetricsComparator;
import de.unitrier.st.soposthistorygt.metricsComparism.PostVersionsListManagement;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class MetricsComparisonTest {

    @Test
    public void testExtraction(){

        int postId = 3758880;

        String pathToCSV = System.getProperty("user.dir") + "\\src\\de\\unitrier\\st\\soposthistorygt\\tests";

        GroundTruthExtractionOfCSVs groundTruthExtractionOfCSVs = new GroundTruthExtractionOfCSVs(pathToCSV);
        PostVersionsListManagement postVersionsListManagement = new PostVersionsListManagement(pathToCSV);

        ConnectionsOfAllVersions connectionsOfAllVersionsGroundTruth_text = groundTruthExtractionOfCSVs.getAllConnectionsOfAllConsecutiveVersions_text(postId);
        ConnectionsOfAllVersions connectionsOfAllVersionsGroundTruth_code = groundTruthExtractionOfCSVs.getAllConnectionsOfAllConsecutiveVersions_code(postId);
        ConnectionsOfAllVersions connectionsOfAllVersionsComputedMetric_text = postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_text(postId);
        ConnectionsOfAllVersions connectionsOfAllVersionsComputedMetric_code = postVersionsListManagement.getAllConnectionsOfAllConsecutiveVersions_code(postId);


        System.out.println("Ground Truth: ");
        System.out.println("All text blocks:");
        for(int i=0; i<connectionsOfAllVersionsGroundTruth_text.size(); i++){
            System.out.println(connectionsOfAllVersionsGroundTruth_text.get(i));
        }
        System.out.println("\n\nAll code blocks:");
        for(int i=0; i<connectionsOfAllVersionsGroundTruth_code.size(); i++){
            System.out.println(connectionsOfAllVersionsGroundTruth_code.get(i));
        }


        System.out.println("Computed Metric: ");
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

        String pathToAllCompletedCSVs = System.getProperty("user.dir") + "\\src\\de\\unitrier\\st\\soposthistorygt\\tests";
        String pathToPostHistories = System.getProperty("user.dir") + "\\src\\de\\unitrier\\st\\soposthistorygt\\tests";

        MetricsComparator metricsComparator = new MetricsComparator(
                pathToPostHistories,
                pathToAllCompletedCSVs);

        metricsComparator.createStatisticsFiles();

        stopWatch.stop();
        System.out.println(stopWatch.getTime() + " milliseconds overall");
    }
}
