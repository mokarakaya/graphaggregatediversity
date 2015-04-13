package com.tev.agg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.AllUnknownItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.svd.ParallelSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.tev.common.IRStatsEvaluatorAgg;
import com.tev.common.Statistics;

class AggDiv {

    private AggDiv() {
    }

    public static void main(String[] args) throws Exception {
        AggDiv ad = new AggDiv();
        ad.run();
    }
    void run() throws Exception {
        DataModel model = new FileDataModel(new File("c:/development/data/ml-1m/ratings.dat"));
        final AllUnknownItemsCandidateItemsStrategy candidateStrategy = new AllUnknownItemsCandidateItemsStrategy();
        IRStatsEvaluatorAgg evaluator = new IRStatsEvaluatorAgg();
//        IRStatsEvaluator eval = new IRStatsEvaluator();
        AverageAbsoluteDifferenceRecommenderEvaluator evaluatorMAE = new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel model) throws TasteException {
                UserSimilarity similarityL = new LogLikelihoodSimilarity(model);
//                UserSimilarity similarityBC = new BinaryCosineSimilarity(model);
                UserSimilarity similarityT = new TanimotoCoefficientSimilarity(model);
//                UserSimilarity similarityB = new BagSimilarity(model);
//                UserSimilarity similarityR = new RandomSimilarity(model);
                UserSimilarity similarityP = new PearsonCorrelationSimilarity(model);
                UserSimilarity similarityC = new UncenteredCosineSimilarity(model);
//                UserSimilarity similarityKL = new KLDivergenceSimilarity(model);
//                ItemSimilarity similarityIR = new RandomSimilarity(model);
//                ItemSimilarity similarityIC = new UncenteredCosineSimilarityTev(model);
                ItemSimilarity similarityIP = new PearsonCorrelationSimilarity(model);
                ItemSimilarity similarityIL = new LogLikelihoodSimilarity(model);
                ItemSimilarity similarityIT = new TanimotoCoefficientSimilarity(model);

                // UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarityT, model);
                //return new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarityP);
                //return new GenericUserBasedRecommender(model, neighborhood, similarityP);
                //return new GenericUserBasedRecommenderNormalized(model, neighborhood, similarityP);
                //return new RandomRecommender(model);
                //return new GenericItemBasedRecommender(model, similarityIC, candidateStrategy, candidateStrategy);
                //return new GenericBooleanPrefItemBasedRecommender(model,similarityIT, candidateStrategy, candidateStrategy);
                return new SVDRecommender(model, new ParallelSGDFactorizer(model, 100, 0.02, 20), candidateStrategy);
                //return new SVDRecommender(model, new SVDPlusPlusFactorizer(model, 40, 10));
                //return new SVDRecommender(model, new ALSWRFactorizer(model, 50, 0.02, 20), candidateStrategy);


            }
            /*
             public Recommender buildRecommender(DataModel model) throws TasteException {
             ItemSimilarity similarity = new LogLikelihoodSimilarity(model);
             return new GenericBooleanPrefItemBasedRecommender(model, similarity);
              
             }*/
        };
        DataModelBuilder modelBuilder = new DataModelBuilder() {
            @Override
            public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
                return new GenericBooleanPrefDataModel(
                        GenericBooleanPrefDataModel.toDataMap(trainingData));
            }
        };

        List<Double> pResults = new ArrayList<>();
        List<Double> rResults = new ArrayList<>();
        List<Double> maeResults = new ArrayList<>();
        List<Double> aDivResults = new ArrayList<>();
        List<Double> iDivResults = new ArrayList<>();
        List<Double> serendResults = new ArrayList<>();
        List<Double> noveltyResults = new ArrayList<>();
/*
        System.out.println("****************");
        PreferenceArray pa = model.getPreferencesForItem(1000);
        int size = pa.length();
        for (int i = 0; i < size; i++) {
            System.out.println(pa.getUserID(i)+","+pa.getValue(i));
        }
        System.out.println("****************");
        pa = model.getPreferencesForItem(1010);
        size = pa.length();
        for (int i = 0; i < size; i++) {
            System.out.println(pa.getUserID(i)+","+pa.getValue(i));
        }
        System.out.println("****************");
        pa = model.getPreferencesForItem(1020);
        size = pa.length();
        for (int i = 0; i < size; i++) {
            System.out.println(pa.getUserID(i)+","+pa.getValue(i));
        }
        System.out.println("****************");

        ItemSimilarity simIC = new UncenteredCosineSimilarityTev(model);
        ItemSimilarity simIT = new TanimotoCoefficientSimilarity(model);

        System.out.println("TONI1-5"+" "+simIT.itemSimilarity(1000,1010));
        System.out.println("TONI1-10"+" "+simIT.itemSimilarity(1000,1020));
        System.out.println("UNCOS1-5"+" "+simIC.itemSimilarity(1000,1010));
        System.out.println("UNCOS1-10"+" "+simIC.itemSimilarity(1000,1020));
*/
        int numTrials = 1;
        HashMap<Long, Float> rankHash;
        HashMap<Long, Float> ratingHash = new HashMap<>();
        float th = 5f;
        float alpha = 1.1f;
        rankHash = rankPop(model);
        ratingHash = rankRating(model);
        for (int iter = 0; iter < 10; iter++) {
            alpha = alpha - 0.1f;
            th = th -0.1f;
            rResults.clear();;
            pResults.clear();
            aDivResults.clear();
            iDivResults.clear();
            serendResults.clear();
            noveltyResults.clear();
            for (int trial = 0; trial < numTrials; trial++) {


//            double score = evaluatorMAE.evaluate(
//                    recommenderBuilder, null, model, 0.999, 1.0);
//            System.out.println("trial:" + trial + " MAE:" + score);
            /*    Statistics stats = eval.evaluate(
                        recommenderBuilder, model, null, 10,
                        4.5,
                        0,
                        1);
             */

                Statistics stats = evaluator.evaluate(
                        recommenderBuilder, model, null, 10000,
                        4.5,
                        0.8,
                        1,
                        rankHash,
                        ratingHash,
                        th,
                        alpha);

 /*           System.out.println("trial:" + trial + " Precision: " + stats.getPrecision()
                    + " Recall: " + stats.getRecall() + " Agg. Div: " + stats.getAggDiversity()
                    + " Ind. Div: " + stats.getIndDiversity()
                    + " Novelty: " + stats.getNovelty());
*/
                rResults.add(stats.getRecall());
                pResults.add(stats.getPrecision());
                aDivResults.add((double) stats.getAggDiversity());
                iDivResults.add((double) stats.getIndDiversity());
                serendResults.add((double) stats.getSerendipity());
                noveltyResults.add((double) stats.getNovelty());

                //      maeResults.add(score);
            }



            double sumR = 0;
            double sumP = 0;
            double sumM = 0;
            double sumAD = 0;
            double sumID = 0;
            double sumSerend = 0;
            double sumNovelty = 0;

            Iterator it = rResults.iterator();
            while (it.hasNext()) {
                sumR = sumR + (double) it.next();
            }
            it = pResults.iterator();
            while (it.hasNext()) {
                sumP = sumP + (double) it.next();
            }
            it = maeResults.iterator();
            while (it.hasNext()) {
                sumM = sumM + (double) it.next();
            }
            it = aDivResults.iterator();
            while (it.hasNext()) {
                sumAD = sumAD + (double) it.next();
            }
            it = iDivResults.iterator();
            while (it.hasNext()) {
                sumID = sumID + (double) it.next();
            }
            it = serendResults.iterator();
            while (it.hasNext()) {
                sumSerend = sumSerend + (double) it.next();
            }
            it = noveltyResults.iterator();
            while (it.hasNext()) {
                sumNovelty = sumNovelty + (double) it.next();
            }

            System.out.println(sumP / numTrials+"\t"+sumR / numTrials+"\t"+sumAD / numTrials+"\t"+sumID / numTrials);
            /*
            System.out.println("Avg. precision: " + sumP / numTrials);
            System.out.println("Avg. recall: " + sumR / numTrials);
            System.out.println("Avg. agg. div: " + sumAD / numTrials);
            System.out.println("Avg. ind. div: " + sumID / numTrials);
            System.out.println("Serendipity: " + sumSerend / numTrials);
            System.out.println("Novelty: " + sumNovelty / numTrials);
*/
            //System.out.println("Avg. MAE: " + sumM / numTrials);
        }
    }
    HashMap<Long, Float> rankPop(DataModel model) throws Exception{
        HashMap<Long, Float> h = new HashMap<>();
        LongPrimitiveIterator it = model.getItemIDs();

        while (it.hasNext()) {
            Long itemID = it.nextLong();
            h.put(itemID,(float) model.getNumUsersWithPreferenceFor(itemID));
        }

        return h;
    }

    HashMap<Long, Float> rankRating(DataModel model) throws Exception{
        HashMap<Long, Float> h = new HashMap<>();
        LongPrimitiveIterator it = model.getItemIDs();

        while (it.hasNext()) {
            Long itemID = it.nextLong();
            PreferenceArray pa = model.getPreferencesForItem(itemID);
            float avg = 0;
            for (int i = 0; i < pa.length(); i++){
                avg = avg + pa.getValue(i);
            }
            avg = avg / pa.length();
            h.put(itemID, avg);
        }

        return h;
    }
}
