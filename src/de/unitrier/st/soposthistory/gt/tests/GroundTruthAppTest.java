package de.unitrier.st.soposthistory.gt.tests;

// TODO: Lorik: revise test cases

import de.unitrier.st.soposthistory.gt.util.anchorsURLs.AnchorTextAndUrlHandler;
import de.unitrier.st.soposthistory.gt.util.anchorsURLs.AnchorTextAndUrlPair;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class GroundTruthAppTest {

    // TODO: these test cases still fail...

    private String inputText1 = "You can force Android to hide the virtual keyboard using the [InputMethodManager][1], calling [`hideSoftInputFromWindow`][2], passing in the token of the window containing your focused view.\n" +
            "\n" +
            "    // Check if no view has focus:\n" +
            "    View view = this.getCurrentFocus();\n" +
            "    if (view != null) {  \n" +
            "        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);\n" +
            "        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);\n" +
            "    }\n" +
            "\n" +
            "This will force the keyboard to be hidden in all situations. In some cases you will want to pass in `InputMethodManager.HIDE_IMPLICIT_ONLY` as the second parameter to ensure you only hide the keyboard when the user didn't explicitly force it to appear (by holding down menu).\n" +
            "  [1]: http://developer.android.com/reference/android/view/inputmethod/InputMethodManager.html" + "  [2]: http://developer.android.com/reference/android/view/inputmethod/InputMethodManager.html#hideSoftInputFromWindow%28android.os.IBinder,%20int%29";

    private String inputText2_separatLines = "Here's an inline link to [Google](http://www.google.com/). \n" +
            "Here's a reference-style link to [DuckDuckGo][1].\n" +
            "Here's a very readable link to [Yahoo!][yahoo].\n" +
            "[1]: http://www.duckduckgo.com/\n" +
            "[yahoo]: http://www.yahoo.com/ Here's a <span class=\"hi\">\n" +
            "Here's a <span class=\"hi\">[poorly-named link](http://www.bing.com/ \"Bing\")</span>.\n" +
            "Here's another <span class=\"hi\">[another poorly-named link](http://www.startpage.com/ \"Startpage\")</span>.\n" +
            "Never write \"[click here][^2]\". Visit [us][web].\n" +
            "[^2]: http://www.w3.org/QA/Tips/noClickHere\n" +
            "[web]: https://stackoverflow.com/ \"Stack Overflow\"\n" +
            "<a href=\"http://example.com\" title=\"example\">example</a>\n" +
            "Have you ever seen <http://uni-trier.de>?";

    private String inputText3_allInOneLine = "Here's an inline link to [Google](http://www.google.com/). " +
            "Here's a reference-style link to [DuckDuckGo][1]. " +
            "Here's a very readable link to [Yahoo!][yahoo]. " +
            "[1]: http://www.duckduckgo.com/ " +
            "[yahoo]: http://www.yahoo.com/ " +
            "Here's a <span class=\"hi\">[poorly-named link](http://www.bing.com/ \"Bing\")</span>. " +
            "Here's another <span class=\"hi\">[another poorly-named link](http://www.startpage.com/ \"Startpage\")</span>. " +
            "Never write \"[click here][^2]\"." +
            " Visit [us][web]. " +
            "[^2]: http://www.w3.org/QA/Tips/noClickHere " +
            "[web]: https://stackoverflow.com/ \"Stack Overflow\" "+
            "<a href=\"http://example.com\" title=\"example\">example</a> + Have you ever seen <http://uni-trier.de>?";

    AnchorTextAndUrlHandler anchorTextAndUrlHandler = new AnchorTextAndUrlHandler();

    @Test
    public void testGetAllAnchorsRefsAndURLpairs(){ // Test passed
        LinkedList<AnchorTextAndUrlPair> allAnchorsRefsAndURLpairs_allInOneLine = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(inputText3_allInOneLine);
        LinkedList<AnchorTextAndUrlPair> allAnchorsRefsAndURLpairs_separatLines = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(inputText2_separatLines);

        assertThat(allAnchorsRefsAndURLpairs_allInOneLine, is(allAnchorsRefsAndURLpairs_separatLines));
    }

    @Test
    public void testAnchorTextNormalizer() { // test passes
        LinkedList<AnchorTextAndUrlPair> anchorTextAndUrlPairs = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(inputText2_separatLines);

        String normalizedInput2 = anchorTextAndUrlHandler.normalizeAnchorsRefsAndURLsForApp(inputText2_separatLines, anchorTextAndUrlPairs);
        String notNormalizedInput3 = anchorTextAndUrlHandler.normalizeAnchorsRefsAndURLsForApp(inputText3_allInOneLine, anchorTextAndUrlPairs);

        assertThat(notNormalizedInput3, is(normalizedInput2));

        /*
        Failes here:
        Never write "[click here][^2]".
        Visit [us][web].
        [^2]: http://www.w3.org/QA/Tips/noClickHere
        [web]: https://stackoverflow.com/ "Stack Overflow"

        with this output because it can't handle regex [^2]

        Here's a [Bing](http://www.bing.com/ ).
        Never write "[click here][^2]".
        Visit [us]( https://stackoverflow.com/ "Stack Overflow").
        [^2]: http://www.w3.org/QA/Tips/noClickHere


        Handling this causes more overhead. TODO : should we handle this?
        */
    }
}


