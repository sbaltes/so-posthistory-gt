package de.unitrier.st.soposthistorygt.tests;

import de.unitrier.st.soposthistorygt.metricsComparism.MetricsComparator;
import de.unitrier.st.soposthistorygt.util.BlockLifeSpan;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static de.unitrier.st.soposthistorygt.metricsComparism.GroundTruthExtractionOfCSVs.extractExportedCsvToListOfBlockLifeSpans;
import static de.unitrier.st.soposthistorygt.metricsComparism.GroundTruthExtractionOfCSVs.extractListOfListsOfBlockLifeSpansOfAllExportedCSVs;
import static de.unitrier.st.soposthistorygt.metricsComparism.GroundTruthExtractionOfCSVs.filterListOfListOfBlockLifeSpansByType;

public class MetricsComparismTest {

    @Test
    public void testExtractionOfOneCompletedCSV() throws IOException {
        List<BlockLifeSpan> blockLifeSpans = extractExportedCsvToListOfBlockLifeSpans(System.getProperty("user.dir") + "\\postVersionLists\\completed_3758880.csv"); // https://stackoverflow.com/a/13011927
        System.out.println(blockLifeSpans);
    }

    @Test
    public void testExtractionOfAllExportedCSVs() throws IOException {
        extractListOfListsOfBlockLifeSpansOfAllExportedCSVs(System.getProperty("user.dir") + "\\data\\Completed_PostId_VersionCount_SO_17-06_sample_100_1_files\\Completed_PostId_VersionCount_SO_17-06_sample_100_1_files"); // https://stackoverflow.com/a/13011927
    }

    @Test
    public void testFilteringOfExtractionOfAllExportedCSVs() throws IOException {
        List<List<BlockLifeSpan>> groundTruth = extractListOfListsOfBlockLifeSpansOfAllExportedCSVs(System.getProperty("user.dir") + "\\data\\"); // https://stackoverflow.com/a/13011927
        List<List<BlockLifeSpan>> groundTruth_text = filterListOfListOfBlockLifeSpansByType(groundTruth, BlockLifeSpan.Type.textblock);
        List<List<BlockLifeSpan>> groundTruth_code = filterListOfListOfBlockLifeSpansByType(groundTruth, BlockLifeSpan.Type.codeblock);

        System.out.println(groundTruth_code);
    }

    @Test
    public void testComparismOfGroundTruthAndExtractedPostVersionLists() throws IOException {
        String pathToAllCompletedCSVs = System.getProperty("user.dir") + "\\postVersionLists";
        String pathToPostHistories = System.getProperty("user.dir") + "\\postVersionLists";

        MetricsComparator metricsComparator = new MetricsComparator(
                pathToPostHistories,
                pathToAllCompletedCSVs);

        List<BlockLifeSpan> blockLifeSpans_computed_text = BlockLifeSpan.getLifeSpansOfAllBlocks(metricsComparator.postVersionsListManagement.getPostVersionListWithID(3758880), BlockLifeSpan.Type.textblock);
        List<BlockLifeSpan> blockLifeSpans_computed_code = BlockLifeSpan.getLifeSpansOfAllBlocks(metricsComparator.postVersionsListManagement.getPostVersionListWithID(3758880), BlockLifeSpan.Type.codeblock);

        System.out.println("Block Life Spans of Ground truth and computed (text): ");
        System.out.println(metricsComparator.groundTruthBlocks_text);
        System.out.println(blockLifeSpans_computed_text);

        System.out.println("Block Life Spans of Ground truth and computed (code): ");
        System.out.println(metricsComparator.groundTruthBlocks_code);
        System.out.println(blockLifeSpans_computed_code);
    }

    @Test
    public void testMetricsComparism() throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.reset();
        stopWatch.start();

        String pathToAllCompletedCSVs = System.getProperty("user.dir") + "\\postVersionLists";
        String pathToPostHistories = System.getProperty("user.dir") + "\\postVersionLists";

        MetricsComparator metricsComparator = new MetricsComparator(
                pathToPostHistories,
                pathToAllCompletedCSVs);

        metricsComparator.createStatisticsFiles();

        stopWatch.stop();
        System.out.println(stopWatch.getTime() + " milliseconds overall");
    }
}
