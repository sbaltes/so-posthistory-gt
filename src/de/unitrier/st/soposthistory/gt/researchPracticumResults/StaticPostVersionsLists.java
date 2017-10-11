package de.unitrier.st.soposthistory.gt.researchPracticumResults;
/*
import de.unitrier.st.soposthistorygt.util.BlockLifeSpan;
import de.unitrier.st.soposthistory.blocks.CodeBlockVersion;
import de.unitrier.st.soposthistory.blocks.TextBlockVersion;
import de.unitrier.st.soposthistory.version.PostVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;
import de.unitrier.st.stringsimilarity.Normalization;

import java.util.List;
import java.util.Vector;


public class StaticPostVersionsLists {

    public enum PostVersionListEnum{
        p_140861,
        p_326440,
        p_1109108,
        p_2581754,
        p_3145655,
        p_3758880,
        p_5445161,
        p_5599842,
        p_9855338,
        p_26196831
    }

    PostVersionList getPostVersionListWithEnumID(PostVersionListEnum postVersionListID){
        switch (postVersionListID){
            case p_140861:
                return p_140861;
            case p_326440:
                return p_326440;
            case p_1109108:
                return p_1109108;
            case p_2581754:
                return p_2581754;
            case p_3145655:
                return p_3145655;
            case p_3758880:
                return p_3758880;
            case p_5445161:
                return p_5445161;
            case p_5599842:
                return p_5599842;
            case p_9855338:
                return p_9855338;
            case p_26196831:
                return p_26196831;
        }
        return null;
    }

    static int getPositionOfEnum(PostVersionListEnum postVersionListID){
        switch (postVersionListID){
            case p_140861:
                return 0;
            case p_326440:
                return 1;
            case p_1109108:
                return 2;
            case p_2581754:
                return 3;
            case p_3145655:
                return 4;
            case p_3758880:
                return 5;
            case p_5445161:
                return 6;
            case p_5599842:
                return 7;
            case p_9855338:
                return 8;
            case p_26196831:
                return 9;
        }
        return -1;
    }

    public PostVersionList p_140861;
    public PostVersionList p_326440;
    public PostVersionList p_1109108;
    public PostVersionList p_2581754;
    public PostVersionList p_3145655;
    public PostVersionList p_3758880;
    public PostVersionList p_5445161;
    public PostVersionList p_5599842;
    public PostVersionList p_9855338;
    public PostVersionList p_26196831;

    public void init() {
        p_140861 = new PostVersionList();
        p_140861.readFromCSV("testdata/", 140861, 2);
        p_326440 = new PostVersionList();
        p_326440.readFromCSV("testdata/", 326440, 2);
        p_1109108 = new PostVersionList();
        p_1109108.readFromCSV("testdata/", 1109108, 2);
        p_2581754 = new PostVersionList();
        p_2581754.readFromCSV("testdata/", 2581754, 2);
        p_3145655 = new PostVersionList();
        p_3145655.readFromCSV("testdata/", 3145655, 2);
        p_3758880 = new PostVersionList();
        p_3758880.readFromCSV("testdata/", 3758880, 2);
        p_5445161 = new PostVersionList();
        p_5445161.readFromCSV("testdata/", 5445161, 2);
        p_5599842 = new PostVersionList();
        p_5599842.readFromCSV("testdata/", 5599842, 2);
        p_9855338 = new PostVersionList();
        p_9855338.readFromCSV("testdata/", 9855338, 2);
        p_26196831 = new PostVersionList();
        p_26196831.readFromCSV("testdata/", 26196831, 2);
    }


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
}
*/