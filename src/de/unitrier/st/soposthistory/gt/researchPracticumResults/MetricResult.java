package de.unitrier.st.soposthistory.gt.researchPracticumResults;
/*
import de.unitrier.st.soposthistorygt.util.BlockLifeSpan;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Vector;

public class MetricResult {

    StopWatch stopWatch = new StopWatch();
    public Vector<BlockLifeSpan> lifeSpansOfAllBlocks_text;
    public Vector<BlockLifeSpan> lifeSpansOfAllBlocks_code;


    long maxTimeMeasured_text = 0;
    long totalTimeMeasured_text = 0;
    double averageTimeMeasured_text = 0;

    int countedHistories_text = 0;

    int numberOfSplittings_text = 0;
    int numberOfFalsePositives_text = 0;


    long maxTimeMeasured_code = 0;
    long totalTimeMeasured_code = 0;
    double averageTimeMeasured_code = 0;

    int countedHistories_code = 0;

    int numberOfSplittings_code = 0;
    int numberOfFalsePositives_code = 0;


    public MetricResult() {
//        stopWatch.start();
    }

    public void updateMetricResult_text() {
        if (this.maxTimeMeasured_text < this.stopWatch.getTime()) {
            this.maxTimeMeasured_text = this.stopWatch.getTime();
        }
        this.totalTimeMeasured_text += this.stopWatch.getTime();
        this.countedHistories_text++;
    }

    public void updateMetricResult_code() {
        if (this.maxTimeMeasured_code < this.stopWatch.getTime()) {
            this.maxTimeMeasured_code = this.stopWatch.getTime();
        }
        this.totalTimeMeasured_code += this.stopWatch.getTime();
        this.countedHistories_code++;
    }

    public void calculateAverageTime() {
        averageTimeMeasured_text = countedHistories_text > 0 ? (double)totalTimeMeasured_text / countedHistories_text : 0;
        averageTimeMeasured_code = countedHistories_code > 0 ? (double)totalTimeMeasured_code / countedHistories_code : 0;
    }
}
*/