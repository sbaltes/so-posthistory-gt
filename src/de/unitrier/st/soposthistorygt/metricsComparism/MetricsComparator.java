package de.unitrier.st.soposthistorygt.metricsComparism;

import de.unitrier.st.soposthistory.blocks.CodeBlockVersion;
import de.unitrier.st.soposthistory.blocks.TextBlockVersion;
import de.unitrier.st.soposthistorygt.util.BlockLifeSpan;
import de.unitrier.st.stringsimilarity.profile.Base;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.BiFunction;

import static de.unitrier.st.soposthistorygt.metricsComparism.GroundTruthExtractionOfCSVs.extractListOfListsOfBlockLifeSpansOfAllExportedCSVs;
import static de.unitrier.st.soposthistorygt.metricsComparism.GroundTruthExtractionOfCSVs.filterListOfListOfBlockLifeSpansByType;
import static de.unitrier.st.stringsimilarity.edit.Base.*;
import static de.unitrier.st.stringsimilarity.edit.Variants.*;
import static de.unitrier.st.stringsimilarity.fingerprint.Variants.*;
import static de.unitrier.st.stringsimilarity.profile.Variants.*;
import static de.unitrier.st.stringsimilarity.set.Variants.*;

public class MetricsComparator{

    String pathToDirectoryOfAllPostHistories;
    String pathToDirectoryOfAllCompletedCSVs;

    public static PostVersionsListManagement postVersionsListManagement;
    public static List<List<BlockLifeSpan>> groundTruth;
    public static List<List<BlockLifeSpan>> groundTruthBlocks_text;
    public static List<List<BlockLifeSpan>> groundTruthBlocks_code;


    // to make automated process ease
    public enum MetricEnum{
        levenshteinStandard,
        levenshteinNormalized,

        damerauLevenshteinStandard,
        damerauLevenshteinNormalized,

        optimalAlignmentStandard,
        optimalAlignmentNormalized,

        optimalAlignment2GramFingerprint,
        optimalAlignment3GramFingerprint,
        optimalAlignment4GramFingerprint,
        optimalAlignment5GramFingerprint,

        optimalAlignmentShingle2Fingerprint,
        optimalAlignmentShingle3Fingerprint,

        optimalAlignment2GramFingerprintNormalized,
        optimalAlignment3GramFingerprintNormalized,
        optimalAlignment4GramFingerprintNormalized,
        optimalAlignment5GramFingerprintNormalized,

        optimalAlignmentShingle2FingerprintNormalized,
        optimalAlignmentShingle3FingerprintNormalized,

        longestCommonSubsequenceStandard,
        longestCommonSubsequenceNormalized,

        longestCommonSubsequence2GramFingerprint,
        longestCommonSubsequence3GramFingerprint,
        longestCommonSubsequence4GramFingerprint,
        longestCommonSubsequence5GramFingerprint,

        longestCommonSubsequenceShingle2Fingerprint,
        longestCommonSubsequenceShingle3Fingerprint,

        longestCommonSubsequence2GramFingerprintNormalized,
        longestCommonSubsequence3GramFingerprintNormalized,
        longestCommonSubsequence4GramFingerprintNormalized,
        longestCommonSubsequence5GramFingerprintNormalized,

        longestCommonSubsequenceShingle2FingerprintNormalized,
        longestCommonSubsequenceShingle3FingerprintNormalized,

        winnowingTokenJaccard,

        winnowing2GramJaccard,
        winnowing3GramJaccard,
        winnowing4GramJaccard,
        winnowing5GramJaccard,

        winnowingShingle2Jaccard,
        winnowingShingle3Jaccard,

        winnowingNormalizedTokenJaccard,

        winnowingNormalized2GramJaccard,
        winnowingNormalized3GramJaccard,
        winnowingNormalized4GramJaccard,
        winnowingNormalized5GramJaccard,

        winnowingNormalizedShingle2Jaccard,
        winnowingNormalizedShingle3Jaccard,

        winnowingTokenDice,

        winnowing2GramDice,
        winnowing3GramDice,
        winnowing4GramDice,
        winnowing5GramDice,

        winnowingShingle2Dice,
        winnowingShingle3Dice,

        winnowingNormalizedTokenDice,

        winnowingNormalized2GramDice,
        winnowingNormalized3GramDice,
        winnowingNormalized4GramDice,
        winnowingNormalized5GramDice,

        winnowingNormalizedShingle2Dice,
        winnowingNormalizedShingle3Dice,

        winnowingTokenDiceVariant,

        winnowing2GramDiceVariant,
        winnowing3GramDiceVariant,
        winnowing4GramDiceVariant,
        winnowing5GramDiceVariant,

        winnowingShingle2DiceVariant,
        winnowingShingle3DiceVariant,

        winnowingNormalizedTokenDiceVariant,

        winnowingNormalized2GramDiceVariant,
        winnowingNormalized3GramDiceVariant,
        winnowingNormalized4GramDiceVariant,
        winnowingNormalized5GramDiceVariant,

        winnowingNormalizedShingle2DiceVariant,
        winnowingNormalizedShingle3DiceVariant,

        winnowingTokenOverlap,

        winnowing2GramOverlap,
        winnowing3GramOverlap,
        winnowing4GramOverlap,
        winnowing5GramOverlap,

        winnowingShingle2Overlap,
        winnowingShingle3Overlap,

        winnowingNormalizedTokenOverlap,

        winnowingNormalized2GramOverlap,
        winnowingNormalized3GramOverlap,
        winnowingNormalized4GramOverlap,
        winnowingNormalized5GramOverlap,

        winnowingNormalizedShingle2Overlap,
        winnowingNormalizedShingle3Overlap,

/*
        rkrGstTokenJaccardMinMatchLength1,

        rkrGst2GramJaccardMinMatchLength1,
        rkrGst3GramJaccardMinMatchLength1,
        rkrGst4GramJaccardMinMatchLength1,
        rkrGst5GramJaccardMinMatchLength1,

        rkrGstShingle2JaccardMinMatchLength1,
        rkrGstShingle3JaccardMinMatchLength1,

        rkrGstNormalizedTokenJaccardMinMatchLength1,

        rkrGstNormalized2GramJaccardMinMatchLength1,
        rkrGstNormalized3GramJaccardMinMatchLength1,
        rkrGstNormalized4GramJaccardMinMatchLength1,
        rkrGstNormalized5GramJaccardMinMatchLength1,

        rkrGstNormalizedShingle2JaccardMinMatchLength1,
        rkrGstNormalizedShingle3JaccardMinMatchLength1,

        rkrGstTokenDiceMinMatchLength1,

        rkrGst2GramDiceMinMatchLength1,
        rkrGst3GramDiceMinMatchLength1,
        rkrGst4GramDiceMinMatchLength1,
        rkrGst5GramDiceMinMatchLength1,

        rkrGstShingle2DiceMinMatchLength1,
        rkrGstShingle3DiceMinMatchLength1,

        rkrGstNormalizedTokenDiceMinMatchLength1,

        rkrGstNormalized2GramDiceMinMatchLength1,
        rkrGstNormalized3GramDiceMinMatchLength1,
        rkrGstNormalized4GramDiceMinMatchLength1,
        rkrGstNormalized5GramDiceMinMatchLength1,

        rkrGstNormalizedShingle2DiceMinMatchLength1,
        rkrGstNormalizedShingle3DiceMinMatchLength1,

        rkrGstTokenDiceVariantMinMatchLength1,

        rkrGst2GramDiceVariantMinMatchLength1,
        rkrGst3GramDiceVariantMinMatchLength1,
        rkrGst4GramDiceVariantMinMatchLength1,
        rkrGst5GramDiceVariantMinMatchLength1,

        rkrGstShingle2DiceVariantMinMatchLength1,
        rkrGstShingle3DiceVariantMinMatchLength1,

        rkrGstNormalizedTokenDiceVariantMinMatchLength1,

        rkrGstNormalized2GramDiceVariantMinMatchLength1,
        rkrGstNormalized3GramDiceVariantMinMatchLength1,
        rkrGstNormalized4GramDiceVariantMinMatchLength1,
        rkrGstNormalized5GramDiceVariantMinMatchLength1,

        rkrGstNormalizedShingle2DiceVariantMinMatchLength1,
        rkrGstNormalizedShingle3DiceVariantMinMatchLength1,

        rkrGstTokenOverlapMinMatchLength1,

        rkrGst2GramOverlapMinMatchLength1,
        rkrGst3GramOverlapMinMatchLength1,
        rkrGst4GramOverlapMinMatchLength1,
        rkrGst5GramOverlapMinMatchLength1,

        rkrGstShingle2OverlapMinMatchLength1,
        rkrGstShingle3OverlapMinMatchLength1,

        rkrGstNormalizedTokenOverlapMinMatchLength1,

        rkrGstNormalized2GramOverlapMinMatchLength1,
        rkrGstNormalized3GramOverlapMinMatchLength1,
        rkrGstNormalized4GramOverlapMinMatchLength1,
        rkrGstNormalized5GramOverlapMinMatchLength1,

        rkrGstNormalizedShingle2OverlapMinMatchLength1,
        rkrGstNormalizedShingle3OverlapMinMatchLength1,

        rkrGstTokenJaccardMinMatchLength2,

        rkrGst2GramJaccardMinMatchLength2,
        rkrGst3GramJaccardMinMatchLength2,
        rkrGst4GramJaccardMinMatchLength2,
        rkrGst5GramJaccardMinMatchLength2,

        rkrGstShingle2JaccardMinMatchLength2,
        rkrGstShingle3JaccardMinMatchLength2,

        rkrGstNormalizedTokenJaccardMinMatchLength2,

        rkrGstNormalized2GramJaccardMinMatchLength2,
        rkrGstNormalized3GramJaccardMinMatchLength2,
        rkrGstNormalized4GramJaccardMinMatchLength2,
        rkrGstNormalized5GramJaccardMinMatchLength2,

        rkrGstNormalizedShingle2JaccardMinMatchLength2,
        rkrGstNormalizedShingle3JaccardMinMatchLength2,

        rkrGstTokenDiceMinMatchLength2,

        rkrGst2GramDiceMinMatchLength2,
        rkrGst3GramDiceMinMatchLength2,
        rkrGst4GramDiceMinMatchLength2,
        rkrGst5GramDiceMinMatchLength2,

        rkrGstShingle2DiceMinMatchLength2,
        rkrGstShingle3DiceMinMatchLength2,

        rkrGstNormalizedTokenDiceMinMatchLength2,

        rkrGstNormalized2GramDiceMinMatchLength2,
        rkrGstNormalized3GramDiceMinMatchLength2,
        rkrGstNormalized4GramDiceMinMatchLength2,
        rkrGstNormalized5GramDiceMinMatchLength2,

        rkrGstNormalizedShingle2DiceMinMatchLength2,
        rkrGstNormalizedShingle3DiceMinMatchLength2,

        rkrGstTokenDiceVariantMinMatchLength2,

        rkrGst2GramDiceVariantMinMatchLength2,
        rkrGst3GramDiceVariantMinMatchLength2,
        rkrGst4GramDiceVariantMinMatchLength2,
        rkrGst5GramDiceVariantMinMatchLength2,

        rkrGstShingle2DiceVariantMinMatchLength2,
        rkrGstShingle3DiceVariantMinMatchLength2,

        rkrGstNormalizedTokenDiceVariantMinMatchLength2,

        rkrGstNormalized2GramDiceVariantMinMatchLength2,
        rkrGstNormalized3GramDiceVariantMinMatchLength2,
        rkrGstNormalized4GramDiceVariantMinMatchLength2,
        rkrGstNormalized5GramDiceVariantMinMatchLength2,

        rkrGstNormalizedShingle2DiceVariantMinMatchLength2,
        rkrGstNormalizedShingle3DiceVariantMinMatchLength2,

        rkrGstTokenOverlapMinMatchLength2,

        rkrGst2GramOverlapMinMatchLength2,
        rkrGst3GramOverlapMinMatchLength2,
        rkrGst4GramOverlapMinMatchLength2,
        rkrGst5GramOverlapMinMatchLength2,

        rkrGstShingle2OverlapMinMatchLength2,
        rkrGstShingle3OverlapMinMatchLength2,

        rkrGstNormalizedTokenOverlapMinMatchLength2,

        rkrGstNormalized2GramOverlapMinMatchLength2,
        rkrGstNormalized3GramOverlapMinMatchLength2,
        rkrGstNormalized4GramOverlapMinMatchLength2,
        rkrGstNormalized5GramOverlapMinMatchLength2,

        rkrGstNormalizedShingle2OverlapMinMatchLength2,
        rkrGstNormalizedShingle3OverlapMinMatchLength2,

        rkrGstTokenJaccardMinMatchLength3,

        rkrGst2GramJaccardMinMatchLength3,
        rkrGst3GramJaccardMinMatchLength3,
        rkrGst4GramJaccardMinMatchLength3,
        rkrGst5GramJaccardMinMatchLength3,

        rkrGstShingle2JaccardMinMatchLength3,
        rkrGstShingle3JaccardMinMatchLength3,

        rkrGstNormalizedTokenJaccardMinMatchLength3,

        rkrGstNormalized2GramJaccardMinMatchLength3,
        rkrGstNormalized3GramJaccardMinMatchLength3,
        rkrGstNormalized4GramJaccardMinMatchLength3,
        rkrGstNormalized5GramJaccardMinMatchLength3,

        rkrGstNormalizedShingle2JaccardMinMatchLength3,
        rkrGstNormalizedShingle3JaccardMinMatchLength3,

        rkrGstTokenDiceMinMatchLength3,

        rkrGst2GramDiceMinMatchLength3,
        rkrGst3GramDiceMinMatchLength3,
        rkrGst4GramDiceMinMatchLength3,
        rkrGst5GramDiceMinMatchLength3,

        rkrGstShingle2DiceMinMatchLength3,
        rkrGstShingle3DiceMinMatchLength3,

        rkrGstNormalizedTokenDiceMinMatchLength3,

        rkrGstNormalized2GramDiceMinMatchLength3,
        rkrGstNormalized3GramDiceMinMatchLength3,
        rkrGstNormalized4GramDiceMinMatchLength3,
        rkrGstNormalized5GramDiceMinMatchLength3,

        rkrGstNormalizedShingle2DiceMinMatchLength3,
        rkrGstNormalizedShingle3DiceMinMatchLength3,

        rkrGstTokenDiceVariantMinMatchLength3,

        rkrGst2GramDiceVariantMinMatchLength3,
        rkrGst3GramDiceVariantMinMatchLength3,
        rkrGst4GramDiceVariantMinMatchLength3,
        rkrGst5GramDiceVariantMinMatchLength3,

        rkrGstShingle2DiceVariantMinMatchLength3,
        rkrGstShingle3DiceVariantMinMatchLength3,

        rkrGstNormalizedTokenDiceVariantMinMatchLength3,

        rkrGstNormalized2GramDiceVariantMinMatchLength3,
        rkrGstNormalized3GramDiceVariantMinMatchLength3,
        rkrGstNormalized4GramDiceVariantMinMatchLength3,
        rkrGstNormalized5GramDiceVariantMinMatchLength3,

        rkrGstNormalizedShingle2DiceVariantMinMatchLength3,
        rkrGstNormalizedShingle3DiceVariantMinMatchLength3,

        rkrGstTokenOverlapMinMatchLength3,

        rkrGst2GramOverlapMinMatchLength3,
        rkrGst3GramOverlapMinMatchLength3,
        rkrGst4GramOverlapMinMatchLength3,
        rkrGst5GramOverlapMinMatchLength3,

        rkrGstShingle2OverlapMinMatchLength3,
        rkrGstShingle3OverlapMinMatchLength3,
        rkrGstNormalizedTokenOverlapMinMatchLength3,

        rkrGstNormalized2GramOverlapMinMatchLength3,
        rkrGstNormalized3GramOverlapMinMatchLength3,
        rkrGstNormalized4GramOverlapMinMatchLength3,
        rkrGstNormalized5GramOverlapMinMatchLength3,

        rkrGstNormalizedShingle2OverlapMinMatchLength3,
        rkrGstNormalizedShingle3OverlapMinMatchLength3,
*/
        cosineNormalizedTokensBool,
        cosineNormalizedTokensTermFrequency,
        cosineNormalizedTokensNormalizedTermFrequency,

        cosineNormalized2GramsBool,
        cosineNormalized3GramsBool,
        cosineNormalized4GramsBool,
        cosineNormalized5GramsBool,

        cosineNormalized2GramsTermFrequency,
        cosineNormalized3GramsTermFrequency,
        cosineNormalized4GramsTermFrequency,
        cosineNormalized5GramsTermFrequency,

        cosineNormalized2GramsNormalizedTermFrequency,
        cosineNormalized3GramsNormalizedTermFrequency,
        cosineNormalized4GramsNormalizedTermFrequency,
        cosineNormalized5GramsNormalizedTermFrequency,

        cosineNormalizedShingle2Bool,
        cosineNormalizedShingle3Bool,
        cosineNormalizedShingle2TermFrequency,
        cosineNormalizedShingle3TermFrequency,
        cosineNormalizedShingle2NormalizedTermFrequency,
        cosineNormalizedShingle3NormalizedTermFrequency,

        manhattanNormalizedTokens,

        manhattanNormalized2Grams,
        manhattanNormalized3Grams,
        manhattanNormalized4Grams,
        manhattanNormalized5Grams,

        manhattanNormalizedShingles2,
        manhattanNormalizedShingles3,

        jaccardTokens,
        jaccardNormalizedTokens,

        jaccard2Grams,
        jaccard3Grams,
        jaccard4Grams,
        jaccard5Grams,

        jaccardNormalized2Grams,
        jaccardNormalized3Grams,
        jaccardNormalized4Grams,
        jaccardNormalized5Grams,

        jaccardNormalizedPadding2grams,
        jaccardNormalizedPadding3grams,
        jaccardNormalizedPadding4grams,
        jaccardNormalizedPadding5grams,

        jaccardShingles2,
        jaccardShingles3,
        jaccardNormalizedShingles2,
        jaccardNormalizedShingles3,

        diceTokens,
        diceNormalizedTokens,

        dice2Grams,
        dice3Grams,
        dice4Grams,
        dice5Grams,

        diceNormalized2Grams,
        diceNormalized3Grams,
        diceNormalized4Grams,
        diceNormalized5Grams,

        diceNormalizedPadding2grams,
        diceNormalizedPadding3grams,
        diceNormalizedPadding4grams,
        diceNormalizedPadding5grams,

        diceShingles2,
        diceShingles3,
        diceNormalizedShingles2,
        diceNormalizedShingles3,

        diceVariantTokens,
        diceVariantNormalizedTokens,

        diceVariant2Grams,
        diceVariant3Grams,
        diceVariant4Grams,
        diceVariant5Grams,

        diceVariantNormalized2Grams,
        diceVariantNormalized3Grams,
        diceVariantNormalized4Grams,
        diceVariantNormalized5Grams,

        diceVariantNormalizedPadding2grams,
        diceVariantNormalizedPadding3grams,
        diceVariantNormalizedPadding4grams,
        diceVariantNormalizedPadding5grams,

        diceVariantShingles2,
        diceVariantShingles3,
        diceVariantNormalizedShingles2,
        diceVariantNormalizedShingles3,

        overlapTokens,
        overlapNormalizedTokens,

        overlap2Grams,
        overlap3Grams,
        overlap4Grams,
        overlap5Grams,

        overlapNormalized2Grams,
        overlapNormalized3Grams,
        overlapNormalized4Grams,
        overlapNormalized5Grams,

        overlapNormalizedPadding2grams,
        overlapNormalizedPadding3grams,
        overlapNormalizedPadding4grams,
        overlapNormalizedPadding5grams,

        overlapShingles2,
        overlapShingles3,
        overlapNormalizedShingles2,
        overlapNormalizedShingles3

        }

    public BiFunction<String, String, Double> getBiFunctionMetric(MetricEnum metric){
        switch (metric){

            case levenshteinStandard:
                return levenshteinStandard;
            case levenshteinNormalized:
                return levenshteinNormalized;

            case damerauLevenshteinStandard:
                return damerauLevenshteinStandard;
            case damerauLevenshteinNormalized:
                return damerauLevenshteinNormalized;

            case optimalAlignmentStandard:
                return optimalAlignmentStandard;
            case optimalAlignmentNormalized:
                return optimalAlignmentNormalized;
            case optimalAlignment2GramFingerprint:
                return optimalAlignment2GramFingerprint;
            case optimalAlignment3GramFingerprint:
                return optimalAlignment3GramFingerprint;
            case optimalAlignment4GramFingerprint:
                return optimalAlignment4GramFingerprint;
            case optimalAlignment5GramFingerprint:
                return optimalAlignment5GramFingerprint;
            case optimalAlignmentShingle2Fingerprint:
                return optimalAlignmentShingle2Fingerprint;
            case optimalAlignmentShingle3Fingerprint:
                return optimalAlignmentShingle3Fingerprint;
            case optimalAlignment2GramFingerprintNormalized:
                return optimalAlignment2GramFingerprintNormalized;
            case optimalAlignment3GramFingerprintNormalized:
                return optimalAlignment3GramFingerprintNormalized;
            case optimalAlignment4GramFingerprintNormalized:
                return optimalAlignment4GramFingerprintNormalized;
            case optimalAlignment5GramFingerprintNormalized:
                return optimalAlignment5GramFingerprintNormalized;
            case optimalAlignmentShingle2FingerprintNormalized:
                return optimalAlignmentShingle2FingerprintNormalized;
            case optimalAlignmentShingle3FingerprintNormalized:
                return optimalAlignmentShingle3FingerprintNormalized;

            case longestCommonSubsequenceStandard:
                return longestCommonSubsequenceStandard;

            case longestCommonSubsequenceNormalized :
                return longestCommonSubsequenceNormalized;
            case longestCommonSubsequence2GramFingerprint :
                return longestCommonSubsequence2GramFingerprint;
            case longestCommonSubsequence3GramFingerprint :
                return longestCommonSubsequence3GramFingerprint;
            case longestCommonSubsequence4GramFingerprint :
                return longestCommonSubsequence4GramFingerprint;
            case longestCommonSubsequence5GramFingerprint :
                return longestCommonSubsequence5GramFingerprint;
            case longestCommonSubsequenceShingle2Fingerprint :
                return longestCommonSubsequenceShingle2Fingerprint;
            case longestCommonSubsequenceShingle3Fingerprint :
                return longestCommonSubsequenceShingle3Fingerprint;
            case longestCommonSubsequence2GramFingerprintNormalized :
                return longestCommonSubsequence2GramFingerprintNormalized;
            case longestCommonSubsequence3GramFingerprintNormalized :
                return longestCommonSubsequence3GramFingerprintNormalized;
            case longestCommonSubsequence4GramFingerprintNormalized :
                return longestCommonSubsequence4GramFingerprintNormalized;
            case longestCommonSubsequence5GramFingerprintNormalized :
                return longestCommonSubsequence5GramFingerprintNormalized;
            case longestCommonSubsequenceShingle2FingerprintNormalized :
                return longestCommonSubsequenceShingle2FingerprintNormalized;
            case longestCommonSubsequenceShingle3FingerprintNormalized :
                return longestCommonSubsequenceShingle3FingerprintNormalized;

            case winnowingTokenJaccard : return winnowingTokenJaccard;
            case winnowing2GramJaccard : return winnowing2GramJaccard;
            case winnowing3GramJaccard : return winnowing3GramJaccard;
            case winnowing4GramJaccard : return winnowing4GramJaccard;
            case winnowing5GramJaccard : return winnowing5GramJaccard;
            case winnowingShingle2Jaccard : return winnowingShingle2Jaccard;
            case winnowingShingle3Jaccard : return winnowingShingle3Jaccard;
            case winnowingNormalizedTokenJaccard : return winnowingNormalizedTokenJaccard;
            case winnowingNormalized2GramJaccard : return winnowingNormalized2GramJaccard;
            case winnowingNormalized3GramJaccard : return winnowingNormalized3GramJaccard;
            case winnowingNormalized4GramJaccard : return winnowingNormalized4GramJaccard;
            case winnowingNormalized5GramJaccard : return winnowingNormalized5GramJaccard;
            case winnowingNormalizedShingle2Jaccard : return winnowingNormalizedShingle2Jaccard;
            case winnowingNormalizedShingle3Jaccard : return winnowingNormalizedShingle3Jaccard;

            case winnowingTokenDice : return winnowingTokenDice;
            case winnowing2GramDice : return winnowing2GramDice;
            case winnowing3GramDice : return winnowing3GramDice;
            case winnowing4GramDice : return winnowing4GramDice;
            case winnowing5GramDice : return winnowing5GramDice;
            case winnowingShingle2Dice : return winnowingShingle2Dice;
            case winnowingShingle3Dice : return winnowingShingle3Dice;
            case winnowingNormalizedTokenDice : return winnowingNormalizedTokenDice;
            case winnowingNormalized2GramDice : return winnowingNormalized2GramDice;
            case winnowingNormalized3GramDice : return winnowingNormalized3GramDice;
            case winnowingNormalized4GramDice : return winnowingNormalized4GramDice;
            case winnowingNormalized5GramDice : return winnowingNormalized5GramDice;
            case winnowingNormalizedShingle2Dice : return winnowingNormalizedShingle2Dice;
            case winnowingNormalizedShingle3Dice : return winnowingNormalizedShingle3Dice;

            case winnowingTokenDiceVariant : return winnowingTokenDiceVariant;
            case winnowing2GramDiceVariant : return winnowing2GramDiceVariant;
            case winnowing3GramDiceVariant : return winnowing3GramDiceVariant;
            case winnowing4GramDiceVariant : return winnowing4GramDiceVariant;
            case winnowing5GramDiceVariant : return winnowing5GramDiceVariant;
            case winnowingShingle2DiceVariant : return winnowingShingle2DiceVariant;
            case winnowingShingle3DiceVariant : return winnowingShingle3DiceVariant;
            case winnowingNormalizedTokenDiceVariant : return winnowingNormalizedTokenDiceVariant;
            case winnowingNormalized2GramDiceVariant : return winnowingNormalized2GramDiceVariant;
            case winnowingNormalized3GramDiceVariant : return winnowingNormalized3GramDiceVariant;
            case winnowingNormalized4GramDiceVariant : return winnowingNormalized4GramDiceVariant;
            case winnowingNormalized5GramDiceVariant : return winnowingNormalized5GramDiceVariant;
            case winnowingNormalizedShingle2DiceVariant : return winnowingNormalizedShingle2DiceVariant;
            case winnowingNormalizedShingle3DiceVariant : return winnowingNormalizedShingle3DiceVariant;

            case winnowingTokenOverlap : return winnowingTokenOverlap;
            case winnowing2GramOverlap : return winnowing2GramOverlap;
            case winnowing3GramOverlap : return winnowing3GramOverlap;
            case winnowing4GramOverlap : return winnowing4GramOverlap;
            case winnowing5GramOverlap : return winnowing5GramOverlap;
            case winnowingShingle2Overlap : return winnowingShingle2Overlap;
            case winnowingShingle3Overlap : return winnowingShingle3Overlap;
            case winnowingNormalizedTokenOverlap : return winnowingNormalizedTokenOverlap;
            case winnowingNormalized2GramOverlap : return winnowingNormalized2GramOverlap;
            case winnowingNormalized3GramOverlap : return winnowingNormalized3GramOverlap;
            case winnowingNormalized4GramOverlap : return winnowingNormalized4GramOverlap;
            case winnowingNormalized5GramOverlap : return winnowingNormalized5GramOverlap;
            case winnowingNormalizedShingle2Overlap : return winnowingNormalizedShingle2Overlap;
            case winnowingNormalizedShingle3Overlap : return winnowingNormalizedShingle3Overlap;


//            case rkrGstTokenJaccardMinMatchLength1 : return rkrGstTokenJaccardMinMatchLength1;
//            case rkrGst2GramJaccardMinMatchLength1 : return rkrGst2GramJaccardMinMatchLength1;
//            case rkrGst3GramJaccardMinMatchLength1 : return rkrGst3GramJaccardMinMatchLength1;
//            case rkrGst4GramJaccardMinMatchLength1 : return rkrGst4GramJaccardMinMatchLength1;
//            case rkrGst5GramJaccardMinMatchLength1 : return rkrGst5GramJaccardMinMatchLength1;
//            case rkrGstShingle2JaccardMinMatchLength1 : return rkrGstShingle2JaccardMinMatchLength1;
//            case rkrGstShingle3JaccardMinMatchLength1 : return rkrGstShingle3JaccardMinMatchLength1;
//            case rkrGstNormalizedTokenJaccardMinMatchLength1 : return rkrGstNormalizedTokenJaccardMinMatchLength1;
//            case rkrGstNormalized2GramJaccardMinMatchLength1 : return rkrGstNormalized2GramJaccardMinMatchLength1;
//            case rkrGstNormalized3GramJaccardMinMatchLength1 : return rkrGstNormalized3GramJaccardMinMatchLength1;
//            case rkrGstNormalized4GramJaccardMinMatchLength1 : return rkrGstNormalized4GramJaccardMinMatchLength1;
//            case rkrGstNormalized5GramJaccardMinMatchLength1 : return rkrGstNormalized5GramJaccardMinMatchLength1;
//            case rkrGstNormalizedShingle2JaccardMinMatchLength1 : return rkrGstNormalizedShingle2JaccardMinMatchLength1;
//            case rkrGstNormalizedShingle3JaccardMinMatchLength1 : return rkrGstNormalizedShingle3JaccardMinMatchLength1;
//
//            case rkrGstTokenDiceMinMatchLength1 : return rkrGstTokenDiceMinMatchLength1;
//            case rkrGst2GramDiceMinMatchLength1 : return rkrGst2GramDiceMinMatchLength1;
//            case rkrGst3GramDiceMinMatchLength1 : return rkrGst3GramDiceMinMatchLength1;
//            case rkrGst4GramDiceMinMatchLength1 : return rkrGst4GramDiceMinMatchLength1;
//            case rkrGst5GramDiceMinMatchLength1 : return rkrGst5GramDiceMinMatchLength1;
//            case rkrGstShingle2DiceMinMatchLength1 : return rkrGstShingle2DiceMinMatchLength1;
//            case rkrGstShingle3DiceMinMatchLength1 : return rkrGstShingle3DiceMinMatchLength1;
//            case rkrGstNormalizedTokenDiceMinMatchLength1 : return rkrGstNormalizedTokenDiceMinMatchLength1;
//            case rkrGstNormalized2GramDiceMinMatchLength1 : return rkrGstNormalized2GramDiceMinMatchLength1;
//            case rkrGstNormalized3GramDiceMinMatchLength1 : return rkrGstNormalized3GramDiceMinMatchLength1;
//            case rkrGstNormalized4GramDiceMinMatchLength1 : return rkrGstNormalized4GramDiceMinMatchLength1;
//            case rkrGstNormalized5GramDiceMinMatchLength1 : return rkrGstNormalized5GramDiceMinMatchLength1;
//            case rkrGstNormalizedShingle2DiceMinMatchLength1 : return rkrGstNormalizedShingle2DiceMinMatchLength1;
//            case rkrGstNormalizedShingle3DiceMinMatchLength1 : return rkrGstNormalizedShingle3DiceMinMatchLength1;
//
//            case rkrGstTokenDiceVariantMinMatchLength1 : return rkrGstTokenDiceVariantMinMatchLength1;
//            case rkrGst2GramDiceVariantMinMatchLength1 : return rkrGst2GramDiceVariantMinMatchLength1;
//            case rkrGst3GramDiceVariantMinMatchLength1 : return rkrGst3GramDiceVariantMinMatchLength1;
//            case rkrGst4GramDiceVariantMinMatchLength1 : return rkrGst4GramDiceVariantMinMatchLength1;
//            case rkrGst5GramDiceVariantMinMatchLength1 : return rkrGst5GramDiceVariantMinMatchLength1;
//            case rkrGstShingle2DiceVariantMinMatchLength1 : return rkrGstShingle2DiceVariantMinMatchLength1;
//            case rkrGstShingle3DiceVariantMinMatchLength1 : return rkrGstShingle3DiceVariantMinMatchLength1;
//            case rkrGstNormalizedTokenDiceVariantMinMatchLength1 : return rkrGstNormalizedTokenDiceVariantMinMatchLength1;
//            case rkrGstNormalized2GramDiceVariantMinMatchLength1 : return rkrGstNormalized2GramDiceVariantMinMatchLength1;
//            case rkrGstNormalized3GramDiceVariantMinMatchLength1 : return rkrGstNormalized3GramDiceVariantMinMatchLength1;
//            case rkrGstNormalized4GramDiceVariantMinMatchLength1 : return rkrGstNormalized4GramDiceVariantMinMatchLength1;
//            case rkrGstNormalized5GramDiceVariantMinMatchLength1 : return rkrGstNormalized5GramDiceVariantMinMatchLength1;
//            case rkrGstNormalizedShingle2DiceVariantMinMatchLength1 : return rkrGstNormalizedShingle2DiceVariantMinMatchLength1;
//            case rkrGstNormalizedShingle3DiceVariantMinMatchLength1 : return rkrGstNormalizedShingle3DiceVariantMinMatchLength1;
//
//            case rkrGstTokenOverlapMinMatchLength1 : return rkrGstTokenOverlapMinMatchLength1;
//            case rkrGst2GramOverlapMinMatchLength1 : return rkrGst2GramOverlapMinMatchLength1;
//            case rkrGst3GramOverlapMinMatchLength1 : return rkrGst3GramOverlapMinMatchLength1;
//            case rkrGst4GramOverlapMinMatchLength1 : return rkrGst4GramOverlapMinMatchLength1;
//            case rkrGst5GramOverlapMinMatchLength1 : return rkrGst5GramOverlapMinMatchLength1;
//            case rkrGstShingle2OverlapMinMatchLength1 : return rkrGstShingle2OverlapMinMatchLength1;
//            case rkrGstShingle3OverlapMinMatchLength1 : return rkrGstShingle3OverlapMinMatchLength1;
//            case rkrGstNormalizedTokenOverlapMinMatchLength1 : return rkrGstNormalizedTokenOverlapMinMatchLength1;
//            case rkrGstNormalized2GramOverlapMinMatchLength1 : return rkrGstNormalized2GramOverlapMinMatchLength1;
//            case rkrGstNormalized3GramOverlapMinMatchLength1 : return rkrGstNormalized3GramOverlapMinMatchLength1;
//            case rkrGstNormalized4GramOverlapMinMatchLength1 : return rkrGstNormalized4GramOverlapMinMatchLength1;
//            case rkrGstNormalized5GramOverlapMinMatchLength1 : return rkrGstNormalized5GramOverlapMinMatchLength1;
//            case rkrGstNormalizedShingle2OverlapMinMatchLength1 : return rkrGstNormalizedShingle2OverlapMinMatchLength1;
//            case rkrGstNormalizedShingle3OverlapMinMatchLength1 : return rkrGstNormalizedShingle3OverlapMinMatchLength1;
//
//            case rkrGstTokenJaccardMinMatchLength2 : return rkrGstTokenJaccardMinMatchLength2;
//            case rkrGst2GramJaccardMinMatchLength2 : return rkrGst2GramJaccardMinMatchLength2;
//            case rkrGst3GramJaccardMinMatchLength2 : return rkrGst3GramJaccardMinMatchLength2;
//            case rkrGst4GramJaccardMinMatchLength2 : return rkrGst4GramJaccardMinMatchLength2;
//            case rkrGst5GramJaccardMinMatchLength2 : return rkrGst5GramJaccardMinMatchLength2;
//            case rkrGstShingle2JaccardMinMatchLength2 : return rkrGstShingle2JaccardMinMatchLength2;
//            case rkrGstShingle3JaccardMinMatchLength2 : return rkrGstShingle3JaccardMinMatchLength2;
//            case rkrGstNormalizedTokenJaccardMinMatchLength2 : return rkrGstNormalizedTokenJaccardMinMatchLength2;
//            case rkrGstNormalized2GramJaccardMinMatchLength2 : return rkrGstNormalized2GramJaccardMinMatchLength2;
//            case rkrGstNormalized3GramJaccardMinMatchLength2 : return rkrGstNormalized3GramJaccardMinMatchLength2;
//            case rkrGstNormalized4GramJaccardMinMatchLength2 : return rkrGstNormalized4GramJaccardMinMatchLength2;
//            case rkrGstNormalized5GramJaccardMinMatchLength2 : return rkrGstNormalized5GramJaccardMinMatchLength2;
//            case rkrGstNormalizedShingle2JaccardMinMatchLength2 : return rkrGstNormalizedShingle2JaccardMinMatchLength2;
//            case rkrGstNormalizedShingle3JaccardMinMatchLength2 : return rkrGstNormalizedShingle3JaccardMinMatchLength2;
//
//            case rkrGstTokenDiceMinMatchLength2 : return rkrGstTokenDiceMinMatchLength2;
//            case rkrGst2GramDiceMinMatchLength2 : return rkrGst2GramDiceMinMatchLength2;
//            case rkrGst3GramDiceMinMatchLength2 : return rkrGst3GramDiceMinMatchLength2;
//            case rkrGst4GramDiceMinMatchLength2 : return rkrGst4GramDiceMinMatchLength2;
//            case rkrGst5GramDiceMinMatchLength2 : return rkrGst5GramDiceMinMatchLength2;
//            case rkrGstShingle2DiceMinMatchLength2 : return rkrGstShingle2DiceMinMatchLength2;
//            case rkrGstShingle3DiceMinMatchLength2 : return rkrGstShingle3DiceMinMatchLength2;
//            case rkrGstNormalizedTokenDiceMinMatchLength2 : return rkrGstNormalizedTokenDiceMinMatchLength2;
//            case rkrGstNormalized2GramDiceMinMatchLength2 : return rkrGstNormalized2GramDiceMinMatchLength2;
//            case rkrGstNormalized3GramDiceMinMatchLength2 : return rkrGstNormalized3GramDiceMinMatchLength2;
//            case rkrGstNormalized4GramDiceMinMatchLength2 : return rkrGstNormalized4GramDiceMinMatchLength2;
//            case rkrGstNormalized5GramDiceMinMatchLength2 : return rkrGstNormalized5GramDiceMinMatchLength2;
//            case rkrGstNormalizedShingle2DiceMinMatchLength2 : return rkrGstNormalizedShingle2DiceMinMatchLength2;
//            case rkrGstNormalizedShingle3DiceMinMatchLength2 : return rkrGstNormalizedShingle3DiceMinMatchLength2;
//
//            case rkrGstTokenDiceVariantMinMatchLength2 : return rkrGstTokenDiceVariantMinMatchLength2;
//            case rkrGst2GramDiceVariantMinMatchLength2 : return rkrGst2GramDiceVariantMinMatchLength2;
//            case rkrGst3GramDiceVariantMinMatchLength2 : return rkrGst3GramDiceVariantMinMatchLength2;
//            case rkrGst4GramDiceVariantMinMatchLength2 : return rkrGst4GramDiceVariantMinMatchLength2;
//            case rkrGst5GramDiceVariantMinMatchLength2 : return rkrGst5GramDiceVariantMinMatchLength2;
//            case rkrGstShingle2DiceVariantMinMatchLength2 : return rkrGstShingle2DiceVariantMinMatchLength2;
//            case rkrGstShingle3DiceVariantMinMatchLength2 : return rkrGstShingle3DiceVariantMinMatchLength2;
//            case rkrGstNormalizedTokenDiceVariantMinMatchLength2 : return rkrGstNormalizedTokenDiceVariantMinMatchLength2;
//            case rkrGstNormalized2GramDiceVariantMinMatchLength2 : return rkrGstNormalized2GramDiceVariantMinMatchLength2;
//            case rkrGstNormalized3GramDiceVariantMinMatchLength2 : return rkrGstNormalized3GramDiceVariantMinMatchLength2;
//            case rkrGstNormalized4GramDiceVariantMinMatchLength2 : return rkrGstNormalized4GramDiceVariantMinMatchLength2;
//            case rkrGstNormalized5GramDiceVariantMinMatchLength2 : return rkrGstNormalized5GramDiceVariantMinMatchLength2;
//            case rkrGstNormalizedShingle2DiceVariantMinMatchLength2 : return rkrGstNormalizedShingle2DiceVariantMinMatchLength2;
//            case rkrGstNormalizedShingle3DiceVariantMinMatchLength2 : return rkrGstNormalizedShingle3DiceVariantMinMatchLength2;
//
//            case rkrGstTokenOverlapMinMatchLength2 : return rkrGstTokenOverlapMinMatchLength2;
//            case rkrGst2GramOverlapMinMatchLength2 : return rkrGst2GramOverlapMinMatchLength2;
//            case rkrGst3GramOverlapMinMatchLength2 : return rkrGst3GramOverlapMinMatchLength2;
//            case rkrGst4GramOverlapMinMatchLength2 : return rkrGst4GramOverlapMinMatchLength2;
//            case rkrGst5GramOverlapMinMatchLength2 : return rkrGst5GramOverlapMinMatchLength2;
//            case rkrGstShingle2OverlapMinMatchLength2 : return rkrGstShingle2OverlapMinMatchLength2;
//            case rkrGstShingle3OverlapMinMatchLength2 : return rkrGstShingle3OverlapMinMatchLength2;
//            case rkrGstNormalizedTokenOverlapMinMatchLength2 : return rkrGstNormalizedTokenOverlapMinMatchLength2;
//            case rkrGstNormalized2GramOverlapMinMatchLength2 : return rkrGstNormalized2GramOverlapMinMatchLength2;
//            case rkrGstNormalized3GramOverlapMinMatchLength2 : return rkrGstNormalized3GramOverlapMinMatchLength2;
//            case rkrGstNormalized4GramOverlapMinMatchLength2 : return rkrGstNormalized4GramOverlapMinMatchLength2;
//            case rkrGstNormalized5GramOverlapMinMatchLength2 : return rkrGstNormalized5GramOverlapMinMatchLength2;
//            case rkrGstNormalizedShingle2OverlapMinMatchLength2 : return rkrGstNormalizedShingle2OverlapMinMatchLength2;
//            case rkrGstNormalizedShingle3OverlapMinMatchLength2 : return rkrGstNormalizedShingle3OverlapMinMatchLength2;
//
//            case rkrGstTokenJaccardMinMatchLength3 : return rkrGstTokenJaccardMinMatchLength3;
//            case rkrGst2GramJaccardMinMatchLength3 : return rkrGst2GramJaccardMinMatchLength3;
//            case rkrGst3GramJaccardMinMatchLength3 : return rkrGst3GramJaccardMinMatchLength3;
//            case rkrGst4GramJaccardMinMatchLength3 : return rkrGst4GramJaccardMinMatchLength3;
//            case rkrGst5GramJaccardMinMatchLength3 : return rkrGst5GramJaccardMinMatchLength3;
//            case rkrGstShingle2JaccardMinMatchLength3 : return rkrGstShingle2JaccardMinMatchLength3;
//            case rkrGstShingle3JaccardMinMatchLength3 : return rkrGstShingle3JaccardMinMatchLength3;
//            case rkrGstNormalizedTokenJaccardMinMatchLength3 : return rkrGstNormalizedTokenJaccardMinMatchLength3;
//            case rkrGstNormalized2GramJaccardMinMatchLength3 : return rkrGstNormalized2GramJaccardMinMatchLength3;
//            case rkrGstNormalized3GramJaccardMinMatchLength3 : return rkrGstNormalized3GramJaccardMinMatchLength3;
//            case rkrGstNormalized4GramJaccardMinMatchLength3 : return rkrGstNormalized4GramJaccardMinMatchLength3;
//            case rkrGstNormalized5GramJaccardMinMatchLength3 : return rkrGstNormalized5GramJaccardMinMatchLength3;
//            case rkrGstNormalizedShingle2JaccardMinMatchLength3 : return rkrGstNormalizedShingle2JaccardMinMatchLength3;
//            case rkrGstNormalizedShingle3JaccardMinMatchLength3 : return rkrGstNormalizedShingle3JaccardMinMatchLength3;
//
//            case rkrGstTokenDiceMinMatchLength3 : return rkrGstTokenDiceMinMatchLength3;
//            case rkrGst2GramDiceMinMatchLength3 : return rkrGst2GramDiceMinMatchLength3;
//            case rkrGst3GramDiceMinMatchLength3 : return rkrGst3GramDiceMinMatchLength3;
//            case rkrGst4GramDiceMinMatchLength3 : return rkrGst4GramDiceMinMatchLength3;
//            case rkrGst5GramDiceMinMatchLength3 : return rkrGst5GramDiceMinMatchLength3;
//            case rkrGstShingle2DiceMinMatchLength3 : return rkrGstShingle2DiceMinMatchLength3;
//            case rkrGstShingle3DiceMinMatchLength3 : return rkrGstShingle3DiceMinMatchLength3;
//            case rkrGstNormalizedTokenDiceMinMatchLength3 : return rkrGstNormalizedTokenDiceMinMatchLength3;
//            case rkrGstNormalized2GramDiceMinMatchLength3 : return rkrGstNormalized2GramDiceMinMatchLength3;
//            case rkrGstNormalized3GramDiceMinMatchLength3 : return rkrGstNormalized3GramDiceMinMatchLength3;
//            case rkrGstNormalized4GramDiceMinMatchLength3 : return rkrGstNormalized4GramDiceMinMatchLength3;
//            case rkrGstNormalized5GramDiceMinMatchLength3 : return rkrGstNormalized5GramDiceMinMatchLength3;
//            case rkrGstNormalizedShingle2DiceMinMatchLength3 : return rkrGstNormalizedShingle2DiceMinMatchLength3;
//            case rkrGstNormalizedShingle3DiceMinMatchLength3 : return rkrGstNormalizedShingle3DiceMinMatchLength3;
//
//            case rkrGstTokenDiceVariantMinMatchLength3 : return rkrGstTokenDiceVariantMinMatchLength3;
//            case rkrGst2GramDiceVariantMinMatchLength3 : return rkrGst2GramDiceVariantMinMatchLength3;
//            case rkrGst3GramDiceVariantMinMatchLength3 : return rkrGst3GramDiceVariantMinMatchLength3;
//            case rkrGst4GramDiceVariantMinMatchLength3 : return rkrGst4GramDiceVariantMinMatchLength3;
//            case rkrGst5GramDiceVariantMinMatchLength3 : return rkrGst5GramDiceVariantMinMatchLength3;
//            case rkrGstShingle2DiceVariantMinMatchLength3 : return rkrGstShingle2DiceVariantMinMatchLength3;
//            case rkrGstShingle3DiceVariantMinMatchLength3 : return rkrGstShingle3DiceVariantMinMatchLength3;
//            case rkrGstNormalizedTokenDiceVariantMinMatchLength3 : return rkrGstNormalizedTokenDiceVariantMinMatchLength3;
//            case rkrGstNormalized2GramDiceVariantMinMatchLength3 : return rkrGstNormalized2GramDiceVariantMinMatchLength3;
//            case rkrGstNormalized3GramDiceVariantMinMatchLength3 : return rkrGstNormalized3GramDiceVariantMinMatchLength3;
//            case rkrGstNormalized4GramDiceVariantMinMatchLength3 : return rkrGstNormalized4GramDiceVariantMinMatchLength3;
//            case rkrGstNormalized5GramDiceVariantMinMatchLength3 : return rkrGstNormalized5GramDiceVariantMinMatchLength3;
//            case rkrGstNormalizedShingle2DiceVariantMinMatchLength3 : return rkrGstNormalizedShingle2DiceVariantMinMatchLength3;
//            case rkrGstNormalizedShingle3DiceVariantMinMatchLength3 : return rkrGstNormalizedShingle3DiceVariantMinMatchLength3;
//
//            case rkrGstTokenOverlapMinMatchLength3 : return rkrGstTokenOverlapMinMatchLength3;
//            case rkrGst2GramOverlapMinMatchLength3 : return rkrGst2GramOverlapMinMatchLength3;
//            case rkrGst3GramOverlapMinMatchLength3 : return rkrGst3GramOverlapMinMatchLength3;
//            case rkrGst4GramOverlapMinMatchLength3 : return rkrGst4GramOverlapMinMatchLength3;
//            case rkrGst5GramOverlapMinMatchLength3 : return rkrGst5GramOverlapMinMatchLength3;
//            case rkrGstShingle2OverlapMinMatchLength3 : return rkrGstShingle2OverlapMinMatchLength3;
//            case rkrGstShingle3OverlapMinMatchLength3 : return rkrGstShingle3OverlapMinMatchLength3;
//            case rkrGstNormalizedTokenOverlapMinMatchLength3 : return rkrGstNormalizedTokenOverlapMinMatchLength3;
//            case rkrGstNormalized2GramOverlapMinMatchLength3 : return rkrGstNormalized2GramOverlapMinMatchLength3;
//            case rkrGstNormalized3GramOverlapMinMatchLength3 : return rkrGstNormalized3GramOverlapMinMatchLength3;
//            case rkrGstNormalized4GramOverlapMinMatchLength3 : return rkrGstNormalized4GramOverlapMinMatchLength3;
//            case rkrGstNormalized5GramOverlapMinMatchLength3 : return rkrGstNormalized5GramOverlapMinMatchLength3;
//            case rkrGstNormalizedShingle2OverlapMinMatchLength3 : return rkrGstNormalizedShingle2OverlapMinMatchLength3;
//            case rkrGstNormalizedShingle3OverlapMinMatchLength3 : return rkrGstNormalizedShingle3OverlapMinMatchLength3;


            case cosineNormalizedTokensBool :
                return cosineNormalizedTokensBool;
            case cosineNormalizedTokensTermFrequency :
                return cosineNormalizedTokensTermFrequency;
            case cosineNormalizedTokensNormalizedTermFrequency :
                return cosineNormalizedTokensNormalizedTermFrequency;
            case cosineNormalized2GramsBool :
                return cosineNormalized2GramsBool;
            case cosineNormalized3GramsBool :
                return cosineNormalized3GramsBool;
            case cosineNormalized4GramsBool :
                return cosineNormalized4GramsBool;
            case cosineNormalized5GramsBool :
                return cosineNormalized5GramsBool;
            case cosineNormalized2GramsTermFrequency :
                return cosineNormalized2GramsTermFrequency;
            case cosineNormalized3GramsTermFrequency :
                return cosineNormalized3GramsTermFrequency;
            case cosineNormalized4GramsTermFrequency :
                return cosineNormalized4GramsTermFrequency;
            case cosineNormalized5GramsTermFrequency :
                return cosineNormalized5GramsTermFrequency;
            case cosineNormalized2GramsNormalizedTermFrequency :
                return cosineNormalized2GramsNormalizedTermFrequency;
            case cosineNormalized3GramsNormalizedTermFrequency :
                return cosineNormalized3GramsNormalizedTermFrequency;
            case cosineNormalized4GramsNormalizedTermFrequency :
                return cosineNormalized4GramsNormalizedTermFrequency;
            case cosineNormalized5GramsNormalizedTermFrequency :
                return cosineNormalized5GramsNormalizedTermFrequency;
            case cosineNormalizedShingle2Bool :
                return cosineNormalizedShingle2Bool;
            case cosineNormalizedShingle3Bool :
                return cosineNormalizedShingle3Bool;
            case cosineNormalizedShingle2TermFrequency :
                return cosineNormalizedShingle2TermFrequency;
            case cosineNormalizedShingle3TermFrequency :
                return cosineNormalizedShingle3TermFrequency;
            case cosineNormalizedShingle2NormalizedTermFrequency :
                return cosineNormalizedShingle2NormalizedTermFrequency;
            case cosineNormalizedShingle3NormalizedTermFrequency :
                return cosineNormalizedShingle3NormalizedTermFrequency;

            case manhattanNormalizedTokens :
                return manhattanNormalizedTokens;
            case manhattanNormalized2Grams :
                return manhattanNormalized2Grams;
            case manhattanNormalized3Grams :
                return manhattanNormalized3Grams;
            case manhattanNormalized4Grams :
                return manhattanNormalized4Grams;
            case manhattanNormalized5Grams :
                return manhattanNormalized5Grams;
            case manhattanNormalizedShingles2 :
                return manhattanNormalizedShingles2;
            case manhattanNormalizedShingles3 :
                return manhattanNormalizedShingles3;

            case jaccardTokens:
                return jaccardTokens;
            case jaccardNormalizedTokens:
                return jaccardNormalizedTokens;
            case jaccard2Grams:
                return jaccard2Grams;
            case jaccard3Grams:
                return jaccard3Grams;
            case jaccard4Grams:
                return jaccard4Grams;
            case jaccard5Grams:
                return jaccard5Grams;
            case jaccardNormalized2Grams:
                return jaccardNormalized2Grams;
            case jaccardNormalized3Grams:
                return jaccardNormalized3Grams;
            case jaccardNormalized4Grams:
                return jaccardNormalized4Grams;
            case jaccardNormalized5Grams:
                return jaccardNormalized5Grams;
            case jaccardNormalizedPadding2grams:
                return jaccardNormalizedPadding2grams;
            case jaccardNormalizedPadding3grams:
                return jaccardNormalizedPadding3grams;
            case jaccardNormalizedPadding4grams:
                return jaccardNormalizedPadding4grams;
            case jaccardNormalizedPadding5grams:
                return jaccardNormalizedPadding5grams;
            case jaccardShingles2:
                return jaccardShingles2;
            case jaccardShingles3:
                return jaccardShingles3;
            case jaccardNormalizedShingles2:
                return jaccardNormalizedShingles2;
            case jaccardNormalizedShingles3:
                return jaccardNormalizedShingles3;

            case diceTokens:
                return diceTokens;
            case diceNormalizedTokens:
                return diceNormalizedTokens;
            case dice2Grams:
                return dice2Grams;
            case dice3Grams:
                return dice3Grams;
            case dice4Grams:
                return dice4Grams;
            case dice5Grams:
                return dice5Grams;
            case diceNormalized2Grams:
                return diceNormalized2Grams;
            case diceNormalized3Grams:
                return diceNormalized3Grams;
            case diceNormalized4Grams:
                return diceNormalized4Grams;
            case diceNormalized5Grams:
                return diceNormalized5Grams;
            case diceNormalizedPadding2grams:
                return diceNormalizedPadding2grams;
            case diceNormalizedPadding3grams:
                return diceNormalizedPadding3grams;
            case diceNormalizedPadding4grams:
                return diceNormalizedPadding4grams;
            case diceNormalizedPadding5grams:
                return diceNormalizedPadding5grams;
            case diceShingles2:
                return diceShingles2;
            case diceShingles3:
                return diceShingles3;
            case diceNormalizedShingles2:
                return diceNormalizedShingles2;
            case diceNormalizedShingles3:
                return diceNormalizedShingles3;

            case diceVariantTokens:
                return diceVariantTokens;
            case diceVariantNormalizedTokens:
                return diceVariantNormalizedTokens;
            case diceVariant2Grams:
                return diceVariant2Grams;
            case diceVariant3Grams:
                return diceVariant3Grams;
            case diceVariant4Grams:
                return diceVariant4Grams;
            case diceVariant5Grams:
                return diceVariant5Grams;
            case diceVariantNormalized2Grams:
                return diceVariantNormalized2Grams;
            case diceVariantNormalized3Grams:
                return diceVariantNormalized3Grams;
            case diceVariantNormalized4Grams:
                return diceVariantNormalized4Grams;
            case diceVariantNormalized5Grams:
                return diceVariantNormalized5Grams;
            case diceVariantNormalizedPadding2grams:
                return diceVariantNormalizedPadding2grams;
            case diceVariantNormalizedPadding3grams:
                return diceVariantNormalizedPadding3grams;
            case diceVariantNormalizedPadding4grams:
                return diceVariantNormalizedPadding4grams;
            case diceVariantNormalizedPadding5grams:
                return diceVariantNormalizedPadding5grams;
            case diceVariantShingles2:
                return diceVariantShingles2;
            case diceVariantShingles3:
                return diceVariantShingles3;
            case diceVariantNormalizedShingles2:
                return diceVariantNormalizedShingles2;
            case diceVariantNormalizedShingles3:
                return diceVariantNormalizedShingles3;

            case overlapTokens:
                return overlapTokens;
            case overlapNormalizedTokens:
                return overlapNormalizedTokens;
            case overlap2Grams:
                return overlap2Grams;
            case overlap3Grams:
                return overlap3Grams;
            case overlap4Grams:
                return overlap4Grams;
            case overlap5Grams:
                return overlap5Grams;
            case overlapNormalized2Grams:
                return overlapNormalized2Grams;
            case overlapNormalized3Grams:
                return overlapNormalized3Grams;
            case overlapNormalized4Grams:
                return overlapNormalized4Grams;
            case overlapNormalized5Grams:
                return overlapNormalized5Grams;
            case overlapNormalizedPadding2grams:
                return overlapNormalizedPadding2grams;
            case overlapNormalizedPadding3grams:
                return overlapNormalizedPadding3grams;
            case overlapNormalizedPadding4grams:
                return overlapNormalizedPadding4grams;
            case overlapNormalizedPadding5grams:
                return overlapNormalizedPadding5grams;
            case overlapShingles2:
                return overlapShingles2;
            case overlapShingles3:
                return overlapShingles3;
            case overlapNormalizedShingles2:
                return overlapNormalizedShingles2;
            case overlapNormalizedShingles3:
                return overlapNormalizedShingles3;

                default:
                    return null;
        }
    }

    public MetricsComparator(String pathToDirectoryOfPostHistories, String pathToDirectoryOfCompletedCSVs) throws IOException {

        this.pathToDirectoryOfAllPostHistories = pathToDirectoryOfPostHistories;
        this.pathToDirectoryOfAllCompletedCSVs = pathToDirectoryOfCompletedCSVs;

        postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfPostHistories);

        groundTruth = extractListOfListsOfBlockLifeSpansOfAllExportedCSVs(pathToDirectoryOfCompletedCSVs);
        groundTruthBlocks_text = filterListOfListOfBlockLifeSpansByType(groundTruth, BlockLifeSpan.Type.textblock);
        groundTruthBlocks_code = filterListOfListOfBlockLifeSpansByType(groundTruth, BlockLifeSpan.Type.codeblock);

        checkWhetherSetOfCompletedPostsIsSameAsSetOfPostHistories();

        checkWhetherNumberOfBlocksIsSame();
    }


    private void checkWhetherSetOfCompletedPostsIsSameAsSetOfPostHistories() {
        Vector<Integer> postIdsOfGroundTruth = new Vector<>();
        Vector<Integer> postIdsOfPostHistories = new Vector<>();
        for(int i=0; i<groundTruth.size(); i++){
            postIdsOfGroundTruth.add(groundTruth.get(i).get(0).firstElement().getPostId());
        }

        for(int i=0; i<postVersionsListManagement.postVersionLists.size(); i++){
            postIdsOfPostHistories.add(postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId());
        }

        Collections.sort(postIdsOfGroundTruth);
        Collections.sort(postIdsOfPostHistories);

        if(!postIdsOfGroundTruth.equals(postIdsOfPostHistories)){
            Vector<Integer> invalidPosts = new Vector<>();
            for(int i=0; i<postIdsOfGroundTruth.size(); i++){
                if(!postIdsOfPostHistories.contains(postIdsOfGroundTruth.get(i))){
                    invalidPosts.add(postIdsOfGroundTruth.get(i));
                }
            }
            for(int i=0; i<postIdsOfPostHistories.size(); i++){
                if(!postIdsOfGroundTruth.contains(postIdsOfPostHistories.get(i))){
                    invalidPosts.add(postIdsOfPostHistories.get(i));
                }
            }

            System.err.println("Every post version list must have a corresponding completed csv, but doesn't.");
            System.err.println("Check the following post(s): " + invalidPosts);
            System.exit(0);
        }
    }

    private void checkWhetherNumberOfBlocksIsSame() {

        for(int i=0; i<groundTruthBlocks_text.size(); i++){

            int numberOfTextBlocksOverallInGroundTruth = 0;
            for(int j=0; j<groundTruthBlocks_text.get(i).size(); j++){
                numberOfTextBlocksOverallInGroundTruth += groundTruthBlocks_text.get(i).get(j).size();
            }

            int numberOfTextBlocksOverallInComputedMetric = 0;
            List<BlockLifeSpan> blockLifeSpansComputedText = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionsListManagement.postVersionLists.get(i), BlockLifeSpan.Type.textblock);
            for(int j=0; j<blockLifeSpansComputedText.size(); j++){
                numberOfTextBlocksOverallInComputedMetric += blockLifeSpansComputedText.get(j).size();
            }

            if(numberOfTextBlocksOverallInGroundTruth != numberOfTextBlocksOverallInComputedMetric){
                System.err.println(
                        "Number of text blocks that will be compared must be the same but are different in post with id "
                                + postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId()
                + " (" + numberOfTextBlocksOverallInGroundTruth + " and " + numberOfTextBlocksOverallInComputedMetric + ")");
                System.exit(0);
            }
        }

        for(int i=0; i<groundTruthBlocks_code.size(); i++){

            int numberOfCodeBlocksOverallInGroundTruth = 0;
            for(int j=0; j<groundTruthBlocks_code.get(i).size(); j++){
                numberOfCodeBlocksOverallInGroundTruth += groundTruthBlocks_code.get(i).get(j).size();
            }

            int numberOfCodeBlocksOverallInComputedMetric = 0;
            List<BlockLifeSpan> blockLifeSpansComputedCode = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionsListManagement.postVersionLists.get(i), BlockLifeSpan.Type.codeblock);
            for(int j=0; j<blockLifeSpansComputedCode.size(); j++){
                numberOfCodeBlocksOverallInComputedMetric += blockLifeSpansComputedCode.get(j).size();
            }

            if(numberOfCodeBlocksOverallInGroundTruth != numberOfCodeBlocksOverallInComputedMetric){
                System.err.println(
                        "Number of code blocks that will be compared must be the same but are different in post with id "
                                + postVersionsListManagement.postVersionLists.get(i).getFirst().getPostId()
                                + " (" + numberOfCodeBlocksOverallInGroundTruth + " and " + numberOfCodeBlocksOverallInComputedMetric + ")");
                System.exit(0);
            }
        }
    }

    // ****** Edit based *****
    static BiFunction<String, String, Double> levenshteinStandard = (x, y) -> levenshtein(x,y);
    public static BiFunction<String, String, Double> levenshteinNormalized = (x, y) -> levenshteinNormalized(x,y);

    static BiFunction<String, String, Double> damerauLevenshteinStandard = (x, y) -> damerauLevenshtein(x,y);
    static BiFunction<String, String, Double> damerauLevenshteinNormalized = (x, y) -> damerauLevenshteinNormalized(x,y);

    static BiFunction<String, String, Double> optimalAlignmentStandard = (x, y) -> optimalAlignment(x,y);
    static BiFunction<String, String, Double> optimalAlignmentNormalized = (x, y) -> optimalAlignmentNormalized(x,y);

    static BiFunction<String, String, Double> optimalAlignment2GramFingerprint = (x, y) -> nGramFingerprintOptimalAlignment(x,y,2);
    static BiFunction<String, String, Double> optimalAlignment3GramFingerprint = (x, y) -> nGramFingerprintOptimalAlignment(x,y,3);
    static BiFunction<String, String, Double> optimalAlignment4GramFingerprint = (x, y) -> nGramFingerprintOptimalAlignment(x,y,4);
    static BiFunction<String, String, Double> optimalAlignment5GramFingerprint = (x, y) -> nGramFingerprintOptimalAlignment(x,y,5);
    static BiFunction<String, String, Double> optimalAlignmentShingle2Fingerprint = (x, y) -> shingleFingerprintOptimalAlignment(x,y,2);
    static BiFunction<String, String, Double> optimalAlignmentShingle3Fingerprint = (x, y) -> shingleFingerprintOptimalAlignment(x,y,3);
    static BiFunction<String, String, Double> optimalAlignment2GramFingerprintNormalized = (x, y) -> nGramFingerprintOptimalAlignmentNormalized(x,y,2);
    static BiFunction<String, String, Double> optimalAlignment3GramFingerprintNormalized = (x, y) -> nGramFingerprintOptimalAlignmentNormalized(x,y,3);
    static BiFunction<String, String, Double> optimalAlignment4GramFingerprintNormalized = (x, y) -> nGramFingerprintOptimalAlignmentNormalized(x,y,4);
    static BiFunction<String, String, Double> optimalAlignment5GramFingerprintNormalized = (x, y) -> nGramFingerprintOptimalAlignmentNormalized(x,y,5);
    static BiFunction<String, String, Double> optimalAlignmentShingle2FingerprintNormalized = (x, y) -> shingleFingerprintOptimalAlignmentNormalized(x,y,2);
    static BiFunction<String, String, Double> optimalAlignmentShingle3FingerprintNormalized = (x, y) -> shingleFingerprintOptimalAlignmentNormalized(x,y,3);

    static BiFunction<String, String, Double> longestCommonSubsequenceStandard = (x, y) -> longestCommonSubsequence(x,y);
    static BiFunction<String, String, Double> longestCommonSubsequenceNormalized = (x, y) -> longestCommonSubsequenceNormalized(x,y);
    static BiFunction<String, String, Double> longestCommonSubsequence2GramFingerprint = (x, y) -> nGramFingerprintLongestCommonSubsequence(x,y,2);
    static BiFunction<String, String, Double> longestCommonSubsequence3GramFingerprint = (x, y) -> nGramFingerprintLongestCommonSubsequence(x,y,3);
    static BiFunction<String, String, Double> longestCommonSubsequence4GramFingerprint = (x, y) -> nGramFingerprintLongestCommonSubsequence(x,y,4);
    static BiFunction<String, String, Double> longestCommonSubsequence5GramFingerprint = (x, y) -> nGramFingerprintLongestCommonSubsequence(x,y,5);
    static BiFunction<String, String, Double> longestCommonSubsequenceShingle2Fingerprint = (x, y) -> shingleFingerprintLongestCommonSubsequence(x,y,2);
    static BiFunction<String, String, Double> longestCommonSubsequenceShingle3Fingerprint = (x, y) -> shingleFingerprintLongestCommonSubsequence(x,y,3);

    static BiFunction<String, String, Double> longestCommonSubsequence2GramFingerprintNormalized = (x, y) -> nGramFingerprintLongestCommonSubsequenceNormalized(x,y,2);
    static BiFunction<String, String, Double> longestCommonSubsequence3GramFingerprintNormalized = (x, y) -> nGramFingerprintLongestCommonSubsequenceNormalized(x,y,3);
    static BiFunction<String, String, Double> longestCommonSubsequence4GramFingerprintNormalized = (x, y) -> nGramFingerprintLongestCommonSubsequenceNormalized(x,y,4);
    static BiFunction<String, String, Double> longestCommonSubsequence5GramFingerprintNormalized = (x, y) -> nGramFingerprintLongestCommonSubsequenceNormalized(x,y,5);
    static BiFunction<String, String, Double> longestCommonSubsequenceShingle2FingerprintNormalized = (x, y) -> shingleFingerprintLongestCommonSubsequenceNormalized(x,y,2);
    static BiFunction<String, String, Double> longestCommonSubsequenceShingle3FingerprintNormalized = (x, y) -> shingleFingerprintLongestCommonSubsequenceNormalized(x,y,3);

    // ****** Fingerprint based
    static BiFunction<String, String, Double> winnowingTokenJaccard = (x, y) -> winnowingTokenJaccard(x,y);
    static BiFunction<String, String, Double> winnowing2GramJaccard = (x, y) -> winnowingNGramJaccard(x,y,2);
    static BiFunction<String, String, Double> winnowing3GramJaccard = (x, y) -> winnowingNGramJaccard(x,y,3);
    static BiFunction<String, String, Double> winnowing4GramJaccard = (x, y) -> winnowingNGramJaccard(x,y,4);
    static BiFunction<String, String, Double> winnowing5GramJaccard = (x, y) -> winnowingNGramJaccard(x,y,5);
    static BiFunction<String, String, Double> winnowingShingle2Jaccard = (x, y) -> winnowingShingleJaccard(x,y,2);
    static BiFunction<String, String, Double> winnowingShingle3Jaccard = (x, y) -> winnowingShingleJaccard(x,y,3);

    static BiFunction<String, String, Double> winnowingNormalizedTokenJaccard = (x, y) -> winnowingNormalizedTokenJaccard(x,y);
    static BiFunction<String, String, Double> winnowingNormalized2GramJaccard = (x, y) -> winnowingNormalizedNGramJaccard(x,y,2);
    static BiFunction<String, String, Double> winnowingNormalized3GramJaccard = (x, y) -> winnowingNormalizedNGramJaccard(x,y,3);
    static BiFunction<String, String, Double> winnowingNormalized4GramJaccard = (x, y) -> winnowingNormalizedNGramJaccard(x,y,4);
    static BiFunction<String, String, Double> winnowingNormalized5GramJaccard = (x, y) -> winnowingNormalizedNGramJaccard(x,y,5);
    static BiFunction<String, String, Double> winnowingNormalizedShingle2Jaccard = (x, y) -> winnowingNormalizedShingleJaccard(x,y,2);
    static BiFunction<String, String, Double> winnowingNormalizedShingle3Jaccard = (x, y) -> winnowingNormalizedShingleJaccard(x,y,3);

    static BiFunction<String, String, Double> winnowingTokenDice = (x, y) -> winnowingTokenDice(x,y);
    static BiFunction<String, String, Double> winnowing2GramDice = (x, y) -> winnowingNGramDice(x,y,2);
    static BiFunction<String, String, Double> winnowing3GramDice = (x, y) -> winnowingNGramDice(x,y,3);
    static BiFunction<String, String, Double> winnowing4GramDice = (x, y) -> winnowingNGramDice(x,y,4);
    static BiFunction<String, String, Double> winnowing5GramDice = (x, y) -> winnowingNGramDice(x,y,5);
    static BiFunction<String, String, Double> winnowingShingle2Dice = (x, y) -> winnowingShingleDice(x,y,2);
    static BiFunction<String, String, Double> winnowingShingle3Dice = (x, y) -> winnowingShingleDice(x,y,3);

    static BiFunction<String, String, Double> winnowingNormalizedTokenDice = (x, y) -> winnowingNormalizedTokenDice(x,y);
    static BiFunction<String, String, Double> winnowingNormalized2GramDice = (x, y) -> winnowingNormalizedNGramDice(x,y,2);
    static BiFunction<String, String, Double> winnowingNormalized3GramDice = (x, y) -> winnowingNormalizedNGramDice(x,y,3);
    static BiFunction<String, String, Double> winnowingNormalized4GramDice = (x, y) -> winnowingNormalizedNGramDice(x,y,4);
    static BiFunction<String, String, Double> winnowingNormalized5GramDice = (x, y) -> winnowingNormalizedNGramDice(x,y,5);
    static BiFunction<String, String, Double> winnowingNormalizedShingle2Dice = (x, y) -> winnowingNormalizedShingleDice(x,y,2);
    static BiFunction<String, String, Double> winnowingNormalizedShingle3Dice = (x, y) -> winnowingNormalizedShingleDice(x,y,3);

    static BiFunction<String, String, Double> winnowingTokenDiceVariant = (x, y) -> winnowingTokenDiceVariant(x,y);
    static BiFunction<String, String, Double> winnowing2GramDiceVariant = (x, y) -> winnowingNGramDiceVariant(x,y,2);
    static BiFunction<String, String, Double> winnowing3GramDiceVariant = (x, y) -> winnowingNGramDiceVariant(x,y,3);
    static BiFunction<String, String, Double> winnowing4GramDiceVariant = (x, y) -> winnowingNGramDiceVariant(x,y,4);
    static BiFunction<String, String, Double> winnowing5GramDiceVariant = (x, y) -> winnowingNGramDiceVariant(x,y,5);
    static BiFunction<String, String, Double> winnowingShingle2DiceVariant = (x, y) -> winnowingShingleDiceVariant(x,y,2);
    static BiFunction<String, String, Double> winnowingShingle3DiceVariant = (x, y) -> winnowingShingleDiceVariant(x,y,3);

    static BiFunction<String, String, Double> winnowingNormalizedTokenDiceVariant = (x, y) -> winnowingNormalizedTokenDiceVariant(x,y);
    static BiFunction<String, String, Double> winnowingNormalized2GramDiceVariant = (x, y) -> winnowingNormalizedNGramDiceVariant(x,y,2);
    static BiFunction<String, String, Double> winnowingNormalized3GramDiceVariant = (x, y) -> winnowingNormalizedNGramDiceVariant(x,y,3);
    static BiFunction<String, String, Double> winnowingNormalized4GramDiceVariant = (x, y) -> winnowingNormalizedNGramDiceVariant(x,y,4);
    static BiFunction<String, String, Double> winnowingNormalized5GramDiceVariant = (x, y) -> winnowingNormalizedNGramDiceVariant(x,y,5);
    static BiFunction<String, String, Double> winnowingNormalizedShingle2DiceVariant = (x, y) -> winnowingNormalizedShingleDiceVariant(x,y,2);
    static BiFunction<String, String, Double> winnowingNormalizedShingle3DiceVariant = (x, y) -> winnowingNormalizedShingleDiceVariant(x,y,3);

    static BiFunction<String, String, Double> winnowingTokenOverlap = (x, y) -> winnowingTokenOverlap(x,y);
    static BiFunction<String, String, Double> winnowing2GramOverlap = (x, y) -> winnowingNGramOverlap(x,y,2);
    static BiFunction<String, String, Double> winnowing3GramOverlap = (x, y) -> winnowingNGramOverlap(x,y,3);
    static BiFunction<String, String, Double> winnowing4GramOverlap = (x, y) -> winnowingNGramOverlap(x,y,4);
    static BiFunction<String, String, Double> winnowing5GramOverlap = (x, y) -> winnowingNGramOverlap(x,y,5);
    static BiFunction<String, String, Double> winnowingShingle2Overlap = (x, y) -> winnowingShingleOverlap(x,y,2);
    static BiFunction<String, String, Double> winnowingShingle3Overlap = (x, y) -> winnowingShingleOverlap(x,y,3);

    static BiFunction<String, String, Double> winnowingNormalizedTokenOverlap = (x, y) -> winnowingNormalizedTokenOverlap(x,y);
    static BiFunction<String, String, Double> winnowingNormalized2GramOverlap = (x, y) -> winnowingNormalizedNGramOverlap(x,y,2);
    static BiFunction<String, String, Double> winnowingNormalized3GramOverlap = (x, y) -> winnowingNormalizedNGramOverlap(x,y,3);
    static BiFunction<String, String, Double> winnowingNormalized4GramOverlap = (x, y) -> winnowingNormalizedNGramOverlap(x,y,4);
    static BiFunction<String, String, Double> winnowingNormalized5GramOverlap = (x, y) -> winnowingNormalizedNGramOverlap(x,y,5);
    static BiFunction<String, String, Double> winnowingNormalizedShingle2Overlap = (x, y) -> winnowingNormalizedShingleOverlap(x,y,2);
    static BiFunction<String, String, Double> winnowingNormalizedShingle3Overlap = (x, y) -> winnowingNormalizedShingleOverlap(x,y,3);


//    static BiFunction<String, String, Double> rkrGstTokenJaccardMinMatchLength1 = (x, y) -> rkrGstTokenJaccard(x,y, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramJaccardMinMatchLength1 = (x, y) -> rkrGstNGramJaccard(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramJaccardMinMatchLength1 = (x, y) -> rkrGstNGramJaccard(x,y,3, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramJaccardMinMatchLength1 = (x, y) -> rkrGstNGramJaccard(x,y,4, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramJaccardMinMatchLength1 = (x, y) -> rkrGstNGramJaccard(x,y,5, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2JaccardMinMatchLength1 = (x, y) -> rkrGstShingleJaccard(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3JaccardMinMatchLength1 = (x, y) -> rkrGstShingleJaccard(x,y,3, 1, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenJaccardMinMatchLength1 = (x, y) -> rkrGstNormalizedTokenJaccard(x,y,1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramJaccardMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramJaccardMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,3, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramJaccardMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,4, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramJaccardMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,5, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2JaccardMinMatchLength1 = (x, y) -> rkrGstNormalizedShingleJaccard(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3JaccardMinMatchLength1 = (x, y) -> rkrGstNormalizedShingleJaccard(x,y,3, 1, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenDiceMinMatchLength1 = (x, y) -> rkrGstTokenDice(x,y, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramDiceMinMatchLength1 = (x, y) -> rkrGstNGramDice(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramDiceMinMatchLength1 = (x, y) -> rkrGstNGramDice(x,y,3, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramDiceMinMatchLength1 = (x, y) -> rkrGstNGramDice(x,y,4, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramDiceMinMatchLength1 = (x, y) -> rkrGstNGramDice(x,y,5, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2DiceMinMatchLength1 = (x, y) -> rkrGstShingleDice(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3DiceMinMatchLength1 = (x, y) -> rkrGstShingleDice(x,y,3, 1, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenDiceMinMatchLength1 = (x, y) -> rkrGstNormalizedTokenDice(x,y, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramDiceMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramDice(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramDiceMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramDice(x,y,3, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramDiceMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramDice(x,y,4, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramDiceMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramDice(x,y,5, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2DiceMinMatchLength1 = (x, y) -> rkrGstNormalizedShingleDice(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3DiceMinMatchLength1 = (x, y) -> rkrGstNormalizedShingleDice(x,y,3, 1, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenDiceVariantMinMatchLength1 = (x, y) -> rkrGstTokenDiceVariant(x,y, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramDiceVariantMinMatchLength1 = (x, y) -> rkrGstNGramDiceVariant(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramDiceVariantMinMatchLength1 = (x, y) -> rkrGstNGramDiceVariant(x,y,3, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramDiceVariantMinMatchLength1 = (x, y) -> rkrGstNGramDiceVariant(x,y,4, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramDiceVariantMinMatchLength1 = (x, y) -> rkrGstNGramDiceVariant(x,y,5, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2DiceVariantMinMatchLength1 = (x, y) -> rkrGstShingleDiceVariant(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3DiceVariantMinMatchLength1 = (x, y) -> rkrGstShingleDiceVariant(x,y,3, 1, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenDiceVariantMinMatchLength1 = (x, y) -> rkrGstNormalizedTokenDiceVariant(x,y, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramDiceVariantMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramDiceVariantMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,3, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramDiceVariantMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,4, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramDiceVariantMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,5, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2DiceVariantMinMatchLength1 = (x, y) -> rkrGstNormalizedShingleDiceVariant(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3DiceVariantMinMatchLength1 = (x, y) -> rkrGstNormalizedShingleDiceVariant(x,y,3, 1, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenOverlapMinMatchLength1 = (x, y) -> rkrGstTokenOverlap(x,y, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramOverlapMinMatchLength1 = (x, y) -> rkrGstNGramOverlap(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramOverlapMinMatchLength1 = (x, y) -> rkrGstNGramOverlap(x,y,3, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramOverlapMinMatchLength1 = (x, y) -> rkrGstNGramOverlap(x,y,4, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramOverlapMinMatchLength1 = (x, y) -> rkrGstNGramOverlap(x,y,5, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2OverlapMinMatchLength1 = (x, y) -> rkrGstShingleOverlap(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3OverlapMinMatchLength1 = (x, y) -> rkrGstShingleOverlap(x,y,3, 1, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenOverlapMinMatchLength1 = (x, y) -> rkrGstNormalizedTokenOverlap(x,y, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramOverlapMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramOverlapMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,3, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramOverlapMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,4, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramOverlapMinMatchLength1 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,5, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2OverlapMinMatchLength1 = (x, y) -> rkrGstNormalizedShingleOverlap(x,y,2, 1, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3OverlapMinMatchLength1 = (x, y) -> rkrGstNormalizedShingleOverlap(x,y,3, 1, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstTokenJaccardMinMatchLength2 = (x, y) -> rkrGstTokenJaccard(x,y, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramJaccardMinMatchLength2 = (x, y) -> rkrGstNGramJaccard(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramJaccardMinMatchLength2 = (x, y) -> rkrGstNGramJaccard(x,y,3, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramJaccardMinMatchLength2 = (x, y) -> rkrGstNGramJaccard(x,y,4, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramJaccardMinMatchLength2 = (x, y) -> rkrGstNGramJaccard(x,y,5, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2JaccardMinMatchLength2 = (x, y) -> rkrGstShingleJaccard(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3JaccardMinMatchLength2 = (x, y) -> rkrGstShingleJaccard(x,y,3, 2, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenJaccardMinMatchLength2 = (x, y) -> rkrGstNormalizedTokenJaccard(x,y,2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramJaccardMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramJaccardMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,3, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramJaccardMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,4, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramJaccardMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,5, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2JaccardMinMatchLength2 = (x, y) -> rkrGstNormalizedShingleJaccard(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3JaccardMinMatchLength2 = (x, y) -> rkrGstNormalizedShingleJaccard(x,y,3, 2, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenDiceMinMatchLength2 = (x, y) -> rkrGstTokenDice(x,y, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramDiceMinMatchLength2 = (x, y) -> rkrGstNGramDice(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramDiceMinMatchLength2 = (x, y) -> rkrGstNGramDice(x,y,3, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramDiceMinMatchLength2 = (x, y) -> rkrGstNGramDice(x,y,4, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramDiceMinMatchLength2 = (x, y) -> rkrGstNGramDice(x,y,5, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2DiceMinMatchLength2 = (x, y) -> rkrGstShingleDice(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3DiceMinMatchLength2 = (x, y) -> rkrGstShingleDice(x,y,3, 2, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenDiceMinMatchLength2 = (x, y) -> rkrGstNormalizedTokenDice(x,y, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramDiceMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramDice(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramDiceMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramDice(x,y,3, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramDiceMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramDice(x,y,4, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramDiceMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramDice(x,y,5, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2DiceMinMatchLength2 = (x, y) -> rkrGstNormalizedShingleDice(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3DiceMinMatchLength2 = (x, y) -> rkrGstNormalizedShingleDice(x,y,3, 2, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenDiceVariantMinMatchLength2 = (x, y) -> rkrGstTokenDiceVariant(x,y, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramDiceVariantMinMatchLength2 = (x, y) -> rkrGstNGramDiceVariant(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramDiceVariantMinMatchLength2 = (x, y) -> rkrGstNGramDiceVariant(x,y,3, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramDiceVariantMinMatchLength2 = (x, y) -> rkrGstNGramDiceVariant(x,y,4, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramDiceVariantMinMatchLength2 = (x, y) -> rkrGstNGramDiceVariant(x,y,5, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2DiceVariantMinMatchLength2 = (x, y) -> rkrGstShingleDiceVariant(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3DiceVariantMinMatchLength2 = (x, y) -> rkrGstShingleDiceVariant(x,y,3, 2, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenDiceVariantMinMatchLength2 = (x, y) -> rkrGstNormalizedTokenDiceVariant(x,y, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramDiceVariantMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramDiceVariantMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,3, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramDiceVariantMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,4, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramDiceVariantMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,5, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2DiceVariantMinMatchLength2 = (x, y) -> rkrGstNormalizedShingleDiceVariant(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3DiceVariantMinMatchLength2 = (x, y) -> rkrGstNormalizedShingleDiceVariant(x,y,3, 2, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenOverlapMinMatchLength2 = (x, y) -> rkrGstTokenOverlap(x,y, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramOverlapMinMatchLength2 = (x, y) -> rkrGstNGramOverlap(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramOverlapMinMatchLength2 = (x, y) -> rkrGstNGramOverlap(x,y,3, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramOverlapMinMatchLength2 = (x, y) -> rkrGstNGramOverlap(x,y,4, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramOverlapMinMatchLength2 = (x, y) -> rkrGstNGramOverlap(x,y,5, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2OverlapMinMatchLength2 = (x, y) -> rkrGstShingleOverlap(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3OverlapMinMatchLength2 = (x, y) -> rkrGstShingleOverlap(x,y,3, 2, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenOverlapMinMatchLength2 = (x, y) -> rkrGstNormalizedTokenOverlap(x,y, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramOverlapMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramOverlapMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,3, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramOverlapMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,4, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramOverlapMinMatchLength2 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,5, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2OverlapMinMatchLength2 = (x, y) -> rkrGstNormalizedShingleOverlap(x,y,2, 2, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3OverlapMinMatchLength2 = (x, y) -> rkrGstNormalizedShingleOverlap(x,y,3, 2, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenJaccardMinMatchLength3 = (x, y) -> rkrGstTokenJaccard(x,y, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramJaccardMinMatchLength3 = (x, y) -> rkrGstNGramJaccard(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramJaccardMinMatchLength3 = (x, y) -> rkrGstNGramJaccard(x,y,3, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramJaccardMinMatchLength3 = (x, y) -> rkrGstNGramJaccard(x,y,4, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramJaccardMinMatchLength3 = (x, y) -> rkrGstNGramJaccard(x,y,5, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2JaccardMinMatchLength3 = (x, y) -> rkrGstShingleJaccard(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3JaccardMinMatchLength3 = (x, y) -> rkrGstShingleJaccard(x,y,3, 3, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenJaccardMinMatchLength3 = (x, y) -> rkrGstNormalizedTokenJaccard(x,y,3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramJaccardMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramJaccardMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,3, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramJaccardMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,4, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramJaccardMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramJaccard(x,y,5, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2JaccardMinMatchLength3 = (x, y) -> rkrGstNormalizedShingleJaccard(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3JaccardMinMatchLength3 = (x, y) -> rkrGstNormalizedShingleJaccard(x,y,3, 3, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenDiceMinMatchLength3 = (x, y) -> rkrGstTokenDice(x,y, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramDiceMinMatchLength3 = (x, y) -> rkrGstNGramDice(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramDiceMinMatchLength3 = (x, y) -> rkrGstNGramDice(x,y,3, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramDiceMinMatchLength3 = (x, y) -> rkrGstNGramDice(x,y,4, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramDiceMinMatchLength3 = (x, y) -> rkrGstNGramDice(x,y,5, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2DiceMinMatchLength3 = (x, y) -> rkrGstShingleDice(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3DiceMinMatchLength3 = (x, y) -> rkrGstShingleDice(x,y,3, 3, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenDiceMinMatchLength3 = (x, y) -> rkrGstNormalizedTokenDice(x,y, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramDiceMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramDice(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramDiceMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramDice(x,y,3, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramDiceMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramDice(x,y,4, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramDiceMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramDice(x,y,5, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2DiceMinMatchLength3 = (x, y) -> rkrGstNormalizedShingleDice(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3DiceMinMatchLength3 = (x, y) -> rkrGstNormalizedShingleDice(x,y,3, 3, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenDiceVariantMinMatchLength3 = (x, y) -> rkrGstTokenDiceVariant(x,y, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramDiceVariantMinMatchLength3 = (x, y) -> rkrGstNGramDiceVariant(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramDiceVariantMinMatchLength3 = (x, y) -> rkrGstNGramDiceVariant(x,y,3, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramDiceVariantMinMatchLength3 = (x, y) -> rkrGstNGramDiceVariant(x,y,4, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramDiceVariantMinMatchLength3 = (x, y) -> rkrGstNGramDiceVariant(x,y,5, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2DiceVariantMinMatchLength3 = (x, y) -> rkrGstShingleDiceVariant(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3DiceVariantMinMatchLength3 = (x, y) -> rkrGstShingleDiceVariant(x,y,3, 3, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenDiceVariantMinMatchLength3 = (x, y) -> rkrGstNormalizedTokenDiceVariant(x,y, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramDiceVariantMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramDiceVariantMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,3, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramDiceVariantMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,4, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramDiceVariantMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramDiceVariant(x,y,5, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2DiceVariantMinMatchLength3 = (x, y) -> rkrGstNormalizedShingleDiceVariant(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3DiceVariantMinMatchLength3 = (x, y) -> rkrGstNormalizedShingleDiceVariant(x,y,3, 3, INITIAL_SEARCH_SIZE);
//
//
//    static BiFunction<String, String, Double> rkrGstTokenOverlapMinMatchLength3 = (x, y) -> rkrGstTokenOverlap(x,y, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst2GramOverlapMinMatchLength3 = (x, y) -> rkrGstNGramOverlap(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst3GramOverlapMinMatchLength3 = (x, y) -> rkrGstNGramOverlap(x,y,3, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst4GramOverlapMinMatchLength3 = (x, y) -> rkrGstNGramOverlap(x,y,4, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGst5GramOverlapMinMatchLength3 = (x, y) -> rkrGstNGramOverlap(x,y,5, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle2OverlapMinMatchLength3 = (x, y) -> rkrGstShingleOverlap(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstShingle3OverlapMinMatchLength3 = (x, y) -> rkrGstShingleOverlap(x,y,3, 3, INITIAL_SEARCH_SIZE);
//
//    static BiFunction<String, String, Double> rkrGstNormalizedTokenOverlapMinMatchLength3 = (x, y) -> rkrGstNormalizedTokenOverlap(x,y, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized2GramOverlapMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized3GramOverlapMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,3, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized4GramOverlapMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,4, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalized5GramOverlapMinMatchLength3 = (x, y) -> rkrGstNormalizedNGramOverlap(x,y,5, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle2OverlapMinMatchLength3 = (x, y) -> rkrGstNormalizedShingleOverlap(x,y,2, 3, INITIAL_SEARCH_SIZE);
//    static BiFunction<String, String, Double> rkrGstNormalizedShingle3OverlapMinMatchLength3 = (x, y) -> rkrGstNormalizedShingleOverlap(x,y,3, 3, INITIAL_SEARCH_SIZE);


    // ****** Profile based
    static BiFunction<String, String, Double> cosineNormalizedTokensBool = (x, y) -> tokensProfileCosineNormalized(x,y, Base.WeightingScheme.bool);
    static BiFunction<String, String, Double> cosineNormalizedTokensTermFrequency = (x, y) -> tokensProfileCosineNormalized(x,y, Base.WeightingScheme.termFrequency);
    static BiFunction<String, String, Double> cosineNormalizedTokensNormalizedTermFrequency = (x, y) -> tokensProfileCosineNormalized(x,y, Base.WeightingScheme.normalizedTermFrequency);
    static BiFunction<String, String, Double> cosineNormalized2GramsBool = (x, y) -> nGramProfileCosineNormalized(x,y,2,Base.WeightingScheme.bool);
    static BiFunction<String, String, Double> cosineNormalized3GramsBool = (x, y) -> nGramProfileCosineNormalized(x,y,3,Base.WeightingScheme.bool);
    static BiFunction<String, String, Double> cosineNormalized4GramsBool = (x, y) -> nGramProfileCosineNormalized(x,y,4,Base.WeightingScheme.bool);
    static BiFunction<String, String, Double> cosineNormalized5GramsBool = (x, y) -> nGramProfileCosineNormalized(x,y,5,Base.WeightingScheme.bool);
    static BiFunction<String, String, Double> cosineNormalized2GramsTermFrequency = (x, y) -> nGramProfileCosineNormalized(x,y,2, Base.WeightingScheme.termFrequency);
    static BiFunction<String, String, Double> cosineNormalized3GramsTermFrequency = (x, y) -> nGramProfileCosineNormalized(x,y,3, Base.WeightingScheme.termFrequency);
    static BiFunction<String, String, Double> cosineNormalized4GramsTermFrequency = (x, y) -> nGramProfileCosineNormalized(x,y,4, Base.WeightingScheme.termFrequency);
    static BiFunction<String, String, Double> cosineNormalized5GramsTermFrequency = (x, y) -> nGramProfileCosineNormalized(x,y,5, Base.WeightingScheme.termFrequency);
    static BiFunction<String, String, Double> cosineNormalized2GramsNormalizedTermFrequency = (x, y) -> nGramProfileCosineNormalized(x,y,2, Base.WeightingScheme.normalizedTermFrequency);
    static BiFunction<String, String, Double> cosineNormalized3GramsNormalizedTermFrequency = (x, y) -> nGramProfileCosineNormalized(x,y,3, Base.WeightingScheme.normalizedTermFrequency);
    static BiFunction<String, String, Double> cosineNormalized4GramsNormalizedTermFrequency = (x, y) -> nGramProfileCosineNormalized(x,y,4, Base.WeightingScheme.normalizedTermFrequency);
    static BiFunction<String, String, Double> cosineNormalized5GramsNormalizedTermFrequency = (x, y) -> nGramProfileCosineNormalized(x,y,5, Base.WeightingScheme.normalizedTermFrequency);
    static BiFunction<String, String, Double> cosineNormalizedShingle2Bool = (x, y) -> shingleProfileCosineNormalized(x,y, 2, Base.WeightingScheme.bool);
    static BiFunction<String, String, Double> cosineNormalizedShingle3Bool = (x, y) -> shingleProfileCosineNormalized(x,y, 3, Base.WeightingScheme.bool);
    static BiFunction<String, String, Double> cosineNormalizedShingle2TermFrequency = (x, y) -> shingleProfileCosineNormalized(x,y, 2, Base.WeightingScheme.termFrequency);
    static BiFunction<String, String, Double> cosineNormalizedShingle3TermFrequency = (x, y) -> shingleProfileCosineNormalized(x,y, 3, Base.WeightingScheme.termFrequency);
    static BiFunction<String, String, Double> cosineNormalizedShingle2NormalizedTermFrequency = (x, y) -> shingleProfileCosineNormalized(x,y, 2, Base.WeightingScheme.normalizedTermFrequency);
    static BiFunction<String, String, Double> cosineNormalizedShingle3NormalizedTermFrequency = (x, y) -> shingleProfileCosineNormalized(x,y, 3, Base.WeightingScheme.normalizedTermFrequency);

    static BiFunction<String, String, Double> manhattanNormalizedTokens = (x, y) -> tokenManhattanNormalized(x,y);
    static BiFunction<String, String, Double> manhattanNormalized2Grams = (x, y) -> nGramManhattanNormalized(x,y,2);
    static BiFunction<String, String, Double> manhattanNormalized3Grams = (x, y) -> nGramManhattanNormalized(x,y,3);
    static BiFunction<String, String, Double> manhattanNormalized4Grams = (x, y) -> nGramManhattanNormalized(x,y,4);
    static BiFunction<String, String, Double> manhattanNormalized5Grams = (x, y) -> nGramManhattanNormalized(x,y,5);
    static BiFunction<String, String, Double> manhattanNormalizedShingles2 = (x, y) -> shingleManhattanNormalized(x,y,2);
    static BiFunction<String, String, Double> manhattanNormalizedShingles3 = (x, y) -> shingleManhattanNormalized(x,y,3);

    // ****** Set based
    static BiFunction<String, String, Double> jaccardTokens = (x, y) -> tokenJaccard(x,y);
    static BiFunction<String, String, Double> jaccardNormalizedTokens = (x, y) -> tokenJaccardNormalized(x,y);

    static BiFunction<String, String, Double> jaccard2Grams = (x, y) -> nGramJaccard(x,y,2);
    static BiFunction<String, String, Double> jaccard3Grams = (x, y) -> nGramJaccard(x,y,3);
    static BiFunction<String, String, Double> jaccard4Grams = (x, y) -> nGramJaccard(x,y,4);
    static BiFunction<String, String, Double> jaccard5Grams = (x, y) -> nGramJaccard(x,y,5);

    static BiFunction<String, String, Double> jaccardNormalized2Grams = (x, y) -> nGramJaccardNormalized(x,y,2);
    static BiFunction<String, String, Double> jaccardNormalized3Grams = (x, y) -> nGramJaccardNormalized(x,y,3);
    static BiFunction<String, String, Double> jaccardNormalized4Grams = (x, y) -> nGramJaccardNormalized(x,y,4);
    static BiFunction<String, String, Double> jaccardNormalized5Grams = (x, y) -> nGramJaccardNormalized(x,y,5);

    static BiFunction<String, String, Double> jaccardNormalizedPadding2grams = (x, y) -> nGramJaccardNormalizedPadding(x,y,2);
    static BiFunction<String, String, Double> jaccardNormalizedPadding3grams = (x, y) -> nGramJaccardNormalizedPadding(x,y,3);
    static BiFunction<String, String, Double> jaccardNormalizedPadding4grams = (x, y) -> nGramJaccardNormalizedPadding(x,y,4);
    static BiFunction<String, String, Double> jaccardNormalizedPadding5grams = (x, y) -> nGramJaccardNormalizedPadding(x,y,5);

    static BiFunction<String, String, Double> jaccardShingles2 = (x, y) -> shingleJaccard(x,y,2);
    static BiFunction<String, String, Double> jaccardShingles3 = (x, y) -> shingleJaccard(x,y,3);

    static BiFunction<String, String, Double> jaccardNormalizedShingles2 = (x, y) -> shingleJaccardNormalized(x,y,2);
    static BiFunction<String, String, Double> jaccardNormalizedShingles3 = (x, y) -> shingleJaccardNormalized(x,y,3);

    static BiFunction<String, String, Double> diceTokens = (x, y) -> tokenDice(x,y);
    static BiFunction<String, String, Double> diceNormalizedTokens = (x, y) -> tokenDiceNormalized(x,y);

    static BiFunction<String, String, Double> dice2Grams = (x, y) -> nGramDice(x,y,2);
    static BiFunction<String, String, Double> dice3Grams = (x, y) -> nGramDice(x,y,3);
    static BiFunction<String, String, Double> dice4Grams = (x, y) -> nGramDice(x,y,4);
    static BiFunction<String, String, Double> dice5Grams = (x, y) -> nGramDice(x,y,5);

    static BiFunction<String, String, Double> diceNormalized2Grams = (x, y) -> nGramDiceNormalized(x,y,2);
    static BiFunction<String, String, Double> diceNormalized3Grams = (x, y) -> nGramDiceNormalized(x,y,3);
    static BiFunction<String, String, Double> diceNormalized4Grams = (x, y) -> nGramDiceNormalized(x,y,4);
    static BiFunction<String, String, Double> diceNormalized5Grams = (x, y) -> nGramDiceNormalized(x,y,5);

    static BiFunction<String, String, Double> diceNormalizedPadding2grams = (x, y) -> nGramDiceNormalizedPadding(x,y,2);
    static BiFunction<String, String, Double> diceNormalizedPadding3grams = (x, y) -> nGramDiceNormalizedPadding(x,y,3);
    static BiFunction<String, String, Double> diceNormalizedPadding4grams = (x, y) -> nGramDiceNormalizedPadding(x,y,4);
    static BiFunction<String, String, Double> diceNormalizedPadding5grams = (x, y) -> nGramDiceNormalizedPadding(x,y,5);

    static BiFunction<String, String, Double> diceShingles2 = (x, y) -> shingleDice(x,y,2);
    static BiFunction<String, String, Double> diceShingles3 = (x, y) -> shingleDice(x,y,3);

    static BiFunction<String, String, Double> diceNormalizedShingles2 = (x, y) -> shingleDiceNormalized(x,y,2);
    static BiFunction<String, String, Double> diceNormalizedShingles3 = (x, y) -> shingleDiceNormalized(x,y,3);

    static BiFunction<String, String, Double> diceVariantTokens = (x, y) -> tokenDiceVariant(x,y);
    static BiFunction<String, String, Double> diceVariantNormalizedTokens = (x, y) -> tokenDiceVariantNormalized(x,y);

    static BiFunction<String, String, Double> diceVariant2Grams = (x, y) -> nGramDiceVariant(x,y,2);
    static BiFunction<String, String, Double> diceVariant3Grams = (x, y) -> nGramDiceVariant(x,y,3);
    static BiFunction<String, String, Double> diceVariant4Grams = (x, y) -> nGramDiceVariant(x,y,4);
    static BiFunction<String, String, Double> diceVariant5Grams = (x, y) -> nGramDiceVariant(x,y,5);

    static BiFunction<String, String, Double> diceVariantNormalized2Grams = (x, y) -> nGramDiceVariantNormalized(x,y,2);
    static BiFunction<String, String, Double> diceVariantNormalized3Grams = (x, y) -> nGramDiceVariantNormalized(x,y,3);
    static BiFunction<String, String, Double> diceVariantNormalized4Grams = (x, y) -> nGramDiceVariantNormalized(x,y,4);
    static BiFunction<String, String, Double> diceVariantNormalized5Grams = (x, y) -> nGramDiceVariantNormalized(x,y,5);

    static BiFunction<String, String, Double> diceVariantNormalizedPadding2grams = (x, y) -> nGramDiceVariantNormalizedPadding(x,y,2);
    static BiFunction<String, String, Double> diceVariantNormalizedPadding3grams = (x, y) -> nGramDiceVariantNormalizedPadding(x,y,3);
    static BiFunction<String, String, Double> diceVariantNormalizedPadding4grams = (x, y) -> nGramDiceVariantNormalizedPadding(x,y,4);
    static BiFunction<String, String, Double> diceVariantNormalizedPadding5grams = (x, y) -> nGramDiceVariantNormalizedPadding(x,y,5);

    static BiFunction<String, String, Double> diceVariantShingles2 = (x, y) -> shingleDiceVariant(x,y,2);
    static BiFunction<String, String, Double> diceVariantShingles3 = (x, y) -> shingleDiceVariant(x,y,3);

    static BiFunction<String, String, Double> diceVariantNormalizedShingles2 = (x, y) -> shingleDiceVariantNormalized(x,y,2);
    static BiFunction<String, String, Double> diceVariantNormalizedShingles3 = (x, y) -> shingleDiceVariantNormalized(x,y,3);

    static BiFunction<String, String, Double> overlapTokens = (x, y) -> tokenOverlap(x,y);
    static BiFunction<String, String, Double> overlapNormalizedTokens = (x, y) -> tokenOverlapNormalized(x,y);

    public static BiFunction<String, String, Double> overlap2Grams = (x, y) -> nGramOverlap(x,y,2);
    static BiFunction<String, String, Double> overlap3Grams = (x, y) -> nGramOverlap(x,y,3);
    static BiFunction<String, String, Double> overlap4Grams = (x, y) -> nGramOverlap(x,y,4);
    static BiFunction<String, String, Double> overlap5Grams = (x, y) -> nGramOverlap(x,y,5);

    static BiFunction<String, String, Double> overlapNormalized2Grams = (x, y) -> nGramOverlapNormalized(x,y,2);
    static BiFunction<String, String, Double> overlapNormalized3Grams = (x, y) -> nGramOverlapNormalized(x,y,3);
    public static BiFunction<String, String, Double> overlapNormalized4Grams = (x, y) -> nGramOverlapNormalized(x,y,4);
    static BiFunction<String, String, Double> overlapNormalized5Grams = (x, y) -> nGramOverlapNormalized(x,y,5);

    static BiFunction<String, String, Double> overlapNormalizedPadding2grams = (x, y) -> nGramOverlapNormalizedPadding(x,y,2);
    static BiFunction<String, String, Double> overlapNormalizedPadding3grams = (x, y) -> nGramOverlapNormalizedPadding(x,y,3);
    static BiFunction<String, String, Double> overlapNormalizedPadding4grams = (x, y) -> nGramOverlapNormalizedPadding(x,y,4);
    static BiFunction<String, String, Double> overlapNormalizedPadding5grams = (x, y) -> nGramOverlapNormalizedPadding(x,y,5);

    static BiFunction<String, String, Double> overlapShingles2 = (x, y) -> shingleOverlap(x,y,2);
    static BiFunction<String, String, Double> overlapShingles3 = (x, y) -> shingleOverlap(x,y,3);

    static BiFunction<String, String, Double> overlapNormalizedShingles2 = (x, y) -> shingleOverlapNormalized(x,y,2);
    static BiFunction<String, String, Double> overlapNormalizedShingles3 = (x, y) -> shingleOverlapNormalized(x,y,3);





    public MetricResult computeSimilarity_extractLifeSpans_writeInResult_text(int postVersionListID, BiFunction<String, String, Double> metric) {

        MetricResult metricResult = new MetricResult();

        metricResult.stopWatch.reset();
        metricResult.stopWatch.start();
        TextBlockVersion.similarityMetric = metric;
        postVersionsListManagement.getPostVersionListWithID(postVersionListID).processVersionHistory();
        metricResult.stopWatch.stop();
        metricResult.stopWatch.getNanoTime();

        metricResult.lifeSpansOfAllBlocks_text = BlockLifeSpan.getLifeSpansOfAllBlocks(
                postVersionsListManagement.getPostVersionListWithID(
                        postVersionListID
                ), BlockLifeSpan.Type.textblock
        );

        metricResult.numberOfSplittings_text = compareTwoListsOfBlockLifeSpans_splitting(
                groundTruthBlocks_text.get(postVersionsListManagement.getPositionOfPostWithID(postVersionListID)),
                metricResult.lifeSpansOfAllBlocks_text);

        metricResult.numberOfFalsePositives_text = compareTwoListsOfBlockLifeSpans_falsePositives(
                metricResult.lifeSpansOfAllBlocks_text,
                groundTruthBlocks_text.get(postVersionsListManagement.getPositionOfPostWithID(postVersionListID))
                );

        metricResult.updateMetricResult_text();

        metricResult.calculateAverageTime();

        return metricResult;
    }

    public MetricResult computeSimilarity_extractLifeSpans_writeInResult_code(int postVersionListID, BiFunction<String, String, Double> metric){

        MetricResult metricResult = new MetricResult();

        metricResult.stopWatch.reset();
        metricResult.stopWatch.start();
        CodeBlockVersion.similarityMetric = metric;
        postVersionsListManagement.getPostVersionListWithID(postVersionListID).processVersionHistory();
        metricResult.stopWatch.stop();
        metricResult.stopWatch.getNanoTime();

        metricResult.lifeSpansOfAllBlocks_code = BlockLifeSpan.getLifeSpansOfAllBlocks(
                postVersionsListManagement.getPostVersionListWithID(
                        postVersionListID
                ),
                BlockLifeSpan.Type.codeblock
        );

        metricResult.numberOfSplittings_code = compareTwoListsOfBlockLifeSpans_splitting(
                groundTruthBlocks_code.get(postVersionsListManagement.getPositionOfPostWithID(postVersionListID)),
                metricResult.lifeSpansOfAllBlocks_code);

        metricResult.numberOfFalsePositives_code = compareTwoListsOfBlockLifeSpans_falsePositives(
                metricResult.lifeSpansOfAllBlocks_code,
                groundTruthBlocks_code.get(postVersionsListManagement.getPositionOfPostWithID(postVersionListID))
        );

        metricResult.updateMetricResult_code();

        metricResult.calculateAverageTime();

        return metricResult;
    }


    // ****** similarities of lifeSpans
    // gets number of (different) blocks in blockLifeSpans in which elements from lifeSpan are found
    public static int getNumberOfSplittingsOfOneLifeSpan(BlockLifeSpan lifeSpan, List<BlockLifeSpan> blockLifeSpans){

        Vector<Integer> appearances = new Vector<>();

        // find blocks of appearances
        for(int i=0; i<lifeSpan.size(); i++){
            for(int j=0; j<blockLifeSpans.size(); j++){
                if(lifeSpan.getType() != blockLifeSpans.get(j).getType())
                    continue;

                for(int k=0; k<blockLifeSpans.get(j).size(); k++) {
                    if (blockLifeSpans.get(j).get(k).getVersion() == lifeSpan.get(i).getVersion() && blockLifeSpans.get(j).get(k).getLocalId() == lifeSpan.get(i).getLocalId())
                        if (!appearances.contains(j))
                            appearances.add(j);
                }
            }
        }

        // number of different blocks
        return appearances.size();
    }

    // checks how much "unnecessary" splitting were made
    public static int compareTwoListsOfBlockLifeSpans_splitting(List<BlockLifeSpan> lifeSpanListOfMetric1, List<BlockLifeSpan> lifeSpanListOfMetric2){

        int numberOfSplittingsOverall = 0;

        for (int i=0; i<lifeSpanListOfMetric1.size(); i++) {
            int numberOfSplittings = getNumberOfSplittingsOfOneLifeSpan(lifeSpanListOfMetric1.get(i), lifeSpanListOfMetric2);
            numberOfSplittingsOverall += numberOfSplittings;
        }

        return numberOfSplittingsOverall - lifeSpanListOfMetric1.size();
    }


    // counts false positives in a block
    private static int getNumberOfFalsePositives(BlockLifeSpan lifeSpan, List<BlockLifeSpan> groundTruthLifeSpans){

        // TODO ..............
        // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

        for(int i=0; i<lifeSpan.size(); i++){
            for(int j=0; j<groundTruthLifeSpans.size(); j++){
                for(int k=0; k<groundTruthLifeSpans.get(j).size(); k++){
                    if(groundTruthLifeSpans.get(j).get(k).getVersion() == lifeSpan.get(i).getVersion()
                        && groundTruthLifeSpans.get(j).get(k).getLocalId() == lifeSpan.get(i).getLocalId()){     // found life span with same element

                        // count number of differences: false positives
                        int differences = 0;
                        for(int l=0; l<groundTruthLifeSpans.get(j).size(); l++){
                            for(int m=0; m<lifeSpan.size(); m++){
                                if(lifeSpan.get(m).getVersion() == groundTruthLifeSpans.get(j).get(l).getVersion() && lifeSpan.get(m).getLocalId() == groundTruthLifeSpans.get(j).get(l).getLocalId()){
                                    differences++;
                                    break;
                                }
                            }
                        }

                        return differences;
                    }
                }
            }
        }

        return 0;
    }

    private static int compareTwoListsOfBlockLifeSpans_falsePositives(List<BlockLifeSpan> lifeSpanListOfMetric1, List<BlockLifeSpan> lifeSpanListOfMetric2){

        int additionElementsOverall = 0;

        for (int i=0; i<lifeSpanListOfMetric1.size(); i++) {
            int additionElements = getNumberOfFalsePositives(lifeSpanListOfMetric1.get(i), lifeSpanListOfMetric2);
            additionElementsOverall += additionElements;
        }

        return  additionElementsOverall;
        //return (double)1 / (1+additionElementsOverall);
    }


    public void createStatisticsFiles() throws IOException {

        MetricsComparator metricsComparator = new MetricsComparator(pathToDirectoryOfAllPostHistories, pathToDirectoryOfAllCompletedCSVs);


        MetricEnum[] metrics = MetricEnum.values().clone();

        for(int i=0; i<metrics.length; i++){
            MetricEnum tmp = metrics[i];
            int rnd = (int)(Math.random()*metrics.length);
            metrics[i] = metrics[rnd];
            metrics[rnd] = tmp;
        }

        //for test
        //MetricEnum[] metrics = new MetricEnum[1];
        //metrics[0] = MetricEnum.overlapNormalizedTokens;
        //

        PrintWriter[] printWriters = new PrintWriter[6];
        printWriters[0] = new PrintWriter(new File("./metric results/total time for all PostVersionLists measured (text)" + ".csv"));
        printWriters[1] = new PrintWriter(new File("./metric results/similarity (unnecessary splittings) (text)" + ".csv"));
        printWriters[2] = new PrintWriter(new File("./metric results/similarity (false positives) (text)" + ".csv"));

        printWriters[3] = new PrintWriter(new File("./metric results/total time for 10 PostVersionLists measured (code)" + ".csv"));
        printWriters[4] = new PrintWriter(new File("./metric results/similarity (unnecessary splittings) (code)" + ".csv"));
        printWriters[5] = new PrintWriter(new File("./metric results/similarity (false positives) (code)" + ".csv"));

        for(int i=0; i<metrics.length+1; i++) {
//        for(int i=0; i<=3; i++) {             // TODO : use this for testing

            for (int j = 0; j < postVersionsListManagement.postVersionLists.size()+1; j++) {

                if(i == 0 && j == 0){
                    printWriters[0].write("total time for all PostVersionLists measured (text)");
                    printWriters[1].write("similarity (unnecessary splittings) (text)");
                    printWriters[2].write("similarity (false positives) (text)");

                    printWriters[3].write("total time for all PostVersionLists measured (code)");
                    printWriters[4].write("similarity (unnecessary splittings) (code)");
                    printWriters[5].write("similarity (false positives) (code)");
                }else if(i == 0){
                    for (PrintWriter printWriter : printWriters) {
                        printWriter.write(postVersionsListManagement.postVersionLists.get(j-1).getFirst().getPostId().toString());
                    }
                }else if(j == 0){
                    for (PrintWriter printWriter : printWriters) {
                        printWriter.write(metrics[i - 1].toString());
                    }
                }else{
                    try {
                        // postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfAllPostHistories);
                        // TODO? : metricsComparator.init();
                        // postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfAllPostHistories);
                        MetricResult tmpMetricResult
                                = metricsComparator.computeSimilarity_extractLifeSpans_writeInResult_text(
                                postVersionsListManagement.postVersionLists.get(j-1).getFirst().getPostId(),
                                getBiFunctionMetric(metrics[i - 1]));

                        printWriters[0].write(tmpMetricResult.totalTimeMeasured_text + "");
                        printWriters[1].write(tmpMetricResult.numberOfSplittings_text + "");
                        printWriters[2].write(tmpMetricResult.numberOfFalsePositives_text + "");

                        // postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfAllPostHistories);
                        // TODO? : metricsComparator.init();
                        // postVersionsListManagement = new PostVersionsListManagement(pathToDirectoryOfAllPostHistories);
                        tmpMetricResult
                                = metricsComparator.computeSimilarity_extractLifeSpans_writeInResult_code(
                                postVersionsListManagement.postVersionLists.get(j-1).getFirst().getPostId(),
                                getBiFunctionMetric(metrics[i - 1]));
                        printWriters[3].write(tmpMetricResult.totalTimeMeasured_code + "");
                        printWriters[4].write(tmpMetricResult.numberOfSplittings_code + "");
                        printWriters[5].write(tmpMetricResult.numberOfFalsePositives_code + "");

                    }catch (Exception e){
                        printWriters[0].write("no result");
                        printWriters[1].write("no result");
                        printWriters[2].write("no result");

                        printWriters[3].write("no result");
                        printWriters[4].write("no result");
                        printWriters[5].write("no result");
                    }
                }

                if(j < postVersionsListManagement.postVersionLists.size()){
                    for (PrintWriter printWriter : printWriters) {
                        printWriter.write(", ");
                    }
                }

            }
            for (PrintWriter printWriter : printWriters) {
                printWriter.write("\n");
            }

            for (PrintWriter printWriter : printWriters) {
                printWriter.flush();
            }

            if(i>0)
                System.out.println("metric " + metrics[i-1] + " completed (" + i + " of " + metrics.length + ")");
        }

        for (PrintWriter printWriter : printWriters) {
            printWriter.close();
        }
    }
}
