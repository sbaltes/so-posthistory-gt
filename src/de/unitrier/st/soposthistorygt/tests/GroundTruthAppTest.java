package de.unitrier.st.soposthistorygt.tests;

// TODO: Lorik: create new (or copy old) classes for URL matching and normalization

import de.unitrier.st.soposthistory.version.PostVersionList;
import de.unitrier.st.soposthistorygt.util.anchorsURLs.AnchorTextAndUrlHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import static de.unitrier.st.soposthistorygt.GroundTruthApp.GroundTruthCreator.normalizeURLsInTextBlocksOfAllVersions;
import static de.unitrier.st.soposthistorygt.metricsComparism.PostVersionsListManagement.pattern_groundTruth;

class GroundTruthAppTest {

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

    //AnchorTextAndUrlHandler anchorTextAndUrlHandler = new AnchorTextAndUrlHandler();

    /*
    @Test
    public void testGetURLsReferencedWithAnchors(){
        Vector<AnchorTextAndUrlPair> allAnchorsRefsAndURLpairs = anchorTextAndUrlHandler.parseAndExtractAnchorsReferencesURLsInSquareAndRoundBrackets(inputText3_allInOneLine);
        for (AnchorTextAndUrlPair anchorRefURLpair : allAnchorsRefsAndURLpairs) {
            System.out.println(anchorRefURLpair);
        }
    }

    @Test
    public void testDeleteAllAnchorsReferencesURLsOf(){
        Vector<AnchorTextAndUrlPair> allAnchorsRefsAndURLpairs = anchorTextAndUrlHandler.parseAndExtractAnchorsReferencesURLsInSquareAndRoundBrackets(inputText3_allInOneLine);
        System.out.println(inputText3_allInOneLine);
        String markdown = anchorTextAndUrlHandler.deleteAllAnchorsReferencesURLsOf(inputText3_allInOneLine, allAnchorsRefsAndURLpairs);
        System.out.println(markdown);
    }

    @Test
    public void testGetAllAnchorsRefsAndURLpairs(){ // Test passed
        Vector<AnchorTextAndUrlPair> allAnchorsRefsAndURLpairs_allInOneLine = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(inputText3_allInOneLine);
        Vector<AnchorTextAndUrlPair> allAnchorsRefsAndURLpairs_separatLines = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(inputText2_separatLines);

        assertEquals(allAnchorsRefsAndURLpairs_allInOneLine, allAnchorsRefsAndURLpairs_separatLines);
    }

    @Test
    public void testAnchorTextNormalizer(){ // test passes
        Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(inputText2_separatLines);

        String normalizedInput2 = anchorTextAndUrlHandler.normalizeAnchorsRefsAndURLs(inputText2_separatLines, anchorTextAndUrlPairs);
        String notNormalizedInput3 = anchorTextAndUrlHandler.normalizeAnchorsRefsAndURLs(inputText3_allInOneLine, anchorTextAndUrlPairs);

        assertEquals(notNormalizedInput3, normalizedInput2);

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

/*
    @Test
    public void testMarkupNormalizedAnchorsAndUrlsWithHTML(){
        Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(inputText2_separatLines);
        String normalizedInput2 = anchorTextAndUrlHandler.normalizeAnchorsRefsAndURLs(inputText2_separatLines, anchorTextAndUrlPairs);
        //String markedUpNormalizedInput2 = anchorTextAndUrlHandler.markupNormalizedAnchorsAndUrlsWithHTML(normalizedInput2, anchorTextAndUrlPairs);

        System.out.println("input");
        System.out.println(inputText2_separatLines);
        System.out.println("normalized input: ");
        System.out.println(normalizedInput2);
        System.out.println(" ---------------------------------------------------------- ");
        System.out.println("marked up normalized input: ");
        //System.out.println(markedUpNormalizedInput2);
    }

    @Test
    public void showReasonOfNormalizationBeforeExtractingURLs(){
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();

        for(int i=0; i<groundTruth.p_9855338.getFirst().getTextBlocks().size(); i++){
            System.out.println(groundTruth.p_9855338.getFirst().getTextBlocks().get(i).getContent());
        }
    }
}
*/

//    @Test
//    void testMarkupNormalizedAnchorsAndUrlsForDatabases(){
//
//        Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(inputText2_separatLines);
//        String normalizedInput2 = anchorTextAndUrlHandler.normalizeAnchorsRefsAndURLsForDatabase(inputText2_separatLines, anchorTextAndUrlPairs);
//
//        System.out.println(inputText2_separatLines);
//        System.out.println(" - ------------------------------------------------------- - ");
//        System.out.println(normalizedInput2);
//    }
//
//    @Test
//    void testMarkupNormalizedAnchorsAndUrlsWithHTML_referenceWithTextBehindUrl(){
//
//        String input = "Consider using a [ManualResetEvent][1] to block the main thread at the end of its processing, and call Reset() on it once the timer's processing has finished.  If this is something that needs to run constantly, consider moving this into a service process instead of a console app.\n" +
//                "\n" +
//                "\n" +
//                "  [1]: http://msdn.microsoft.com/en-us/library/system.threading.manualresetevent.aspx \"\"MSDN Reference\"\"";
//
//        Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(input);
//        String normalizedInput2 = anchorTextAndUrlHandler.normalizeAnchorsRefsAndURLsForDatabase(input, anchorTextAndUrlPairs);
//
//        System.out.println(normalizedInput2);
//    }
//
//    @Test
//    void testMarkupNormalizedAnchorsAndUrlsWithHTML_referenceWithTextBehindUrl2(){
//
//        String input = "Consider using a [ManualResetEvent][1] to block the main thread at the end of its processing, and call Reset() on it once the timer's processing has finished.  If this is something that needs to run constantly, consider moving this into a service process instead of a console app.\n" +
//                "\n" +
//                "You can use something like Console.WriteLine() to block the main thread, so other background threads (like timer threads) will still work. You may also use an [AutoResetEvent][2] to block the execution, then (when you need to) you can call Set() method on that AutoResetEvent object to release the main thread. Also ensure that your reference to Timer object doesn't go out of scope and garbage collected.\n" +
//                "\n" +
//                "  [1]: http://msdn.microsoft.com/en-us/library/system.threading.manualresetevent.aspx \"\"MSDN Reference\"\"\n" +
//                "  [2]: http://msdn.microsoft.com/en-us/library/system.threading.autoresetevent.aspx\n";
//
//        Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs = anchorTextAndUrlHandler.extractAllAnchorsRefsAndURLpairs(input);
//        String normalizedInput2 = anchorTextAndUrlHandler.normalizeAnchorsRefsAndURLsForDatabase(input, anchorTextAndUrlPairs);
//
//        System.out.println(" ############################################ ");
//        System.out.println("before:\n");
//        System.out.println(input);
//        System.out.println(" -------------------------------------------- ");
//        System.out.println("after:\n");
//        System.out.println(normalizedInput2);
//        System.out.println(" ############################################ ");
//    }


    @Test
    public void testSetIfParsable() throws IOException {

        Vector<String> pathToAllDirectories = new Vector<>();
        pathToAllDirectories.add(System.getProperty("user.dir") + "\\data\\PostId_VersionCount_SO_17-06_sample_100_1_files\\PostId_VersionCount_SO_17-06_sample_100_1_files\\PostId_VersionCount_SO_17-06_sample_100_1_files");
        pathToAllDirectories.add(System.getProperty("user.dir") + "\\data\\PostId_VersionCount_SO_17-06_sample_100_1_files\\PostId_VersionCount_SO_17-06_sample_100_1_files\\PostId_VersionCount_SO_17-06_sample_100_1_files");
        pathToAllDirectories.add(System.getProperty("user.dir") + "\\data\\PostId_VersionCount_SO_Java_17-06_sample_100_1_files\\PostId_VersionCount_SO_Java_17-06_sample_100_1");
        pathToAllDirectories.add(System.getProperty("user.dir") + "\\data\\PostId_VersionCount_SO_Java_17-06_sample_100_2_files\\PostId_VersionCount_SO_Java_17-06_sample_100_2");


        for (String path : pathToAllDirectories) {
            File file = new File(path);
            File[] allPostHistoriesInFolder = file.listFiles((dir, name) -> name.matches(pattern_groundTruth.pattern())); // https://stackoverflow.com/questions/4852531/find-files-in-a-folder-using-java

            assert allPostHistoriesInFolder != null;
            for (File postHistory : allPostHistoriesInFolder) {
                try {
                    PostVersionList tmpPostVersionList = new PostVersionList();
                    int postId = Integer.valueOf(postHistory.getName().substring(0, postHistory.getName().length() - 4));
                    tmpPostVersionList.readFromCSV(path + "\\", postId, 2);

                    AnchorTextAndUrlHandler anchorTextAndUrlHandler = new AnchorTextAndUrlHandler();
                    normalizeURLsInTextBlocksOfAllVersions(tmpPostVersionList, anchorTextAndUrlHandler);
                } catch (Exception e) {

                    System.out.println("Failed to parse " + postHistory.getPath());
                }
            }
        }
    }
}


