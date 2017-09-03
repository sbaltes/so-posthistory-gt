package de.unitrier.st.soposthistorygt.metricsComparism;

import java.util.Vector;

public class ConnectionsOfTwoVersions extends Vector<ConnectedBlocks> {

    int leftVersionId;
    int rightVersionId;

    public ConnectionsOfTwoVersions(int leftVersionId){
        this.leftVersionId = leftVersionId;
        this.rightVersionId = leftVersionId + 1;
    }
}