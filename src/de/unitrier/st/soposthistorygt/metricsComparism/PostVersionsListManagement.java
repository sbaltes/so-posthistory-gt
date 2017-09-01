package de.unitrier.st.soposthistorygt.metricsComparism;

import de.unitrier.st.soposthistory.version.PostVersionList;
import de.unitrier.st.soposthistorygt.util.anchorsURLs.AnchorTextAndUrlHandler;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import static de.unitrier.st.soposthistorygt.GroundTruthApp.GroundTruthCreator.normalizeURLsInTextBlocksOfAllVersions;
import static de.unitrier.st.soposthistorygt.GroundTruthApp.GroundTruthCreator.removeEmptyTextAndCodeBlocks;


public class PostVersionsListManagement {

    private String pathToDirectory;

    private Pattern pattern_groundTruth = Pattern.compile("[0-9]+" + "\\.csv");
    List<PostVersionList> postVersionLists = new Vector<>();


    public PostVersionsListManagement(String pathToDirectoryOfPostHistories){
        this.pathToDirectory = pathToDirectoryOfPostHistories;

        File file = new File(pathToDirectoryOfPostHistories);
        File[] allPostHistoriesInFolder = file.listFiles((dir, name) -> name.matches(pattern_groundTruth.pattern())); // https://stackoverflow.com/questions/4852531/find-files-in-a-folder-using-java

        assert allPostHistoriesInFolder != null;
        for(File postHistory : allPostHistoriesInFolder){
            PostVersionList tmpPostVersionList = new PostVersionList();
            int postId = Integer.valueOf(postHistory.getName().substring(0, postHistory.getName().length()-4));
            tmpPostVersionList.readFromCSV(pathToDirectory + "\\", postId, 2);

            AnchorTextAndUrlHandler anchorTextAndUrlHandler = new AnchorTextAndUrlHandler();
            normalizeURLsInTextBlocksOfAllVersions(tmpPostVersionList, anchorTextAndUrlHandler);
            removeEmptyTextAndCodeBlocks(tmpPostVersionList);

            postVersionLists.add(
                    tmpPostVersionList
            );
        }

        postVersionLists.sort(Comparator.comparingInt(o -> o.getFirst().getPostId()));
    }

    public PostVersionList getPostVersionListWithID(int postID){
        for(PostVersionList postVersionList : postVersionLists){
            if(postID == postVersionList.getFirst().getPostId()){
                return postVersionList;
            }
        }

        return null;
    }


    int getPositionOfPostWithID(int postVersionListID){
        for(int i=0; i<postVersionLists.size(); i++){
            if(postVersionLists.get(i).getFirst().getPostId() == postVersionListID){
                return i;
            }
        }
        return -1;
    }


/*
    public String getStatisticsOfPostVersionList(){

        int numberOfPostVersionsOverall = 0;
        int numberOfTextBlocksOverall = 0;
        int lengthOfTextBlocksOverall = 0;
        int numberOfCodeBlocksOverall = 0;
        int lengthOfCodeBlocksOverall = 0;

        int numberOfVersionsMin = -1;
        int numberOfVersionsMax = 0;

        int numberOfTextBlocksMin = 0;
        int lengthOfTextBlocksMin = 0;
        int numberOfTextBlocksMax = 0;
        int lengthOfTextBlocksMax = 0;

        int numberOfCodeBlocksMin = -1;
        int lengthOfCodeBlocksMin = -1;
        int numberOfCodeBlocksMax = -1;
        int lengthOfCodeBlocksMax = -1;

        for(int i=0; i<PostVersionListEnum.values().length; i++) {

            PostVersionList tmpPostVersionList = getPostVersionListWithEnumID(PostVersionListEnum.values()[i]);

            int numberOfVersionsTmp = tmpPostVersionList.size();

            numberOfPostVersionsOverall += numberOfVersionsTmp;
            numberOfVersionsMax = (numberOfVersionsTmp > numberOfVersionsMax) ? numberOfVersionsTmp : numberOfVersionsMax;
            numberOfVersionsMin = (numberOfVersionsTmp < numberOfVersionsMin || (numberOfVersionsMin == -1)) ? numberOfVersionsTmp : numberOfVersionsMin;

            for (PostVersion postVersion : tmpPostVersionList) {

                // text
                List<TextBlockVersion> textBlocks = postVersion.getTextBlocks();

                numberOfTextBlocksOverall += textBlocks.size();
                numberOfTextBlocksMax = (textBlocks.size() > numberOfTextBlocksMax) ? textBlocks.size() : numberOfTextBlocksMax;
                numberOfTextBlocksMin = (textBlocks.size() < numberOfTextBlocksMin || (numberOfTextBlocksMin == -1)) ? textBlocks.size() : numberOfTextBlocksMin;

                for (TextBlockVersion textBlockVersion : textBlocks) {
                    int tmpTextBlockLength = Normalization.removeWhitespaces(textBlockVersion.getContent().replaceAll("", "")).length();    // TODO : which normalization to take?
                    lengthOfTextBlocksOverall += tmpTextBlockLength;
                    lengthOfTextBlocksMax = (tmpTextBlockLength > lengthOfTextBlocksMax) ? tmpTextBlockLength : lengthOfTextBlocksMax;
                    lengthOfTextBlocksMin = (tmpTextBlockLength < lengthOfTextBlocksMin || lengthOfTextBlocksMin == -1) ? tmpTextBlockLength : lengthOfTextBlocksMin;
                }


                // code
                List<CodeBlockVersion> codeBlocks = postVersion.getCodeBlocks();

                numberOfCodeBlocksOverall += codeBlocks.size();
                numberOfCodeBlocksMax = (codeBlocks.size() > numberOfCodeBlocksMax) ? codeBlocks.size() : numberOfCodeBlocksMax;
                numberOfCodeBlocksMin = (codeBlocks.size() < numberOfCodeBlocksMin || (numberOfCodeBlocksMin == -1)) ? codeBlocks.size() : numberOfCodeBlocksMin;

                for (CodeBlockVersion codeBlockVersion : codeBlocks) {
                    int tmpCodeBlockLength = Normalization.removeWhitespaces(codeBlockVersion.getContent().replaceAll("", "")).length();    // TODO : which normalization to take?
                    lengthOfCodeBlocksOverall += tmpCodeBlockLength;
                    lengthOfCodeBlocksMax = (tmpCodeBlockLength > lengthOfCodeBlocksMax) ? tmpCodeBlockLength : lengthOfCodeBlocksMax;
                    lengthOfCodeBlocksMin = (tmpCodeBlockLength < lengthOfCodeBlocksMin || lengthOfCodeBlocksMin == -1) ? tmpCodeBlockLength : lengthOfCodeBlocksMin;
                }
            }
        }


        Vector<Vector<BlockLifeSpan>> groundTruth_text = MetricsComparator.groundTruthBlocks_text;
        Vector<Vector<BlockLifeSpan>> groundTruth_code = MetricsComparator.groundTruthBlocks_code;

        int sumOfLifeSpans_text = 0;
        double averageLifeSpanSize_text = 0;
        for (Vector<BlockLifeSpan> blockLifeSpans : groundTruth_text) {
            sumOfLifeSpans_text += blockLifeSpans.size();
            for (BlockLifeSpan blockLifeSpan : blockLifeSpans) {
                averageLifeSpanSize_text += blockLifeSpan.size();
            }
        }
        averageLifeSpanSize_text /= sumOfLifeSpans_text;


        int sumOfLifeSpans_code = 0;
        double averageLifeSpanSize_code = 0;
        for (Vector<BlockLifeSpan> blockLifeSpans : groundTruth_code) {
            sumOfLifeSpans_code += blockLifeSpans.size();
            for (BlockLifeSpan blockLifeSpan : blockLifeSpans) {
                averageLifeSpanSize_code += blockLifeSpan.size();
            }
        }
        averageLifeSpanSize_code /= sumOfLifeSpans_code;


        return "number of post versions overall: " + numberOfPostVersionsOverall + "\n"
                + "max number of versions found: " + numberOfVersionsMax + "\n"
                + "min number of versions found: " + numberOfVersionsMin + "\n"
                + "average number of post versions: " + (double)numberOfPostVersionsOverall / PostVersionListEnum.values().length + "\n"
                + "\n"
                + "number of text blocks overall: " + numberOfTextBlocksOverall + "\n"
                + "max number of text blocks found: " + numberOfTextBlocksMax + "\n"
                + "min number of text blocks found: " + numberOfTextBlocksMin + "\n"
                + "average number of text blocks per post in one version: " + (double)numberOfTextBlocksOverall / numberOfPostVersionsOverall + "\n"    // TODO : is this right?
                + "\n"
                + "number of code blocks overall: " + numberOfCodeBlocksOverall + "\n"
                + "max number of code blocks found: " + numberOfCodeBlocksMax + "\n"
                + "min number of code blocks found: " + numberOfCodeBlocksMin + "\n"
                + "average number of code blocks per post in one version: " + (double)numberOfCodeBlocksOverall / numberOfPostVersionsOverall + "\n"    // TODO : is this right?
                + "\n"
                + "overall length of text blocks: " + lengthOfTextBlocksOverall + "\n"
                + "max length of a text block found: " + lengthOfTextBlocksMax + "\n"
                + "min length of a text block found: " + lengthOfTextBlocksMin + "\n"
                + "average length of a text block per post in one version: " + (double)lengthOfTextBlocksOverall / numberOfPostVersionsOverall + "\n"    // TODO : is this right?
                + "\n"
                + "overall length of code blocks: " + lengthOfCodeBlocksOverall + "\n"
                + "max length of a code block found: " + lengthOfCodeBlocksMax + "\n"
                + "min length of a code block found: " + lengthOfCodeBlocksMin + "\n"
                + "average length of a code block per post in one version: " + (double)lengthOfCodeBlocksOverall / numberOfPostVersionsOverall + "\n"    // TODO : is this right?

                + "\n\n"
                + "sum of text life spans (cohesive pairs): " + sumOfLifeSpans_text + "\n"
                + "average lifeSpan size text: " + averageLifeSpanSize_text + "\n\n"
                + "sum of code life spans (cohesive pairs): " + sumOfLifeSpans_code + "\n"
                + "average lifeSpan size code: " + averageLifeSpanSize_code + "\n\n"
                ;

    }
    */
}
