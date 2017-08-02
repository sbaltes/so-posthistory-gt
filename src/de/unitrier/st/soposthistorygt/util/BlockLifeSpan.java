package de.unitrier.st.soposthistorygt.util;

import de.unitrier.st.soposthistory.blocks.CodeBlockVersion;
import de.unitrier.st.soposthistory.blocks.PostBlockVersion;
import de.unitrier.st.soposthistory.blocks.TextBlockVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;

import java.util.Vector;

public class BlockLifeSpan extends Vector<BlockLifeSpanSnapshot> {

    public enum Type{ codeblock, textblock }

    private Type type;


    public static int getNumberOfSnapshots(Vector<BlockLifeSpan> blockLifeSpans){
        int numberOfSnapshots = 0;

        for (BlockLifeSpan blockLifeSpan : blockLifeSpans) {
            numberOfSnapshots += blockLifeSpan.size();
        }

        return numberOfSnapshots;
    }
    
    public static String printVectorOfLifeSpans(Vector<BlockLifeSpan> blockLifeSpans){

        StringBuilder sb = new StringBuilder();

        sb.append("number of snapshots: ");
        sb.append(getNumberOfSnapshots(blockLifeSpans));
        sb.append("\n");

        for (BlockLifeSpan blockLifeSpan : blockLifeSpans) {
            sb.append(blockLifeSpan);
        }

        return  sb.toString();
    }


    // ****** extract LifeSpans
    public static Vector<BlockLifeSpan> getLifeSpansOfAllBlocks(PostVersionList postVersions, Type type){ // TODO: optimize getLifeSpansOfAllBlocks to O(n*m) for n versions and m blocks

        Vector<BlockLifeSpan> blockLifeSpansOutput = new Vector<>();

        for(int i=0; i<postVersions.size(); i++){
            for(int j=0; j<postVersions.get(i).getPostBlocks().size(); j++){

                switch (type){
                    case codeblock:
                        if((postVersions.get(i).getPostBlocks().get(j) instanceof TextBlockVersion))
                            continue;
                        break;

                    case textblock:
                        if((postVersions.get(i).getPostBlocks().get(j) instanceof CodeBlockVersion))
                            continue;
                        break;
                }

                Integer tmpPredId = null;
                try {
                    tmpPredId = postVersions.get(i).getPostBlocks().get(j).getPred().getLocalId();
                }catch(Exception e){}

                BlockLifeSpanSnapshot tmpLifeSnapshot
                        = new BlockLifeSpanSnapshot(
                                postVersions.get(i).getPostBlocks().get(j).getId(),
                                postVersions.get(i).getPostHistoryId(),
                                i+1,
                                j+1);

                if(tmpPredId == null){
                    BlockLifeSpan tmpBlockLifeSpan = null;
                    if(postVersions.get(i).getPostBlocks().get(j) instanceof TextBlockVersion)
                        tmpBlockLifeSpan = new BlockLifeSpan(BlockLifeSpan.Type.textblock);
                    else if(postVersions.get(i).getPostBlocks().get(j) instanceof CodeBlockVersion)
                        tmpBlockLifeSpan = new BlockLifeSpan(BlockLifeSpan.Type.codeblock);
                    tmpBlockLifeSpan.add(tmpLifeSnapshot);

                    PostBlockVersion tmpBlock = postVersions.get(i).getPostBlocks().get(j);
                    int k=i+1;
                    while(k < postVersions.size()){
                        boolean found = false;
                        for(int l=0; l<postVersions.get(k).getPostBlocks().size(); l++){
                            if(postVersions.get(k).getPostBlocks().get(l).getPred() == tmpBlock){
                                tmpLifeSnapshot
                                        = new BlockLifeSpanSnapshot(
                                                postVersions.get(i).getPostBlocks().get(j).getId(),
                                                postVersions.get(i).getPostHistoryId(),
                                                k+1,
                                                l+1);
                                tmpBlock = postVersions.get(k).getPostBlocks().get(l);
                                found = true;
                                tmpBlockLifeSpan.add(tmpLifeSnapshot);
                                break;
                            }
                        }
                        if(found)
                            k++;
                        else
                            break;
                    }

                    blockLifeSpansOutput.add(tmpBlockLifeSpan);
                }
            }
        }
        return blockLifeSpansOutput;
    }


    public BlockLifeSpan(Type type){
        this.type = type;
    }

    public Type getType(){
        return type;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(BlockLifeSpanSnapshot snapshot : this){
            sb.append(snapshot);
        }
        return type + ": " + sb + "\n";
    }

}
