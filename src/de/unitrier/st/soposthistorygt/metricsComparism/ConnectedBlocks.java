package de.unitrier.st.soposthistorygt.metricsComparism;

public class ConnectedBlocks {

    Integer leftLocalId;
    Integer rightLocalId;
    int postBlockTypeId;

    public ConnectedBlocks(Integer leftLocalId, Integer rightLocalId, Integer postBlockTypeId){
        this.leftLocalId = leftLocalId;
        this.rightLocalId = rightLocalId;
        this.postBlockTypeId = postBlockTypeId;
    }

    @Override
    public String toString(){
        return "(" + leftLocalId + ", " + rightLocalId + ")";
    }
}
