package de.unitrier.st.soposthistory.gt.GroundTruthApp;

import javax.swing.*;


class BlockPair {

    JLabel labelLeftBlock;
    JLabel labelRightBlock;
    boolean clickedBlockIsInstanceOfTextBlockVersion;

    int leftBlockPosition;
    int rightBlockPosition;


    BlockPair(
            JLabel labelLeftBlock,
            JLabel labelRightBlock,
            boolean clickedBlockIsInstanceOfTextBlockVersion,
            int leftBlockPosition,
            int rightBlockPosition) {

        this.labelLeftBlock = labelLeftBlock;
        this.labelRightBlock = labelRightBlock;
        this.clickedBlockIsInstanceOfTextBlockVersion = clickedBlockIsInstanceOfTextBlockVersion;

        this.leftBlockPosition = leftBlockPosition;
        this.rightBlockPosition = rightBlockPosition;
    }


    @Override
    public boolean equals(Object blockPair){
        return (blockPair instanceof BlockPair) && ((BlockPair) blockPair).labelLeftBlock.equals(this.labelLeftBlock)
                && ((BlockPair) blockPair).labelRightBlock.equals(this.labelRightBlock)
                && ((BlockPair) blockPair).clickedBlockIsInstanceOfTextBlockVersion == this.clickedBlockIsInstanceOfTextBlockVersion
                && ((BlockPair) blockPair).leftBlockPosition == this.leftBlockPosition
                && ((BlockPair) blockPair).rightBlockPosition == this.rightBlockPosition;
    }
}
