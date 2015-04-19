package com.tev.common;

import com.google.common.collect.Lists;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.RunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Divide each users' prefs into training and test. Put into test set those
 * prefs whose pref. value is larger than relevanceThreshold with a probability
 * (1-trainingPercentage)
 *
 * @author tevfik
 */
public class IRStatsEvaluatorAgg {

    DataModel model;
    private static final Logger log = Logger.getLogger(IRStatsEvaluatorAgg.class.getName());
    private Random random = new Random();
    private double recall;
    private double precision;
    private double indDiv;
    private double serend;
    private double novelty;
    private HashSet<Long> aggDiv = new HashSet<>();
    HashMap<Long, Float> rankHash = new HashMap<>();
    HashMap<Long, Integer>  recsHash = new HashMap<>();
    HashMap<Long, Float> ratingHash = new HashMap<>();
    float th;
    int total = 1;
    float alpha;

    public Statistics evaluate(
            RecommenderBuilder recommenderBuilder,
            DataModel model,
            IDRescorer rescorer,
            int at,
            double relevanceThreshold,
            double trainingPercentage,
            double evaluationPercentage,
            HashMap rankHash,
            HashMap ratingHash,
            float th,
            float alpha) throws TasteException {

    	this.model=model;
        //RandomUtils.useTestSeed();
        recsHash.clear();
        this.rankHash = rankHash;
        this.ratingHash = ratingHash;
        this.th = th;
        this.alpha = alpha;
        int numUsers = model.getNumUsers();
        aggDiv.clear();
        total = 1;
//why do we need these initializations???
        FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<>(
                1 + (int) (evaluationPercentage * numUsers));
        FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<>(
                1 + (int) (evaluationPercentage * numUsers));

        log.log(Level.INFO, "Starting to divide preferences into training and test...");

        LongPrimitiveIterator it = model.getUserIDs();
        System.out.println("*****"+model.getNumUsers());
        while (it.hasNext()) {
            long userID = it.nextLong();
            if (random.nextDouble() < evaluationPercentage) {
                splitOneUsersPrefs(trainingPercentage, trainingPrefs, testPrefs, userID, model, relevanceThreshold);
            }
        }

        DataModel trainingDataModel = new GenericDataModel(trainingPrefs);
        DataModel testDataModel = new GenericDataModel(testPrefs);

        log.log(Level.INFO, "Building model...");
        final Recommender recommender = recommenderBuilder.buildRecommender(trainingDataModel);
        log.log(Level.INFO, "Model build complete.");
        System.out.println("Number of Relevant Items: " + testPrefs.size());

        it = testDataModel.getUserIDs();
        System.out.println("Num of Test Users: " + testDataModel.getNumUsers());
        int numTestUsers = testDataModel.getNumUsers();
        int hits = 0;
        int nRelevantPrefs = 0;
        while (it.hasNext()) {

            long userID = it.nextLong();
            List<RecommendedItem> recommendedItems = recommender.recommend(userID, at, rescorer);
            //System.out.println("xxxxxxxxx"+userID);
            //////////////////////
            recommendedItems = manip(recommendedItems);
//            recommendedItems = ranking(recommendedItems, th);
            //System.out.println(recommendedItems);
            //////////////////////
            nRelevantPrefs += testPrefs.get(userID).length();
            for (RecommendedItem recommendedItem : recommendedItems) {
                total++;
                if (recsHash.containsKey(recommendedItem.getItemID())){
                    int v = recsHash.get(recommendedItem.getItemID());
                    recsHash.put(recommendedItem.getItemID(),v+1);
                }
                else {
                    recsHash.put(recommendedItem.getItemID(),1);
                }
                for (int i = 0; i < testPrefs.get(userID).length(); i++) {
                    if (testPrefs.get(userID).getItemID(i) == recommendedItem.getItemID()) {
                        hits++;
                    }
                }
                aggDiv.add(recommendedItem.getItemID());

            }
       //     System.out.println(aggDiv.size());
        //    System.out.println("wwwwwwwwwwwww");
        //    System.out.println(hits);
        //    System.out.println("wwwwwwwwwwwww");
//            serend += calcSerendipity(userID, recommendedItems, trainingDataModel);
//            novelty += calcNovelty(recommendedItems, trainingDataModel);
            indDiv += calcIndDiv(recommendedItems, trainingDataModel);
            //System.out.println("Avg. Pre:" + precision / numTestUsers);
        }
        recall = (double) hits / nRelevantPrefs;
        precision = recall / at;
        serend = serend / numTestUsers;
        indDiv = indDiv / numTestUsers;
        novelty = novelty / numTestUsers;

        return new Statistics() {
            @Override
            public double getPrecision() {
                return precision;
            }

            @Override
            public double getRecall() {
                return recall;
            }

            @Override
            public long getAggDiversity() {
                return aggDiv.size();
            }

            @Override
            public double getIndDiversity() {
                return indDiv;
            }

            @Override
            public double getSerendipity() {
                return serend;
            }

            @Override
            public double getNovelty() {
                return novelty;
            }
        };

    }

    List<RecommendedItem> manip(List<RecommendedItem> items){
    	ManipComparator mc = new ManipComparator();
        PriorityQueue<RecommendedItem> pq = new PriorityQueue<RecommendedItem>(10,mc);
        int i;
        for (i=0; i< items.size(); i++){
            pq.add(items.get(i));
        }
        List<RecommendedItem> newList = new ArrayList<RecommendedItem>();
        while (!pq.isEmpty()){
            newList.add(pq.poll());
            if (newList.size() == 20)
                break;
        }
        return newList;
    }
    List<RecommendedItem> ranking(List<RecommendedItem> items, float th){
        //RankingComparator ic = new RankingComparator();
        RatingComparator ic = new RatingComparator();
        PriorityQueue<RecommendedItem> pq = new PriorityQueue<>(10,ic);
        int i;
        for (i=0; i< items.size(); i++){
            if (items.get(i).getValue() > th){
                pq.add(items.get(i));
            }
        }
        List<RecommendedItem> newList = new ArrayList<RecommendedItem>();
        while (!pq.isEmpty()){
            newList.add(pq.poll());
            if (newList.size() == 20)
                break;
        }

        for (i=0; i< items.size(); i++){
            if (items.get(i).getValue() <= th) {
                newList.add(items.get(i));
            }
        }

        return newList.subList(0,19);
    }

    public class RankingComparator implements Comparator<RecommendedItem>
    {
        @Override
        public int compare(RecommendedItem x, RecommendedItem y)
        {
            if (rankHash.get(x.getItemID()) < rankHash.get(y.getItemID()))
            {
                return -1;
            }
            if (rankHash.get(x.getItemID()) > rankHash.get(y.getItemID()))
            {
                return 1;
            }
            return 0;
        }
    }

    public class RatingComparator implements Comparator<RecommendedItem>
    {
        @Override
        public int compare(RecommendedItem x, RecommendedItem y)
        {
            if (ratingHash.get(x.getItemID()) < ratingHash.get(y.getItemID()))
            {
                return -1;
            }
            if (ratingHash.get(x.getItemID()) > ratingHash.get(y.getItemID()))
            {
                return 1;
            }
            return 0;
        }
    }




    public class ManipComparator implements Comparator<RecommendedItem>
    {
        @Override
        public int compare(RecommendedItem x, RecommendedItem y)
        {
        	int userCount=0;
        	try {
				userCount=model.getNumUsers();
			} catch (TasteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            int numX, numY;
            if (recsHash.containsKey(x.getItemID()))
                numX = recsHash.get(x.getItemID());
            else numX = 0;
            if (recsHash.containsKey(y.getItemID()))
                numY = recsHash.get(y.getItemID());
            else numY = 0;
            float ratio_x = (float) numX/total;
            float ratio_y = (float) numY/total;
            float newXR = (float) (x.getValue()*(1-Math.pow(ratio_x, alpha)));
            float newYR = (float) (y.getValue()*(1-Math.pow(ratio_y, alpha)));
            if (newXR > newYR)
            {
                return -1;
            }
            if (newXR < newYR)
            {
                return 1;
            }
            return 0;
        }
    }

    /*
     * 
     */
    double calcNovelty(List<RecommendedItem> recs, DataModel model) throws TasteException {

        double novelty = 0;
        int size = recs.size();
        int nanCount = 0;
        long userCount = model.getNumUsers();
        for (RecommendedItem rec : recs) {
            double num = (double) userCount / model.getNumUsersWithPreferenceFor(rec.getItemID());
            double temp = Math.log(num) / Math.log(2);
            if (Double.isNaN(temp)) {
                nanCount++;
            } else {
                novelty += temp;
            }
        }
        return novelty / (size - nanCount);
    }

    double calcIndDiv(List<RecommendedItem> recs, DataModel model) throws TasteException {
//        ItemSimilarity sim = new UncenteredCosineSimilarityTev(model);
//        double indDiv = 0;
//        int size = recs.size();
//        int nanCount = 0;
//        // this is inefficient since upper triangle is enough
//        for (int i = 0; i < size; i++) {
//            for (int j = i + 1; j < size; j++) {
//                double temp = sim.itemSimilarity(recs.get(i).getItemID(), recs.get(j).getItemID());
//                if (Double.isNaN(temp)) {
//                    nanCount++;
//                } else {
//                    indDiv += temp;
//                }
//            }
//        }
//        int upper = ((size * size) - size) / 2;
//        return (1 - (indDiv / (upper - nanCount)));
    	return 0;
    }

    double calcSerendipity(long userID, List<RecommendedItem> recs, DataModel model) throws TasteException {
        ItemSimilarity sim = new UncenteredCosineSimilarity(model);
        double serend = 0;
        int size = recs.size();
        int profile = model.getItemIDsFromUser(userID).size();
        int nanCount = 0;
        LongPrimitiveIterator it = model.getItemIDsFromUser(userID).iterator();
        while (it.hasNext()) {
            long itemID = it.next();
            for (int i = 0; i < size; i++) {
                double temp = sim.itemSimilarity(itemID, recs.get(i).getItemID());
                if (Double.isNaN(temp)) {
                    nanCount++;
                } else {
                    serend += temp;
                }
            }
        }
        return (1 - (serend / ((profile * size) - nanCount)));
    }
/*
    private void splitOneUsersPrefs(double trainingPercentage,
                                    FastByIDMap<PreferenceArray> trainingPrefs,
                                    FastByIDMap<PreferenceArray> testPrefs,
                                    long userID,
                                    DataModel dataModel,
                                    double relevanceThreshold) throws TasteException {
        List<Preference> oneUserTrainingPrefs = null;
        List<Preference> oneUserTestPrefs = null;
        PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
        double theRelevanceThreshold = Double.isNaN(relevanceThreshold) ? computeThreshold(prefs) : relevanceThreshold;
        int size = prefs.length();
        int flag = 1;
        for (int i = 0; i < size; i++) {
            Preference newPref = new GenericPreference(userID, prefs.getItemID(i), prefs.getValue(i));
            if ((random.nextDouble() > trainingPercentage)
                    && (prefs.getValue(i) >= theRelevanceThreshold) && flag==1) {
                flag = 0;
                if (oneUserTestPrefs == null) {
                    oneUserTestPrefs = Lists.newArrayListWithCapacity(3);
                }
                oneUserTestPrefs.add(newPref);

            } else {
                if (oneUserTrainingPrefs == null) {
                    oneUserTrainingPrefs = Lists.newArrayListWithCapacity(3);
                }
                oneUserTrainingPrefs.add(newPref);
            }
        }
        if (oneUserTrainingPrefs != null) {
            trainingPrefs.put(userID, new GenericUserPreferenceArray(oneUserTrainingPrefs));
            if (oneUserTestPrefs != null) {
                testPrefs.put(userID, new GenericUserPreferenceArray(oneUserTestPrefs));
            }
        }
    }
    */
    private void splitOneUsersPrefs(double trainingPercentage,
                                    FastByIDMap<PreferenceArray> trainingPrefs,
                                    FastByIDMap<PreferenceArray> testPrefs,
                                    long userID,
                                    DataModel dataModel,
                                    double relevanceThreshold) throws TasteException {
        List<Preference> oneUserTrainingPrefs = null;
        List<Preference> oneUserTestPrefs = null;
        PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
        double theRelevanceThreshold = Double.isNaN(relevanceThreshold) ? computeThreshold(prefs) : relevanceThreshold;
        int size = prefs.length();
        for (int i = 0; i < size; i++) {
            Preference newPref = new GenericPreference(userID, prefs.getItemID(i), prefs.getValue(i));
            if ((random.nextDouble() > trainingPercentage)
                    && (prefs.getValue(i) >= theRelevanceThreshold)) {

                if (oneUserTestPrefs == null) {
                    oneUserTestPrefs = Lists.newArrayListWithCapacity(3);
                }
                oneUserTestPrefs.add(newPref);

            } else {
                if (oneUserTrainingPrefs == null) {
                    oneUserTrainingPrefs = Lists.newArrayListWithCapacity(3);
                }
                oneUserTrainingPrefs.add(newPref);
            }
        }
        if (oneUserTrainingPrefs != null) {
            trainingPrefs.put(userID, new GenericUserPreferenceArray(oneUserTrainingPrefs));
            if (oneUserTestPrefs != null) {
                testPrefs.put(userID, new GenericUserPreferenceArray(oneUserTestPrefs));
            }
        }
    }

    private static double computeThreshold(PreferenceArray prefs) {
        if (prefs.length() < 2) {
            // Not enough data points -- return a threshold that allows everything
            return Double.NEGATIVE_INFINITY;
        }
        RunningAverageAndStdDev stdDev = new FullRunningAverageAndStdDev();
        int size = prefs.length();
        for (int i = 0; i < size; i++) {
            stdDev.addDatum(prefs.getValue(i));
        }
        return stdDev.getAverage() + stdDev.getStandardDeviation();
    }
}
