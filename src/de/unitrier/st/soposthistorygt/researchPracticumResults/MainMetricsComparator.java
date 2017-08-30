package de.unitrier.st.soposthistorygt.researchPracticumResults;

import org.apache.commons.lang3.time.StopWatch;

import java.io.FileNotFoundException;

public class MainMetricsComparator {

    public static void main(String[] args) throws FileNotFoundException {

        for(int i=0; i<1; i++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.reset();
            stopWatch.start();

            MetricsComparator metricsComparator = new MetricsComparator();
            metricsComparator.createStatisticsFiles(i+1);

            stopWatch.stop();
            System.out.println(stopWatch.getTime() + " milliseconds overall");
        }
    }
}
