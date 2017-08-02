package de.unitrier.st.soposthistorygt.util.anchorsURLs;

public class AnchorTextAndUrlPair {
    String anchor;
    String reference;
    String url;
    AnchorRefUrlType type;

    private static final String bareURL_lazy = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";    // https://stackoverflow.com/a/6041965
    private static final String bareURL2_greedy = "(?:(?:https?|ftp|file):\\/\\/|www\\.|ftp\\.)(?:\\([-A-Z0-9+&@#\\/%=~_|$?!:,.]*\\)|[-A-Z0-9+&@#\\/%=~_|$?!:,.])*(?:\\([-A-Z0-9+&@#\\/%=~_|$?!:,.]*\\)|[A-Z0-9+&@#\\/%=~_|$])";    // https://stackoverflow.com/a/29288898

    static final String regex_spanClassWithAnchorTextAndDirectURL = "<span.*?" + bareURL_lazy + ".*?<\\/span>";
    // static final String regex_anchorTextAndDirectURL = "\\[.*\\]\\(.*" + bareURL_lazy + "\\/?\\)";
    // static final String regex_anchorTextWithReferenceToURL = "\\[.*?\\]: " + bareURL_lazy + "|" + "\\[.*?\\]\\[.*?\\]";
    static final String regex_bareURL_wrappedInTags = "<" + bareURL_lazy + ">";
    // static final String regex_urlWrappedInHTMLsyntax = "<a href.*?" + bareURL_lazy + ".*?<\\/a>";
    static final String regex_urlWrappedInHTMLsyntax =  "<a href=\"" + bareURL_lazy + ".+?<\\/a>";
    static final String regex_bareURL_notWrappedInTags = bareURL_lazy;


    public enum AnchorRefUrlType{               // https://stackoverflow.com/editing-help#code
        spanClassWithAnchorTextAndDirectURL,    // Here's a <span class="hi">[poorly-named link](http://www.google.com/ "Google")</span>.
        bareURL_wrappedInTags,                  // Have you ever seen <http://example.com>?
        urlWrappedInHTMLsyntax,                 // <a href="http://example.com" title="example">example</a>
        anchorTextAndDirectURL,                 // e.g. Here's an inline link to [Google](http://www.google.com/).
        anchorTextWithReferenceToURL,           // e.g. Here's a reference-style link to [Google][1].   ...     and later   ...     [1]: http://www.google.com/
                                                // or Here's a very readable link to [Yahoo!][yahoo].      ...     and later   ...     [yahoo]: http://www.yahoo.com/
        bareURL_notWrappedInTags                // I often visit http://example.com.
    }

    public static String getRegexWithEnum(AnchorRefUrlType type){
        switch (type){
            case spanClassWithAnchorTextAndDirectURL:
                return regex_spanClassWithAnchorTextAndDirectURL;
//            case anchorTextAndDirectURL:
//                return regex_anchorTextAndDirectURL;
            case bareURL_wrappedInTags:
                return regex_bareURL_wrappedInTags;
            case urlWrappedInHTMLsyntax:
                return regex_urlWrappedInHTMLsyntax;
//            case anchorTextWithReferenceToURL:
//                return regex_anchorTextWithReferenceToURL;
            case bareURL_notWrappedInTags:
                return regex_bareURL_notWrappedInTags;

            default:
                return null;
        }
    }


    AnchorTextAndUrlPair(String anchor, String reference, String url, AnchorRefUrlType anchorRefUrlType) {
        this.anchor = anchor;
        this.reference = reference;
        this.url = url;
        this.type = anchorRefUrlType;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "anchor text: " + anchor + "\n"
                + "reference: " + reference + "\n"
                + "URL: " + url + "\n"
                + "Type: " + type + "\n"
                + "\n";
    }
}
