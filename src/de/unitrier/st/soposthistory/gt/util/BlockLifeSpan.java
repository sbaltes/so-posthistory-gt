package de.unitrier.st.soposthistory.gt.util;

import de.unitrier.st.soposthistory.blocks.CodeBlockVersion;
import de.unitrier.st.soposthistory.blocks.PostBlockVersion;
import de.unitrier.st.soposthistory.blocks.TextBlockVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;

import java.util.List;
import java.util.LinkedList;

public class BlockLifeSpan extends LinkedList<BlockLifeSpanSnapshot> {

    public enum Type{ codeblock, textblock }

    private Type type;


    public static int getNumberOfSnapshots(LinkedList<BlockLifeSpan> blockLifeSpans){
        int numberOfSnapshots = 0;

        for (BlockLifeSpan blockLifeSpan : blockLifeSpans) {
            numberOfSnapshots += blockLifeSpan.size();
        }

        return numberOfSnapshots;
    }
    
    public static String printLinkedListOfLifeSpans(LinkedList<BlockLifeSpan> blockLifeSpans){

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
    public static List<BlockLifeSpan> getLifeSpansOfAllBlocks(PostVersionList postVersions, Type type){ // TODO: optimize getLifeSpansOfAllBlocks to O(n*m) for n versions and m blocks

        List<BlockLifeSpan> listOfBlockLifeSpans = new LinkedList<>();

        for(int i=0; i<postVersions.size(); i++){
            for(int j=0; j<postVersions.get(i).getPostBlocks().size(); j++){

                PostBlockVersion tmpPostBlock = postVersions.get(i).getPostBlocks().get(j);

                switch (type){
                    case codeblock:
                        if((tmpPostBlock instanceof TextBlockVersion))
                            continue;
                        break;

                    case textblock:
                        if((tmpPostBlock instanceof CodeBlockVersion))
                            continue;
                        break;
                }

                BlockLifeSpanSnapshot tmpLifeSnapshot
                        = new BlockLifeSpanSnapshot(
                        tmpPostBlock.getId(),
                        postVersions.get(i).getPostHistoryId(),
                        i+1,
                        j+1);

                boolean chainExists = false;
                for(int k=0; k<listOfBlockLifeSpans.size(); k++){
                    if((listOfBlockLifeSpans.get(k).getLast().getVersion() == i)
                            && (tmpPostBlock.getPred() != null)
                            && (tmpPostBlock.getPred().getLocalId() == listOfBlockLifeSpans.get(k).getLast().getLocalId())){
                        chainExists = true;
                        listOfBlockLifeSpans.get(k).add(tmpLifeSnapshot);
                        break;
                    }
                }

                if(!chainExists) {
                    BlockLifeSpan tmpBlockLifeSpan = new BlockLifeSpan(type);
                    tmpBlockLifeSpan.add(tmpLifeSnapshot);
                    listOfBlockLifeSpans.add(tmpBlockLifeSpan);
                }
            }
        }

        return listOfBlockLifeSpans;
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
