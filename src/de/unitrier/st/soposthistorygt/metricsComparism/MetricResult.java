package de.unitrier.st.soposthistorygt.metricsComparism;

import org.apache.commons.lang3.time.StopWatch;

import java.util.Objects;

public class MetricResult {

    StopWatch stopWatch = new StopWatch();
    public ConnectionsOfAllVersions connectionsOfAllVersions_text;
    public ConnectionsOfAllVersions connectionsOfAllVersions_code;


    long totalTimeMeasured_text = 0;
    long totalTimeMeasured_code = 0;

    int truePositives_text = 0;
    int falsePositives_text = 0;
    int trueNegatives_text = 0;
    int falseNegatives_text = 0;

    int truePositives_code = 0;
    int falsePositives_code = 0;
    int trueNegatives_code = 0;
    int falseNegatives_code = 0;


    public MetricResult(){}

    public void updateMetricResult_text() {
        this.totalTimeMeasured_text += this.stopWatch.getTime();
    }

    public void updateMetricResult_code() {
        this.totalTimeMeasured_code += this.stopWatch.getTime();
    }


    void compareMetricResults_text(ConnectionsOfAllVersions groundTruth){

        for(int i=0; i<groundTruth.size(); i++){
            for(int j=0; j<groundTruth.get(i).size(); j++){
                ConnectedBlocks connectedBlocksGroundTruth = groundTruth.get(i).get(j);
                ConnectedBlocks connectedBlocksComputedMetric = connectionsOfAllVersions_text.get(i).get(j);

                if(Objects.equals(connectedBlocksGroundTruth.rightLocalId, connectedBlocksComputedMetric.rightLocalId)){
                    if(connectedBlocksGroundTruth.rightLocalId != null){
                        truePositives_text++;
                    }else{
                        trueNegatives_text++;
                    }
                }else{
                    if(connectedBlocksGroundTruth.rightLocalId == null){
                        falsePositives_text++;
                    }else{
                        falseNegatives_text++;
                    }
                }

            }
        }
    }

    void compareMetricResults_code(ConnectionsOfAllVersions groundTruth){

        for(int i=0; i<groundTruth.size(); i++){
            for(int j=0; j<groundTruth.get(i).size(); j++){
                ConnectedBlocks connectedBlocksGroundTruth = groundTruth.get(i).get(j);
                ConnectedBlocks connectedBlocksComputedMetric = connectionsOfAllVersions_code.get(i).get(j);

                if(Objects.equals(connectedBlocksGroundTruth.rightLocalId, connectedBlocksComputedMetric.rightLocalId)){
                    if(connectedBlocksGroundTruth.rightLocalId != null){
                        truePositives_code++;
                    }else{
                        trueNegatives_code++;
                    }
                }else{
                    if(connectedBlocksGroundTruth.rightLocalId == null){
                        falsePositives_code++;
                    }else{
                        falseNegatives_code++;
                    }
                }

            }
        }
    }
}
