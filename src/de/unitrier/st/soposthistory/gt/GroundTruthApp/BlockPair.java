package de.unitrier.st.soposthistory.gt.GroundTruthApp;

import javax.swing.*;
import java.util.logging.Level;

import static de.unitrier.st.soposthistory.history.PostHistoryIterator.logger;


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

        if(!(blockPair instanceof BlockPair)){  // TODO for Sebastian: Intellij proposes this. Is this too much overhead?
            logger.log(Level.WARNING, "");
            throw new ClassCastException("Failed casting instance which should be of type BlockPair");
        }

        return ((BlockPair) blockPair).labelLeftBlock.equals(this.labelLeftBlock)
                && ((BlockPair) blockPair).labelRightBlock.equals(this.labelRightBlock)
                && ((BlockPair) blockPair).clickedBlockIsInstanceOfTextBlockVersion == this.clickedBlockIsInstanceOfTextBlockVersion
                && ((BlockPair) blockPair).leftBlockPosition == this.leftBlockPosition
                && ((BlockPair) blockPair).rightBlockPosition == this.rightBlockPosition;

    }
}
