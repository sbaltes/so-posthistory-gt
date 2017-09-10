package de.unitrier.st.soposthistorygt.metricsComparism;

import de.unitrier.st.stringsimilarity.edit.Variants;

import java.util.function.BiFunction;


class Metric {

    // to make automated process ease
    public enum Type{
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


    static BiFunction<String, String, Double> getBiFunctionMetric(Type metric){
        switch (metric){

            case levenshteinStandard:
                return levenshtein;
            case levenshteinNormalized:
                return levenshteinNormalized;

            case damerauLevenshteinStandard:
                return damerauLevenshtein;
            case damerauLevenshteinNormalized:
                return damerauLevenshteinNormalized;

            case optimalAlignmentStandard:
                return optimalAlignment;
            case optimalAlignmentNormalized:
                return optimalAlignmentNormalized;
            case optimalAlignment2GramFingerprint:
                return twoGramFingerprintOptimalAlignment;
            case optimalAlignment3GramFingerprint:
                return threeGramFingerprintOptimalAlignment;
            case optimalAlignment4GramFingerprint:
                return fourGramFingerprintOptimalAlignment;
            case optimalAlignment5GramFingerprint:
                return fiveGramFingerprintOptimalAlignment;
            case optimalAlignmentShingle2Fingerprint:
                return twoShingleFingerprintOptimalAlignment;
            case optimalAlignmentShingle3Fingerprint:
                return threeShingleFingerprintOptimalAlignment;
            case optimalAlignment2GramFingerprintNormalized:
                return twoGramFingerprintOptimalAlignmentNormalized;
            case optimalAlignment3GramFingerprintNormalized:
                return threeGramFingerprintOptimalAlignmentNormalized;
            case optimalAlignment4GramFingerprintNormalized:
                return fourGramFingerprintOptimalAlignmentNormalized;
            case optimalAlignment5GramFingerprintNormalized:
                return fiveGramFingerprintOptimalAlignmentNormalized;
            case optimalAlignmentShingle2FingerprintNormalized:
                return twoShingleFingerprintOptimalAlignmentNormalized;
            case optimalAlignmentShingle3FingerprintNormalized:
                return threeShingleFingerprintOptimalAlignmentNormalized;

            case longestCommonSubsequenceStandard:
                return longestCommonSubsequence;

            case longestCommonSubsequenceNormalized :
                return longestCommonSubsequenceNormalized;
            case longestCommonSubsequence2GramFingerprint :
                return twoGramFingerprintLongestCommonSubsequence;
            case longestCommonSubsequence3GramFingerprint :
                return threeGramFingerprintLongestCommonSubsequence;
            case longestCommonSubsequence4GramFingerprint :
                return fourGramFingerprintLongestCommonSubsequence;
            case longestCommonSubsequence5GramFingerprint :
                return fiveGramFingerprintLongestCommonSubsequence;
            case longestCommonSubsequenceShingle2Fingerprint :
                return twoShingleFingerprintLongestCommonSubsequence;
            case longestCommonSubsequenceShingle3Fingerprint :
                return threeShingleFingerprintLongestCommonSubsequence;
            case longestCommonSubsequence2GramFingerprintNormalized :
                return twoGramFingerprintLongestCommonSubsequenceNormalized;
            case longestCommonSubsequence3GramFingerprintNormalized :
                return threeGramFingerprintLongestCommonSubsequenceNormalized;
            case longestCommonSubsequence4GramFingerprintNormalized :
                return fourGramFingerprintLongestCommonSubsequenceNormalized;
            case longestCommonSubsequence5GramFingerprintNormalized :
                return fiveGramFingerprintLongestCommonSubsequenceNormalized;
            case longestCommonSubsequenceShingle2FingerprintNormalized :
                return twoShingleFingerprintLongestCommonSubsequenceNormalized;
            case longestCommonSubsequenceShingle3FingerprintNormalized :
                return threeShingleFingerprintLongestCommonSubsequenceNormalized;

            case winnowingTokenJaccard : return winnowingTokenJaccard;
            case winnowing2GramJaccard : return winnowingTwoGramJaccard;
            case winnowing3GramJaccard : return winnowingThreeGramJaccard;
            case winnowing4GramJaccard : return winnowingFourGramJaccard;
            case winnowing5GramJaccard : return winnowingFiveGramJaccard;
            case winnowingShingle2Jaccard : return winnowingTwoShingleJaccard;
            case winnowingShingle3Jaccard : return winnowingThreeShingleJaccard;
            case winnowingNormalizedTokenJaccard : return winnowingTokenJaccardNormalized;
            case winnowingNormalized2GramJaccard : return winnowingTwoGramJaccardNormalized;
            case winnowingNormalized3GramJaccard : return winnowingThreeGramJaccardNormalized;
            case winnowingNormalized4GramJaccard : return winnowingFourGramJaccardNormalized;
            case winnowingNormalized5GramJaccard : return winnowingFiveGramJaccardNormalized;
            case winnowingNormalizedShingle2Jaccard : return winnowingTwoShingleJaccardNormalized;
            case winnowingNormalizedShingle3Jaccard : return winnowingThreeShingleJaccardNormalized;

            case winnowingTokenDice : return winnowingTokenDice;
            case winnowing2GramDice : return winnowingTwoGramDice;
            case winnowing3GramDice : return winnowingThreeGramDice;
            case winnowing4GramDice : return winnowingFourGramDice;
            case winnowing5GramDice : return winnowingFiveGramDice;
            case winnowingShingle2Dice : return winnowingTwoShingleDice;
            case winnowingShingle3Dice : return winnowingThreeShingleDice;
            case winnowingNormalizedTokenDice : return winnowingNormalizedTokenDice;
            case winnowingNormalized2GramDice : return winnowingTwoGramDiceNormalized;
            case winnowingNormalized3GramDice : return winnowingThreeGramDiceNormalized;
            case winnowingNormalized4GramDice : return winnowingFourGramDiceNormalized;
            case winnowingNormalized5GramDice : return winnowingFiveGramDiceNormalized;
            case winnowingNormalizedShingle2Dice : return winnowingTwoShingleDiceNormalized;
            case winnowingNormalizedShingle3Dice : return winnowingThreeShingleDiceNormalized;

            case winnowingTokenDiceVariant : return winnowingTokenDiceVariant;
            case winnowing2GramDiceVariant : return winnowingTwoGramDiceVariant;
            case winnowing3GramDiceVariant : return winnowingThreeGramDiceVariant;
            case winnowing4GramDiceVariant : return winnowingFourGramDiceVariant;
            case winnowing5GramDiceVariant : return winnowingFiveGramDiceVariant;
            case winnowingShingle2DiceVariant : return winnowingTwoShingleDiceVariant;
            case winnowingShingle3DiceVariant : return winnowingThreeShingleDiceVariant;
            case winnowingNormalizedTokenDiceVariant : return winnowingTokenDiceVariantNormalized;
            case winnowingNormalized2GramDiceVariant : return winnowingTwoGramDiceVariantNormalized;
            case winnowingNormalized3GramDiceVariant : return winnowingThreeGramDiceVariantNormalized;
            case winnowingNormalized4GramDiceVariant : return winnowingFourGramDiceVariantNormalized;
            case winnowingNormalized5GramDiceVariant : return winnowingFiveGramDiceVariantNormalized;
            case winnowingNormalizedShingle2DiceVariant : return winnowingTwoShingleDiceVariantNormalized;
            case winnowingNormalizedShingle3DiceVariant : return winnowingThreeShingleDiceVariantNormalized;

            case winnowingTokenOverlap : return winnowingTokenOverlap;
            case winnowing2GramOverlap : return winnowingTwoGramOverlap;
            case winnowing3GramOverlap : return winnowingThreeGramOverlap;
            case winnowing4GramOverlap : return winnowingFourGramOverlap;
            case winnowing5GramOverlap : return winnowingFiveGramOverlap;
            case winnowingShingle2Overlap : return winnowingTwoShingleOverlap;
            case winnowingShingle3Overlap : return winnowingThreeShingleOverlap;
            case winnowingNormalizedTokenOverlap : return winnowingTokenOverlapNormalized;
            case winnowingNormalized2GramOverlap : return winnowingTwoGramOverlapNormalized;
            case winnowingNormalized3GramOverlap : return winnowingThreeGramOverlapNormalized;
            case winnowingNormalized4GramOverlap : return winnowingFourGramOverlapNormalized;
            case winnowingNormalized5GramOverlap : return winnowingFiveGramOverlapNormalized;
            case winnowingNormalizedShingle2Overlap : return winnowingTwoShingleOverlapNormalized;
            case winnowingNormalizedShingle3Overlap : return winnowingThreeShingleOverlapNormalized;


            case cosineNormalizedTokensBool :
                return cosineTokenNormalizedBool;
            case cosineNormalizedTokensTermFrequency :
                return cosineTokenNormalizedTermFrequency;
            case cosineNormalizedTokensNormalizedTermFrequency :
                return cosineTokenNormalizedNormalizedTermFrequency;
            case cosineNormalized2GramsBool :
                return cosineTwoGramNormalizedBool;
            case cosineNormalized3GramsBool :
                return cosineThreeGramNormalizedBool;
            case cosineNormalized4GramsBool :
                return cosineFourGramNormalizedBool;
            case cosineNormalized5GramsBool :
                return cosineFiveGramNormalizedBool;
            case cosineNormalized2GramsTermFrequency :
                return cosineTwoGramNormalizedTermFrequency;
            case cosineNormalized3GramsTermFrequency :
                return cosineThreeGramNormalizedTermFrequency;
            case cosineNormalized4GramsTermFrequency :
                return cosineFourGramNormalizedTermFrequency;
            case cosineNormalized5GramsTermFrequency :
                return cosineFiveGramNormalizedTermFrequency;
            case cosineNormalized2GramsNormalizedTermFrequency :
                return cosineTwoGramNormalizedNormalizedTermFrequency;
            case cosineNormalized3GramsNormalizedTermFrequency :
                return cosineThreeGramNormalizedNormalizedTermFrequency;
            case cosineNormalized4GramsNormalizedTermFrequency :
                return cosineFourGramNormalizedNormalizedTermFrequency;
            case cosineNormalized5GramsNormalizedTermFrequency :
                return cosineFiveGramNormalizedNormalizedTermFrequency;
            case cosineNormalizedShingle2Bool :
                return cosineTwoShingleNormalizedBool;
            case cosineNormalizedShingle3Bool :
                return cosineThreeShingleNormalizedBool;
            case cosineNormalizedShingle2TermFrequency :
                return cosineTwoShingleNormalizedTermFrequency;
            case cosineNormalizedShingle3TermFrequency :
                return cosineThreeShingleNormalizedTermFrequency;
            case cosineNormalizedShingle2NormalizedTermFrequency :
                return cosineTwoShingleNormalizedNormalizedTermFrequency;
            case cosineNormalizedShingle3NormalizedTermFrequency :
                return cosineThreeShingleNormalizedNormalizedTermFrequency;

            case manhattanNormalizedTokens :
                return manhattanTokenNormalized;
            case manhattanNormalized2Grams :
                return manhattanTwoGramNormalized;
            case manhattanNormalized3Grams :
                return manhattanThreeGramNormalized;
            case manhattanNormalized4Grams :
                return manhattanFourGramNormalized;
            case manhattanNormalized5Grams :
                return manhattanFiveGramNormalized;
            case manhattanNormalizedShingles2 :
                return manhattanTwoShingleNormalized;
            case manhattanNormalizedShingles3 :
                return manhattanThreeShingleNormalized;

            case jaccardTokens:
                return tokenJaccard;
            case jaccardNormalizedTokens:
                return tokenJaccardNormalized;
            case jaccard2Grams:
                return twoGramJaccard;
            case jaccard3Grams:
                return threeGramJaccard;
            case jaccard4Grams:
                return fourGramJaccard;
            case jaccard5Grams:
                return fiveGramJaccard;
            case jaccardNormalized2Grams:
                return twoGramJaccardNormalized;
            case jaccardNormalized3Grams:
                return threeGramJaccardNormalized;
            case jaccardNormalized4Grams:
                return fourGramJaccardNormalized;
            case jaccardNormalized5Grams:
                return fiveGramJaccardNormalized;
            case jaccardNormalizedPadding2grams:
                return twoGramJaccardNormalizedPadding;
            case jaccardNormalizedPadding3grams:
                return threeGramJaccardNormalizedPadding;
            case jaccardNormalizedPadding4grams:
                return fourGramJaccardNormalizedPadding;
            case jaccardNormalizedPadding5grams:
                return fiveGramJaccardNormalizedPadding;
            case jaccardShingles2:
                return twoShingleJaccard;
            case jaccardShingles3:
                return threeShingleJaccard;
            case jaccardNormalizedShingles2:
                return twoShingleJaccardNormalized;
            case jaccardNormalizedShingles3:
                return threeShingleJaccardNormalized;

            case diceTokens:
                return tokenDice;
            case diceNormalizedTokens:
                return tokenDiceNormalized;
            case dice2Grams:
                return twoGramDice;
            case dice3Grams:
                return threeGramDice;
            case dice4Grams:
                return fourGramDice;
            case dice5Grams:
                return fiveGramDice;
            case diceNormalized2Grams:
                return twoGramDiceNormalized;
            case diceNormalized3Grams:
                return threeGramDiceNormalized;
            case diceNormalized4Grams:
                return fourGramDiceNormalized;
            case diceNormalized5Grams:
                return fiveGramDiceNormalized;
            case diceNormalizedPadding2grams:
                return twoGramDiceNormalizedPadding;
            case diceNormalizedPadding3grams:
                return threeGramDiceNormalizedPadding;
            case diceNormalizedPadding4grams:
                return fourGramDiceNormalizedPadding;
            case diceNormalizedPadding5grams:
                return fiveGramDiceNormalizedPadding;
            case diceShingles2:
                return twoShingleDice;
            case diceShingles3:
                return threeShingleDice;
            case diceNormalizedShingles2:
                return twoShingleDiceNormalized;
            case diceNormalizedShingles3:
                return threeShingleDiceNormalized;

            case diceVariantTokens:
                return tokenDiceVariant;
            case diceVariantNormalizedTokens:
                return tokenDiceVariantNormalized;
            case diceVariant2Grams:
                return twoGramDiceVariant;
            case diceVariant3Grams:
                return threeGramDiceVariant;
            case diceVariant4Grams:
                return fourGramDiceVariant;
            case diceVariant5Grams:
                return fiveGramDiceVariant;
            case diceVariantNormalized2Grams:
                return twoGramDiceVariantNormalized;
            case diceVariantNormalized3Grams:
                return threeGramDiceVariantNormalized;
            case diceVariantNormalized4Grams:
                return fourGramDiceVariantNormalized;
            case diceVariantNormalized5Grams:
                return fiveGramDiceVariantNormalized;
            case diceVariantNormalizedPadding2grams:
                return twoGramDiceVariantNormalizedPadding;
            case diceVariantNormalizedPadding3grams:
                return threeGramDiceVariantNormalizedPadding;
            case diceVariantNormalizedPadding4grams:
                return fourGramDiceVariantNormalizedPadding;
            case diceVariantNormalizedPadding5grams:
                return fiveGramDiceVariantNormalizedPadding;
            case diceVariantShingles2:
                return twoShingleDiceVariant;
            case diceVariantShingles3:
                return threeShingleDiceVariant;
            case diceVariantNormalizedShingles2:
                return twoShingleDiceVariantNormalized;
            case diceVariantNormalizedShingles3:
                return threeShingleDiceVariantNormalized;

            case overlapTokens:
                return tokenOverlap;
            case overlapNormalizedTokens:
                return tokenOverlapNormalized;
            case overlap2Grams:
                return twoGramOverlap;
            case overlap3Grams:
                return threeGramOverlap;
            case overlap4Grams:
                return fourGramOverlap;
            case overlap5Grams:
                return fiveGramOverlap;
            case overlapNormalized2Grams:
                return twoGramOverlapNormalized;
            case overlapNormalized3Grams:
                return threeGramOverlapNormalized;
            case overlapNormalized4Grams:
                return fourGramOverlapNormalized;
            case overlapNormalized5Grams:
                return fiveGramOverlapNormalized;
            case overlapNormalizedPadding2grams:
                return twoGramOverlapNormalizedPadding;
            case overlapNormalizedPadding3grams:
                return threeGramOverlapNormalizedPadding;
            case overlapNormalizedPadding4grams:
                return fourGramOverlapNormalizedPadding;
            case overlapNormalizedPadding5grams:
                return fiveGramOverlapNormalizedPadding;
            case overlapShingles2:
                return twoShingleOverlap;
            case overlapShingles3:
                return threeShingleOverlap;
            case overlapNormalizedShingles2:
                return twoShingleOverlapNormalized;
            case overlapNormalizedShingles3:
                return threeShingleOverlapNormalized;

            default:
                return null;
        }
    }


    // ****** Edit based *****
    private static BiFunction<String, String, Double> levenshtein = Variants::levenshtein;
    private static BiFunction<String, String, Double> levenshteinNormalized = Variants::levenshteinNormalized;

    private static BiFunction<String, String, Double> damerauLevenshtein = Variants::damerauLevenshtein;
    private static BiFunction<String, String, Double> damerauLevenshteinNormalized = Variants::damerauLevenshteinNormalized;

    private static BiFunction<String, String, Double> optimalAlignment = Variants::optimalAlignment;
    private static BiFunction<String, String, Double> optimalAlignmentNormalized = Variants::optimalAlignmentNormalized;

    private static BiFunction<String, String, Double> twoGramFingerprintOptimalAlignment = Variants::twoGramFingerprintOptimalAlignment;
    private static BiFunction<String, String, Double> threeGramFingerprintOptimalAlignment = Variants::threeGramFingerprintOptimalAlignment;
    private static BiFunction<String, String, Double> fourGramFingerprintOptimalAlignment = Variants::fourGramFingerprintOptimalAlignment;
    private static BiFunction<String, String, Double> fiveGramFingerprintOptimalAlignment = Variants::fiveGramFingerprintOptimalAlignment;
    private static BiFunction<String, String, Double> twoShingleFingerprintOptimalAlignment = Variants::twoShingleFingerprintOptimalAlignment;
    private static BiFunction<String, String, Double> threeShingleFingerprintOptimalAlignment = Variants::threeShingleFingerprintOptimalAlignment;
    private static BiFunction<String, String, Double> twoGramFingerprintOptimalAlignmentNormalized = Variants::twoGramFingerprintOptimalAlignmentNormalized;
    private static BiFunction<String, String, Double> threeGramFingerprintOptimalAlignmentNormalized = Variants::threeGramFingerprintOptimalAlignmentNormalized;
    private static BiFunction<String, String, Double> fourGramFingerprintOptimalAlignmentNormalized = Variants::fourGramFingerprintOptimalAlignmentNormalized;
    private static BiFunction<String, String, Double> fiveGramFingerprintOptimalAlignmentNormalized = Variants::fiveGramFingerprintOptimalAlignmentNormalized;
    private static BiFunction<String, String, Double> twoShingleFingerprintOptimalAlignmentNormalized = Variants::twoShingleFingerprintOptimalAlignmentNormalized;
    private static BiFunction<String, String, Double> threeShingleFingerprintOptimalAlignmentNormalized = Variants::threeShingleFingerprintOptimalAlignmentNormalized;

    private static BiFunction<String, String, Double> longestCommonSubsequence = Variants::longestCommonSubsequence;
    private static BiFunction<String, String, Double> longestCommonSubsequenceNormalized = Variants::longestCommonSubsequenceNormalized;
    private static BiFunction<String, String, Double> twoGramFingerprintLongestCommonSubsequence = Variants::twoGramFingerprintLongestCommonSubsequence;
    private static BiFunction<String, String, Double> threeGramFingerprintLongestCommonSubsequence = Variants::threeGramFingerprintLongestCommonSubsequence;
    private static BiFunction<String, String, Double> fourGramFingerprintLongestCommonSubsequence = Variants::fourGramFingerprintLongestCommonSubsequence;
    private static BiFunction<String, String, Double> fiveGramFingerprintLongestCommonSubsequence = Variants::fiveGramFingerprintLongestCommonSubsequence;
    private static BiFunction<String, String, Double> twoShingleFingerprintLongestCommonSubsequence = Variants::twoShingleFingerprintLongestCommonSubsequence;
    private static BiFunction<String, String, Double> threeShingleFingerprintLongestCommonSubsequence = Variants::threeShingleFingerprintLongestCommonSubsequence;

    private static BiFunction<String, String, Double> twoGramFingerprintLongestCommonSubsequenceNormalized = Variants::twoGramFingerprintLongestCommonSubsequenceNormalized;
    private static BiFunction<String, String, Double> threeGramFingerprintLongestCommonSubsequenceNormalized = Variants::threeGramFingerprintLongestCommonSubsequenceNormalized;
    private static BiFunction<String, String, Double> fourGramFingerprintLongestCommonSubsequenceNormalized = Variants::fourGramFingerprintLongestCommonSubsequenceNormalized;
    private static BiFunction<String, String, Double> fiveGramFingerprintLongestCommonSubsequenceNormalized = Variants::fiveGramFingerprintLongestCommonSubsequenceNormalized;
    private static BiFunction<String, String, Double> twoShingleFingerprintLongestCommonSubsequenceNormalized = Variants::twoShingleFingerprintLongestCommonSubsequenceNormalized;
    private static BiFunction<String, String, Double> threeShingleFingerprintLongestCommonSubsequenceNormalized = Variants::threeShingleFingerprintLongestCommonSubsequenceNormalized;

    // ****** Fingerprint based
    private static BiFunction<String, String, Double> winnowingTokenJaccard = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTokenJaccard;
    private static BiFunction<String, String, Double> winnowingTwoGramJaccard = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoGramJaccard;
    private static BiFunction<String, String, Double> winnowingThreeGramJaccard = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeGramJaccard;
    private static BiFunction<String, String, Double> winnowingFourGramJaccard = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFourGramJaccard;
    private static BiFunction<String, String, Double> winnowingFiveGramJaccard = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFiveGramJaccard;
    private static BiFunction<String, String, Double> winnowingTwoShingleJaccard = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoShingleJaccard;
    private static BiFunction<String, String, Double> winnowingThreeShingleJaccard = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeShingleJaccard;

    private static BiFunction<String, String, Double> winnowingTokenJaccardNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTokenJaccardNormalized;
    private static BiFunction<String, String, Double> winnowingTwoGramJaccardNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoGramJaccardNormalized;
    private static BiFunction<String, String, Double> winnowingThreeGramJaccardNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeGramJaccardNormalized;
    private static BiFunction<String, String, Double> winnowingFourGramJaccardNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFourGramJaccardNormalized;
    private static BiFunction<String, String, Double> winnowingFiveGramJaccardNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFiveGramJaccardNormalized;
    private static BiFunction<String, String, Double> winnowingTwoShingleJaccardNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoShingleJaccardNormalized;
    private static BiFunction<String, String, Double> winnowingThreeShingleJaccardNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeShingleJaccardNormalized;

    private static BiFunction<String, String, Double> winnowingTokenDice = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTokenDice;
    private static BiFunction<String, String, Double> winnowingTwoGramDice = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoGramDice;
    private static BiFunction<String, String, Double> winnowingThreeGramDice = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeGramDice;
    private static BiFunction<String, String, Double> winnowingFourGramDice = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFourGramDice;
    private static BiFunction<String, String, Double> winnowingFiveGramDice = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFiveGramDice;
    private static BiFunction<String, String, Double> winnowingTwoShingleDice = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoShingleDice;
    private static BiFunction<String, String, Double> winnowingThreeShingleDice = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeShingleDice;

    private static BiFunction<String, String, Double> winnowingNormalizedTokenDice = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTokenDiceNormalized;
    private static BiFunction<String, String, Double> winnowingTwoGramDiceNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoGramDiceNormalized;
    private static BiFunction<String, String, Double> winnowingThreeGramDiceNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeGramDiceNormalized;
    private static BiFunction<String, String, Double> winnowingFourGramDiceNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFourGramDiceNormalized;
    private static BiFunction<String, String, Double> winnowingFiveGramDiceNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFiveGramDiceNormalized;
    private static BiFunction<String, String, Double> winnowingTwoShingleDiceNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoShingleDiceNormalized;
    private static BiFunction<String, String, Double> winnowingThreeShingleDiceNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeShingleDiceNormalized;

    private static BiFunction<String, String, Double> winnowingTokenDiceVariant = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTokenDiceVariant;
    private static BiFunction<String, String, Double> winnowingTwoGramDiceVariant = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoGramDiceVariant;
    private static BiFunction<String, String, Double> winnowingThreeGramDiceVariant = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeGramDiceVariant;
    private static BiFunction<String, String, Double> winnowingFourGramDiceVariant = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFourGramDiceVariant;
    private static BiFunction<String, String, Double> winnowingFiveGramDiceVariant = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFiveGramDiceVariant;
    private static BiFunction<String, String, Double> winnowingTwoShingleDiceVariant = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoShingleDiceVariant;
    private static BiFunction<String, String, Double> winnowingThreeShingleDiceVariant = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeShingleDiceVariant;

    private static BiFunction<String, String, Double> winnowingTokenDiceVariantNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTokenDiceVariantNormalized;
    private static BiFunction<String, String, Double> winnowingTwoGramDiceVariantNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoGramDiceVariantNormalized;
    private static BiFunction<String, String, Double> winnowingThreeGramDiceVariantNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeGramDiceVariantNormalized;
    private static BiFunction<String, String, Double> winnowingFourGramDiceVariantNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFourGramDiceVariantNormalized;
    private static BiFunction<String, String, Double> winnowingFiveGramDiceVariantNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFiveGramDiceVariantNormalized;
    private static BiFunction<String, String, Double> winnowingTwoShingleDiceVariantNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoShingleDiceVariantNormalized;
    private static BiFunction<String, String, Double> winnowingThreeShingleDiceVariantNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeShingleDiceVariantNormalized;

    private static BiFunction<String, String, Double> winnowingTokenOverlap = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTokenOverlap;
    private static BiFunction<String, String, Double> winnowingTwoGramOverlap = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoGramOverlap;
    private static BiFunction<String, String, Double> winnowingThreeGramOverlap = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeGramOverlap;
    private static BiFunction<String, String, Double> winnowingFourGramOverlap = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFourGramOverlap;
    private static BiFunction<String, String, Double> winnowingFiveGramOverlap = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFiveGramOverlap;
    private static BiFunction<String, String, Double> winnowingTwoShingleOverlap = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoShingleOverlap;
    private static BiFunction<String, String, Double> winnowingThreeShingleOverlap = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeShingleOverlap;

    private static BiFunction<String, String, Double> winnowingTokenOverlapNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTokenOverlapNormalized;
    private static BiFunction<String, String, Double> winnowingTwoGramOverlapNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoGramOverlapNormalized;
    private static BiFunction<String, String, Double> winnowingThreeGramOverlapNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeGramOverlapNormalized;
    private static BiFunction<String, String, Double> winnowingFourGramOverlapNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFourGramOverlapNormalized;
    private static BiFunction<String, String, Double> winnowingFiveGramOverlapNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingFiveGramOverlapNormalized;
    private static BiFunction<String, String, Double> winnowingTwoShingleOverlapNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingTwoShingleOverlapNormalized;
    private static BiFunction<String, String, Double> winnowingThreeShingleOverlapNormalized = de.unitrier.st.stringsimilarity.fingerprint.Variants::winnowingThreeShingleOverlapNormalized;



    // ****** Profile based
    private static BiFunction<String, String, Double> cosineTokenNormalizedBool = de.unitrier.st.stringsimilarity.profile.Variants::cosineTokenNormalizedBool;
    private static BiFunction<String, String, Double> cosineTokenNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineTokenNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineTokenNormalizedNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineTokenNormalizedNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineTwoGramNormalizedBool = de.unitrier.st.stringsimilarity.profile.Variants::cosineTwoGramNormalizedBool;
    private static BiFunction<String, String, Double> cosineThreeGramNormalizedBool = de.unitrier.st.stringsimilarity.profile.Variants::cosineThreeGramNormalizedBool;
    private static BiFunction<String, String, Double> cosineFourGramNormalizedBool = de.unitrier.st.stringsimilarity.profile.Variants::cosineFourGramNormalizedBool;
    private static BiFunction<String, String, Double> cosineFiveGramNormalizedBool = de.unitrier.st.stringsimilarity.profile.Variants::cosineFiveGramNormalizedBool;
    private static BiFunction<String, String, Double> cosineTwoGramNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineTwoGramNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineThreeGramNormalizedTermFrequency =  de.unitrier.st.stringsimilarity.profile.Variants::cosineThreeGramNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineFourGramNormalizedTermFrequency =  de.unitrier.st.stringsimilarity.profile.Variants::cosineFourGramNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineFiveGramNormalizedTermFrequency =  de.unitrier.st.stringsimilarity.profile.Variants::cosineFiveGramNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineTwoGramNormalizedNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineTwoGramNormalizedNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineThreeGramNormalizedNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineThreeGramNormalizedNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineFourGramNormalizedNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineFourGramNormalizedNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineFiveGramNormalizedNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineFiveGramNormalizedNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineTwoShingleNormalizedBool = de.unitrier.st.stringsimilarity.profile.Variants::cosineTwoShingleNormalizedBool;
    private static BiFunction<String, String, Double> cosineThreeShingleNormalizedBool = de.unitrier.st.stringsimilarity.profile.Variants::cosineThreeShingleNormalizedBool;
    private static BiFunction<String, String, Double> cosineTwoShingleNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineTwoShingleNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineThreeShingleNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineThreeShingleNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineTwoShingleNormalizedNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineTwoShingleNormalizedNormalizedTermFrequency;
    private static BiFunction<String, String, Double> cosineThreeShingleNormalizedNormalizedTermFrequency = de.unitrier.st.stringsimilarity.profile.Variants::cosineThreeShingleNormalizedNormalizedTermFrequency;

    private static BiFunction<String, String, Double> manhattanTokenNormalized = de.unitrier.st.stringsimilarity.profile.Variants::manhattanTokenNormalized;
    private static BiFunction<String, String, Double> manhattanTwoGramNormalized = de.unitrier.st.stringsimilarity.profile.Variants::manhattanTwoGramNormalized;
    private static BiFunction<String, String, Double> manhattanThreeGramNormalized = de.unitrier.st.stringsimilarity.profile.Variants::manhattanThreeGramNormalized;
    private static BiFunction<String, String, Double> manhattanFourGramNormalized = de.unitrier.st.stringsimilarity.profile.Variants::manhattanFourGramNormalized;
    private static BiFunction<String, String, Double> manhattanFiveGramNormalized = de.unitrier.st.stringsimilarity.profile.Variants::manhattanFiveGramNormalized;
    private static BiFunction<String, String, Double> manhattanTwoShingleNormalized = de.unitrier.st.stringsimilarity.profile.Variants::manhattanTwoShingleNormalized;
    private static BiFunction<String, String, Double> manhattanThreeShingleNormalized = de.unitrier.st.stringsimilarity.profile.Variants::manhattanThreeShingleNormalized;

    // ****** Set based
    private static BiFunction<String, String, Double> tokenJaccard = de.unitrier.st.stringsimilarity.set.Variants::tokenJaccard;
    private static BiFunction<String, String, Double> tokenJaccardNormalized = de.unitrier.st.stringsimilarity.set.Variants::tokenJaccardNormalized;

    private static BiFunction<String, String, Double> twoGramJaccard = de.unitrier.st.stringsimilarity.set.Variants::twoGramJaccard;
    private static BiFunction<String, String, Double> threeGramJaccard = de.unitrier.st.stringsimilarity.set.Variants::threeGramJaccard;
    private static BiFunction<String, String, Double> fourGramJaccard = de.unitrier.st.stringsimilarity.set.Variants::fourGramJaccard;
    private static BiFunction<String, String, Double> fiveGramJaccard = de.unitrier.st.stringsimilarity.set.Variants::fiveGramJaccard;

    private static BiFunction<String, String, Double> twoGramJaccardNormalized = de.unitrier.st.stringsimilarity.set.Variants::twoGramJaccardNormalized;
    private static BiFunction<String, String, Double> threeGramJaccardNormalized = de.unitrier.st.stringsimilarity.set.Variants::threeGramJaccardNormalized;
    private static BiFunction<String, String, Double> fourGramJaccardNormalized = de.unitrier.st.stringsimilarity.set.Variants::fourGramJaccardNormalized;
    private static BiFunction<String, String, Double> fiveGramJaccardNormalized = de.unitrier.st.stringsimilarity.set.Variants::fiveGramJaccardNormalized;

    private static BiFunction<String, String, Double> twoGramJaccardNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::twoGramJaccardNormalizedPadding;
    private static BiFunction<String, String, Double> threeGramJaccardNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::threeGramJaccardNormalizedPadding;
    private static BiFunction<String, String, Double> fourGramJaccardNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::fourGramJaccardNormalizedPadding;
    private static BiFunction<String, String, Double> fiveGramJaccardNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::fiveGramJaccardNormalizedPadding;

    private static BiFunction<String, String, Double> twoShingleJaccard = de.unitrier.st.stringsimilarity.set.Variants::twoShingleJaccard;
    private static BiFunction<String, String, Double> threeShingleJaccard = de.unitrier.st.stringsimilarity.set.Variants::threeShingleJaccard;

    private static BiFunction<String, String, Double> twoShingleJaccardNormalized = de.unitrier.st.stringsimilarity.set.Variants::twoShingleJaccardNormalized;
    private static BiFunction<String, String, Double> threeShingleJaccardNormalized = de.unitrier.st.stringsimilarity.set.Variants::threeShingleJaccardNormalized;

    private static BiFunction<String, String, Double> tokenDice = de.unitrier.st.stringsimilarity.set.Variants::tokenDice;
    private static BiFunction<String, String, Double> tokenDiceNormalized = de.unitrier.st.stringsimilarity.set.Variants::tokenDiceNormalized;

    private static BiFunction<String, String, Double> twoGramDice = de.unitrier.st.stringsimilarity.set.Variants::twoGramDice;
    private static BiFunction<String, String, Double> threeGramDice = de.unitrier.st.stringsimilarity.set.Variants::threeGramDice;
    private static BiFunction<String, String, Double> fourGramDice = de.unitrier.st.stringsimilarity.set.Variants::fourGramDice;
    private static BiFunction<String, String, Double> fiveGramDice = de.unitrier.st.stringsimilarity.set.Variants::fiveGramDice;

    private static BiFunction<String, String, Double> twoGramDiceNormalized = de.unitrier.st.stringsimilarity.set.Variants::twoGramDiceNormalized;
    private static BiFunction<String, String, Double> threeGramDiceNormalized = de.unitrier.st.stringsimilarity.set.Variants::threeGramDiceNormalized;
    private static BiFunction<String, String, Double> fourGramDiceNormalized = de.unitrier.st.stringsimilarity.set.Variants::fourGramDiceNormalized;
    private static BiFunction<String, String, Double> fiveGramDiceNormalized = de.unitrier.st.stringsimilarity.set.Variants::fiveGramDiceNormalized;

    private static BiFunction<String, String, Double> twoGramDiceNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::twoGramDiceNormalizedPadding;
    private static BiFunction<String, String, Double> threeGramDiceNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::threeGramDiceNormalizedPadding;
    private static BiFunction<String, String, Double> fourGramDiceNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::fourGramDiceNormalizedPadding;
    private static BiFunction<String, String, Double> fiveGramDiceNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::fiveGramDiceNormalizedPadding;

    private static BiFunction<String, String, Double> twoShingleDice = de.unitrier.st.stringsimilarity.set.Variants::twoShingleDice;
    private static BiFunction<String, String, Double> threeShingleDice = de.unitrier.st.stringsimilarity.set.Variants::threeShingleDice;

    private static BiFunction<String, String, Double> twoShingleDiceNormalized = de.unitrier.st.stringsimilarity.set.Variants::twoShingleDiceNormalized;
    private static BiFunction<String, String, Double> threeShingleDiceNormalized = de.unitrier.st.stringsimilarity.set.Variants::threeShingleDiceNormalized;

    private static BiFunction<String, String, Double> tokenDiceVariant = de.unitrier.st.stringsimilarity.set.Variants::tokenDiceVariant;
    private static BiFunction<String, String, Double> tokenDiceVariantNormalized = de.unitrier.st.stringsimilarity.set.Variants::tokenDiceVariantNormalized;

    private static BiFunction<String, String, Double> twoGramDiceVariant = de.unitrier.st.stringsimilarity.set.Variants::twoGramDiceVariant;
    private static BiFunction<String, String, Double> threeGramDiceVariant = de.unitrier.st.stringsimilarity.set.Variants::threeGramDiceVariant;
    private static BiFunction<String, String, Double> fourGramDiceVariant = de.unitrier.st.stringsimilarity.set.Variants::fourGramDiceVariant;
    private static BiFunction<String, String, Double> fiveGramDiceVariant = de.unitrier.st.stringsimilarity.set.Variants::fiveGramDiceVariant;

    private static BiFunction<String, String, Double> twoGramDiceVariantNormalized = de.unitrier.st.stringsimilarity.set.Variants::twoGramDiceVariantNormalized;
    private static BiFunction<String, String, Double> threeGramDiceVariantNormalized = de.unitrier.st.stringsimilarity.set.Variants::threeGramDiceVariantNormalized;
    private static BiFunction<String, String, Double> fourGramDiceVariantNormalized = de.unitrier.st.stringsimilarity.set.Variants::fourGramDiceVariantNormalized;
    private static BiFunction<String, String, Double> fiveGramDiceVariantNormalized = de.unitrier.st.stringsimilarity.set.Variants::fiveGramDiceVariantNormalized;

    private static BiFunction<String, String, Double> twoGramDiceVariantNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::twoGramDiceVariantNormalizedPadding;
    private static BiFunction<String, String, Double> threeGramDiceVariantNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::threeGramDiceVariantNormalizedPadding;
    private static BiFunction<String, String, Double> fourGramDiceVariantNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::fourGramDiceVariantNormalizedPadding;
    private static BiFunction<String, String, Double> fiveGramDiceVariantNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::fiveGramDiceVariantNormalizedPadding;

    private static BiFunction<String, String, Double> twoShingleDiceVariant = de.unitrier.st.stringsimilarity.set.Variants::twoShingleDiceVariant;
    private static BiFunction<String, String, Double> threeShingleDiceVariant = de.unitrier.st.stringsimilarity.set.Variants::threeShingleDiceVariant;

    private static BiFunction<String, String, Double> twoShingleDiceVariantNormalized = de.unitrier.st.stringsimilarity.set.Variants::twoShingleDiceVariantNormalized;
    private static BiFunction<String, String, Double> threeShingleDiceVariantNormalized = de.unitrier.st.stringsimilarity.set.Variants::threeShingleDiceVariantNormalized;

    private static BiFunction<String, String, Double> tokenOverlap = de.unitrier.st.stringsimilarity.set.Variants::tokenOverlap;
    private static BiFunction<String, String, Double> tokenOverlapNormalized = de.unitrier.st.stringsimilarity.set.Variants::tokenOverlapNormalized;

    private static BiFunction<String, String, Double> twoGramOverlap = de.unitrier.st.stringsimilarity.set.Variants::twoGramOverlap;
    private static BiFunction<String, String, Double> threeGramOverlap = de.unitrier.st.stringsimilarity.set.Variants::threeGramOverlap;
    private static BiFunction<String, String, Double> fourGramOverlap = de.unitrier.st.stringsimilarity.set.Variants::fourGramOverlap;
    private static BiFunction<String, String, Double> fiveGramOverlap = de.unitrier.st.stringsimilarity.set.Variants::fiveGramOverlap;

    private static BiFunction<String, String, Double> twoGramOverlapNormalized = de.unitrier.st.stringsimilarity.set.Variants::twoGramOverlapNormalized;
    private static BiFunction<String, String, Double> threeGramOverlapNormalized = de.unitrier.st.stringsimilarity.set.Variants::threeGramOverlapNormalized;
    private static BiFunction<String, String, Double> fourGramOverlapNormalized = de.unitrier.st.stringsimilarity.set.Variants::fourGramOverlapNormalized;
    private static BiFunction<String, String, Double> fiveGramOverlapNormalized = de.unitrier.st.stringsimilarity.set.Variants::fiveGramOverlapNormalized;

    private static BiFunction<String, String, Double> twoGramOverlapNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::twoGramOverlapNormalizedPadding;
    private static BiFunction<String, String, Double> threeGramOverlapNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::threeGramOverlapNormalizedPadding;
    private static BiFunction<String, String, Double> fourGramOverlapNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::fourGramOverlapNormalizedPadding;
    private static BiFunction<String, String, Double> fiveGramOverlapNormalizedPadding = de.unitrier.st.stringsimilarity.set.Variants::fiveGramOverlapNormalizedPadding;

    private static BiFunction<String, String, Double> twoShingleOverlap = de.unitrier.st.stringsimilarity.set.Variants::twoShingleOverlap;
    private static BiFunction<String, String, Double> threeShingleOverlap = de.unitrier.st.stringsimilarity.set.Variants::threeShingleOverlap;

    private static BiFunction<String, String, Double> twoShingleOverlapNormalized = de.unitrier.st.stringsimilarity.set.Variants::twoShingleOverlapNormalized;
    private static BiFunction<String, String, Double> threeShingleOverlapNormalized = de.unitrier.st.stringsimilarity.set.Variants::threeShingleOverlapNormalized;

}
