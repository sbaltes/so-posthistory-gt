package de.unitrier.st.soposthistory.gt.GroundTruthApp;

import org.sotorrent.posthistoryextractor.diffs.LineDiff;
import org.sotorrent.posthistoryextractor.diffs.diff_match_patch;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class BlockPair {

    PostBlockWebView leftBlock;
    PostBlockWebView rightBlock;
    int leftVersion;

    BlockPair(
            PostBlockWebView leftBlock,
            PostBlockWebView rightBlock,
            int leftVersion) {

        this.leftBlock = leftBlock;
        this.rightBlock = rightBlock;
        this.leftVersion = leftVersion;
    }


    @Override
    public boolean equals(Object blockPair){
        return (blockPair instanceof BlockPair) && ((BlockPair) blockPair).leftBlock.equals(this.rightBlock)
                && ((BlockPair) blockPair).leftBlock.equals(this.rightBlock)
                && ((BlockPair) blockPair).leftVersion == this.leftVersion;
    }

}
