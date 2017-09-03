package de.unitrier.st.soposthistorygt.metricsComparism;

import java.util.Vector;

public class ConnectionsOfAllVersions extends Vector<ConnectionsOfTwoVersions>{

    int postId;

    public ConnectionsOfAllVersions(int postId){
        this.postId = postId;
    }

}
