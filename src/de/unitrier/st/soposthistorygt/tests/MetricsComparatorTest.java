package de.unitrier.st.soposthistorygt.tests;

public class MetricsComparatorTest {
/*
    // TODO: PostHistory.processVersionHistory() -> add option to only process text or code blocks

    final static double MAX_DELTA = 0.00001;

    @Test
    public void testGroundTruth(){

        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();
        groundTruth.setPerfectPredAndSucc();

        System.out.println(
                BlockLifeSpan.printVectorOfLifeSpans(
                        BlockLifeSpan.getLifeSpansOfAllBlocks(
                                //groundTruth.p_140861 // verified manually!
                                groundTruth.p_326440    // verified manually!
                                //groundTruth.p_1109108 // verified manually!
                                //groundTruth.p_2581754 // verified manually!
                                //groundTruth.p_3145655 // verified manually!
                                //groundTruth.p_3758880 // verified manually!
                                //groundTruth.p_5445161 // verified manually!
                                //groundTruth.p_5599842 // verified manually!
                                //groundTruth.p_9855338 // verified manually!
                                //groundTruth.p_26196831 // verified manually!
                                , textblock
                        )
                )
        );

        System.out.println(
                BlockLifeSpan.printVectorOfLifeSpans(
                        BlockLifeSpan.getLifeSpansOfAllBlocks(
                                //groundTruth.p_140861 // verified manually!
                                groundTruth.p_326440    // verified manually!
                                //groundTruth.p_1109108 // verified manually!
                                //groundTruth.p_2581754 // verified manually!
                                //groundTruth.p_3145655 // verified manually!
                                //groundTruth.p_3758880 // verified manually!
                                //groundTruth.p_5445161 // verified manually!
                                //groundTruth.p_5599842 // verified manually!
                                //groundTruth.p_9855338 // verified manually!
                                //groundTruth.p_26196831 // verified manually!
                                , codeblock
                        )
                )
        );
    }

    @Test
    public void testCompareTwoListsOfBlockLifeSpansSuccessful(){
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();
        groundTruth.setPerfectPredAndSucc();
        Vector<BlockLifeSpan> groundTruthLifeSpansSuccessful_code = BlockLifeSpan.getLifeSpansOfAllBlocks(groundTruth.p_1109108, BlockLifeSpan.Type.codeblock);

        MetricsComparator c = new MetricsComparator();
        c.init();
        CodeBlockVersion.similarityMetric = MetricsComparator.levenshteinNormalized;
        c.p_1109108.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpans_code = BlockLifeSpan.getLifeSpansOfAllBlocks(groundTruth.p_1109108, BlockLifeSpan.Type.codeblock);

        System.out.println("ground truth: " + "\n" + groundTruthLifeSpansSuccessful_code);

        System.out.println("method to be compared with: " + "\n" + metricToBeComparedLifeSpans_code);

        assertEquals(
                1,
                MetricsComparator.compareTwoListsOfBlockLifeSpans_splitting(
                        groundTruthLifeSpansSuccessful_code,
                        metricToBeComparedLifeSpans_code),
                MAX_DELTA);
    }

    @Test
    public void testCompareTwoListsOfBlockLifeSpansFail(){
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();
        groundTruth.setPerfectPredAndSucc();

        MetricsComparator c = new MetricsComparator();
        c.init();


        Vector<BlockLifeSpan> groundTruthLifeSpansFail = BlockLifeSpan.getLifeSpansOfAllBlocks(groundTruth.p_2581754, textblock);
        TextBlockVersion.similarityMetric = MetricsComparator.levenshteinNormalized;
        c.p_2581754.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpansFail = BlockLifeSpan.getLifeSpansOfAllBlocks(c.p_2581754, textblock);

        System.out.println("ground truth: " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(groundTruthLifeSpansFail)+ "\n" + groundTruthLifeSpansFail);

        System.out.println("method to be compared with: " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(metricToBeComparedLifeSpansFail)+ "\n" + metricToBeComparedLifeSpansFail);

        // shows that a block can have more than one predecessor
        for(int i=0; i<c.p_2581754.get(3).getPostBlocks().size(); i++){
            System.out.println(c.p_2581754.get(3).getPostBlocks().get(i).getPred().getLocalId() + "->" +  c.p_2581754.get(3).getPostBlocks().get(i).getLocalId());
        }


        // the following proofs that all blocks are found
        int cntBlocks = 0;
        for(int i=0; i<c.p_2581754.size(); i++){
            cntBlocks += (c.p_2581754.get(i).getTextBlocks().size() + c.p_2581754.get(i).getTextBlocks().size());
        }
        assertEquals(54, cntBlocks);
    }

    @Test
    public void testCompareTwoListsOfBlockLifeSpansDifferenceWithGroundTruth(){
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();
        groundTruth.setPerfectPredAndSucc();

        MetricsComparator c = new MetricsComparator();
        c.init();

        PostVersionList postVersionListGroundTruth = groundTruth.p_326440;
        PostVersionList postVersionListMetric = c.p_326440;


        Vector<BlockLifeSpan> groundTruthLifeSpans_text = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListGroundTruth, textblock);
        TextBlockVersion.similarityMetric = MetricsComparator.overlapNormalized4Grams;
        postVersionListMetric.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpans_text = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListMetric, textblock);

        System.out.println("ground truth (text): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(groundTruthLifeSpans_text)+ "\n" + groundTruthLifeSpans_text);
        System.out.println("method to be compared with (text): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(metricToBeComparedLifeSpans_text)+ "\n" + metricToBeComparedLifeSpans_text);


        Vector<BlockLifeSpan> groundTruthLifeSpans_code = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListGroundTruth, BlockLifeSpan.Type.codeblock);
        CodeBlockVersion.similarityMetric = MetricsComparator.overlapNormalized4Grams;
        postVersionListMetric.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpans_code = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListMetric, BlockLifeSpan.Type.codeblock);

        System.out.println("ground truth (code): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(groundTruthLifeSpans_code)+ "\n" + groundTruthLifeSpans_code);
        System.out.println("method to be compared with (code): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(metricToBeComparedLifeSpans_code)+ "\n" + metricToBeComparedLifeSpans_code);

        System.out.println("text blocks are equal: " + groundTruthLifeSpans_text.equals(metricToBeComparedLifeSpans_text));
        System.out.println("code blocks are equal: " + groundTruthLifeSpans_code.equals(metricToBeComparedLifeSpans_code));

        assert(groundTruthLifeSpans_text.equals(metricToBeComparedLifeSpans_text));
        assert(groundTruthLifeSpans_code.equals(metricToBeComparedLifeSpans_code));
    }

    @Test
    public void checkLifeSpanSizes(){

        MetricsComparator metricsComparator = new MetricsComparator();
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();
        groundTruth.setPerfectPredAndSucc();

        boolean snapshotSizesWereCorrect = true;

        for(int i = 0; i< MetricsComparator.Type.values().length; i++) {

            for (int j = 0; j < StaticPostVersionsLists.PostVersionListEnum.values().length; j++) {

                boolean snapshotSizesWereCorrectInThisRun = true;

                try {
                    metricsComparator.init();
                    MetricResult tmpMetricResult
                            = metricsComparator.computeSimilarity_extractLifeSpans_writeInResult_text(
                            StaticPostVersionsLists.PostVersionListEnum.values()[j],
                            metricsComparator.getBiFunctionMetric(MetricsComparator.Type.values()[i]));

                    if(BlockLifeSpan.getNumberOfSnapshots(tmpMetricResult.lifeSpansOfAllBlocks_text) != BlockLifeSpan.getNumberOfSnapshots(MetricsComparator.groundTruthBlocks_text.get(j))){
                        System.out.println(
                                "problem found at i=" + i + ", j=" + j + ": in post version list: " + StaticPostVersionsLists.PostVersionListEnum.values()[j] + " and metric " + MetricsComparator.Type.values()[i] + "\n"
                              + "number of snapshots in testet metric (text): " + BlockLifeSpan.getNumberOfSnapshots(tmpMetricResult.lifeSpansOfAllBlocks_text) + "\n"
                              + "number of snapshots in ground truth (text): " + BlockLifeSpan.getNumberOfSnapshots(MetricsComparator.groundTruthBlocks_text.get(j))
                              + "\n");

                        System.out.println(tmpMetricResult.lifeSpansOfAllBlocks_text);
                        System.out.println();
                        System.out.println(MetricsComparator.groundTruthBlocks_text.get(j));

                        snapshotSizesWereCorrect = false;
                    }

                    metricsComparator.init();
                    tmpMetricResult
                            = metricsComparator.computeSimilarity_extractLifeSpans_writeInResult_code(
                            StaticPostVersionsLists.PostVersionListEnum.values()[j],
                            metricsComparator.getBiFunctionMetric(MetricsComparator.Type.values()[i]));

                    if(BlockLifeSpan.getNumberOfSnapshots(tmpMetricResult.lifeSpansOfAllBlocks_code) != BlockLifeSpan.getNumberOfSnapshots(MetricsComparator.groundTruthBlocks_code.get(j))){
                        System.out.println(
                                "problem found at i=" + i + ", j=" + j + ": in post version list: " + StaticPostVersionsLists.PostVersionListEnum.values()[j] + " and metric " + MetricsComparator.Type.values()[i] + "\n"
                                        + "number of snapshots in testet metric (code): " + BlockLifeSpan.getNumberOfSnapshots(tmpMetricResult.lifeSpansOfAllBlocks_code) + "\n"
                                        + "number of snapshots in ground truth (code): " + BlockLifeSpan.getNumberOfSnapshots(MetricsComparator.groundTruthBlocks_code.get(j))
                                        + "\n");

                        System.out.println(tmpMetricResult.lifeSpansOfAllBlocks_code);
                        System.out.println();
                        System.out.println(MetricsComparator.groundTruthBlocks_code.get(j));

                        snapshotSizesWereCorrect = false;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                if(snapshotSizesWereCorrectInThisRun){
                    System.out.println(
                            "Run successful: "
                            + "snapshot sizes of ground truth and metric to be compared of post version list: "
                            + StaticPostVersionsLists.PostVersionListEnum.values()[j]
                            + " and metric " + MetricsComparator.Type.values()[i]
                            + " correspond!");
                }
            }

        }

        assertEquals(snapshotSizesWereCorrect, true);
    }

    @Test
    public void checkNumberOfSplittings(){
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();
        groundTruth.setPerfectPredAndSucc();

        MetricsComparator c = new MetricsComparator();
        c.init();

        PostVersionList postVersionListGroundTruth = groundTruth.p_326440;
        PostVersionList postVersionListMetric = c.p_326440;


        Vector<BlockLifeSpan> groundTruthLifeSpans_text = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListGroundTruth, textblock);
        TextBlockVersion.similarityMetric = MetricsComparator.levenshteinNormalized;
        postVersionListMetric.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpans_text = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListMetric, textblock);

        System.out.println("ground truth (text): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(groundTruthLifeSpans_text)+ "\n" + groundTruthLifeSpans_text);
        System.out.println("method to be compared with (text): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(metricToBeComparedLifeSpans_text)+ "\n" + metricToBeComparedLifeSpans_text);


        Vector<BlockLifeSpan> groundTruthLifeSpans_code = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListGroundTruth, BlockLifeSpan.Type.codeblock);
        CodeBlockVersion.similarityMetric = MetricsComparator.levenshteinNormalized;
        postVersionListMetric.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpans_code = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListMetric, BlockLifeSpan.Type.codeblock);

        System.out.println("ground truth (code): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(groundTruthLifeSpans_code)+ "\n" + groundTruthLifeSpans_code);
        System.out.println("method to be compared with (code): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(metricToBeComparedLifeSpans_code)+ "\n" + metricToBeComparedLifeSpans_code);


        double numberOfSplittings = MetricsComparator.getNumberOfSplittingsOfOneLifeSpan(groundTruthLifeSpans_code.lastElement(), metricToBeComparedLifeSpans_code);
        System.out.println(numberOfSplittings);
    }

    @Test
    public void checkDifferenceOf_Overlap4GramsNormilized_and_groundTruth(){
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();
        groundTruth.setPerfectPredAndSucc();

        MetricsComparator c = new MetricsComparator();
        c.init();

        PostVersionList postVersionListGroundTruth = groundTruth.p_326440;
        PostVersionList postVersionListMetric = c.p_326440;


        Vector<BlockLifeSpan> groundTruthLifeSpans_text = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListGroundTruth, textblock);
        TextBlockVersion.similarityMetric = MetricsComparator.overlapNormalized4Grams;
        postVersionListMetric.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpans_text = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListMetric, textblock);

        System.out.println("ground truth (text): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(groundTruthLifeSpans_text)+ "\n" + groundTruthLifeSpans_text);
        System.out.println("method to be compared with (text): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(metricToBeComparedLifeSpans_text)+ "\n" + metricToBeComparedLifeSpans_text);


        Vector<BlockLifeSpan> groundTruthLifeSpans_code = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListGroundTruth, BlockLifeSpan.Type.codeblock);
        CodeBlockVersion.similarityMetric = MetricsComparator.overlapNormalized4Grams;
        postVersionListMetric.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpans_code = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListMetric, BlockLifeSpan.Type.codeblock);

        System.out.println("ground truth (code): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(groundTruthLifeSpans_code)+ "\n" + groundTruthLifeSpans_code);
        System.out.println("method to be compared with (code): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(metricToBeComparedLifeSpans_code)+ "\n" + metricToBeComparedLifeSpans_code);


        System.out.println("equal text versions: " + (groundTruthLifeSpans_text.equals(metricToBeComparedLifeSpans_text)));
        System.out.println("equal code versions: " + (groundTruthLifeSpans_code.equals(metricToBeComparedLifeSpans_code)));

        assert(groundTruthLifeSpans_text.equals(metricToBeComparedLifeSpans_text));
        assert(groundTruthLifeSpans_code.equals(metricToBeComparedLifeSpans_code));

    }

    @Test
    public void checkDifferenceOf_Overlap2Grams_and_groundTruth(){
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.init();
        groundTruth.setPerfectPredAndSucc();

        MetricsComparator c = new MetricsComparator();
        c.init();

        PostVersionList postVersionListGroundTruth = groundTruth.p_326440;
        PostVersionList postVersionListMetric = c.p_326440;


        Vector<BlockLifeSpan> groundTruthLifeSpans_text = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListGroundTruth, textblock);
        TextBlockVersion.similarityMetric = MetricsComparator.overlap2Grams;
        postVersionListMetric.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpans_text = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListMetric, textblock);

        System.out.println("ground truth (text): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(groundTruthLifeSpans_text)+ "\n" + groundTruthLifeSpans_text);
        System.out.println("method to be compared with (text): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(metricToBeComparedLifeSpans_text)+ "\n" + metricToBeComparedLifeSpans_text);


        Vector<BlockLifeSpan> groundTruthLifeSpans_code = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListGroundTruth, BlockLifeSpan.Type.codeblock);
        CodeBlockVersion.similarityMetric = MetricsComparator.overlap2Grams;
        postVersionListMetric.processVersionHistory();
        Vector<BlockLifeSpan> metricToBeComparedLifeSpans_code = BlockLifeSpan.getLifeSpansOfAllBlocks(postVersionListMetric, BlockLifeSpan.Type.codeblock);

        System.out.println("ground truth (code): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(groundTruthLifeSpans_code)+ "\n" + groundTruthLifeSpans_code);
        System.out.println("method to be compared with (code): " + "\n" + "number of snapshots: " + BlockLifeSpan.getNumberOfSnapshots(metricToBeComparedLifeSpans_code)+ "\n" + metricToBeComparedLifeSpans_code);


        System.out.println("equal text versions: " + (groundTruthLifeSpans_text.equals(metricToBeComparedLifeSpans_text)));
        System.out.println("equal code versions: " + (groundTruthLifeSpans_code.equals(metricToBeComparedLifeSpans_code)));

        assert(groundTruthLifeSpans_text.equals(metricToBeComparedLifeSpans_text));
        assert(groundTruthLifeSpans_code.equals(metricToBeComparedLifeSpans_code));

    }
    */
}
