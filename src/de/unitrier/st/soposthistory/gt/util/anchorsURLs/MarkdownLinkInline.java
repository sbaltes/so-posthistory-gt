package de.unitrier.st.soposthistory.gt.util.anchorsURLs;

import java.util.regex.Pattern;

public class MarkdownLinkInline {
    // TODO: adapt for include second matching group for title/label

    // Source: https://stackoverflow.com/editing-help#code
    // Example 1: Here's an inline link to [Google](http://www.google.com/).
    // Example 2: [poorly-named link](http://www.google.com/ "Google").

    // Source: https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet#links
    // Example 3: [I'm an inline-style link](https://www.google.com)
    // Example 4: [I'm an inline-style link with title](https://www.google.com "Google's Homepage")

    public static final Pattern regex = Pattern.compile("\\[([^]]+)]\\(\\s*((?:http|ftp|https):\\/\\/(?:[\\w_-]+(?:(?:\\.[\\w_-]+)+))(?:[\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-]))?(?:\\s+\"([^\"]+)\")?\\s*\\)");
}
