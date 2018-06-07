package de.unitrier.st.soposthistory.gt.GroundTruthApp;

import javafx.scene.web.WebView;
import org.sotorrent.posthistoryextractor.blocks.PostBlockVersion;

public class PostBlockWebView {

    WebView webView = new WebView();
    PostBlockVersion postBlock;
    boolean isLeftVersion;

    PostBlockWebView(PostBlockVersion postBlockVersion, boolean isLeftVersion, int version) {
        this.postBlock = postBlockVersion;
        this.isLeftVersion = isLeftVersion;
    }

    @Override
    public boolean equals (Object postBlockWebView) {
        return this.postBlock.getPostHistoryId().equals(((PostBlockWebView) postBlockWebView).postBlock.getPostHistoryId())
                && this.postBlock.getLocalId().equals(((PostBlockWebView) postBlockWebView).postBlock.getLocalId());
    }
}
