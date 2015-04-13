package scr.ratingManipulation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.NotImplementedException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.eval.RelevantItemsDataSplitter;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.impl.common.RunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.eval.GenericRelevantItemsDataSplitter;
import org.apache.mahout.cf.taste.impl.eval.IRStatisticsImpl;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.common.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * <p>
 * For each user, these implementation determine the top {@code n} preferences, then evaluate the IR
 * statistics based on a {@link DataModel} that does not have these values. This number {@code n} is the
 * "at" value, as in "precision at 5". For example, this would mean precision evaluated by removing the top 5
 * preferences for a user and then finding the percentage of those 5 items included in the top 5
 * recommendations for that user.
 * </p>
 */
public final class RMRecommenderIRStatsEvaluator implements RecommenderIRStatsEvaluator {

    private static final Logger log = LoggerFactory.getLogger(RMRecommenderIRStatsEvaluator.class);

    private static final double LOG2 = Math.log(2.0);
    Map<Integer,Integer> counter;
    /**
     * Pass as "relevanceThreshold" argument to
     * {@link #evaluate(RecommenderBuilder, DataModelBuilder, DataModel, IDRescorer, int, double, double)} to
     * have it attempt to compute a reasonable threshold. Note that this will impact performance.
     */
    public static final double CHOOSE_THRESHOLD = Double.NaN;

    private final Random random;
    private final RelevantItemsDataSplitter dataSplitter;

    public RMRecommenderIRStatsEvaluator() {
        this(new GenericRelevantItemsDataSplitter());
    }

    public RMRecommenderIRStatsEvaluator(RelevantItemsDataSplitter dataSplitter) {
        Preconditions.checkNotNull(dataSplitter);
        random = RandomUtils.getRandom();
        this.dataSplitter = dataSplitter;
    }

    public RMIRStatistics evaluate(RecommenderBuilder recommenderBuilder,
                                 DataModelBuilder dataModelBuilder,
                                 DataModel dataModel,
                                 IDRescorer rescorer,
                                 int at,
                                 double relevanceThreshold,
                                 double evaluationPercentage, FastByIDMap<PreferenceArray> trainingPrefs, FastByIDMap<PreferenceArray> testPrefs) throws TasteException {

    	counter=new HashMap<>();
        Preconditions.checkArgument(recommenderBuilder != null, "recommenderBuilder is null");
        Preconditions.checkArgument(dataModel != null, "dataModel is null");
        Preconditions.checkArgument(at >= 1, "at must be at least 1");
        Preconditions.checkArgument(evaluationPercentage > 0.0 && evaluationPercentage <= 1.0,
                "Invalid evaluationPercentage: %s", evaluationPercentage);

        double precisionIntersection = 0;
        double precisionAll = 0;
        RunningAverage recall = new FullRunningAverage();
        RunningAverage fallOut = new FullRunningAverage();
        RunningAverage nDCG = new FullRunningAverage();
        int numUsersRecommendedFor = 0;
        int numUsersWithRecommendations = 0;
       
        Map<Long,Integer>aggregateDiversityMap= new HashMap<>();
        DataModel trainingDataModel = new GenericDataModel(trainingPrefs);
        DataModel testDataModel = new GenericDataModel(testPrefs);
        RMRecommender recommender = (RMRecommender) recommenderBuilder.buildRecommender(trainingDataModel);
        LongPrimitiveIterator ite = testDataModel.getUserIDs();
        while (ite.hasNext()) {

            long userID = ite.nextLong();

            
            
            
            
//            PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
//            // List some most-preferred items that would count as (most) "relevant" results
//            double theRelevanceThreshold = Double.isNaN(relevanceThreshold) ? computeThreshold(prefs) : relevanceThreshold;
//            FastIDSet relevantItemIDs = dataSplitter.getRelevantItemsIDs(userID, at, theRelevanceThreshold, dataModel);
//
//            int numRelevantItems = relevantItemIDs.size();
//            if (numRelevantItems <= 0) {
//              continue;
//            }
//
//            try {
//            	trainingDataModel.getPreferencesFromUser(userID);
//            } catch (NoSuchUserException nsee) {
//              continue; // Oops we excluded all prefs for the user -- just move on
//            }
//
//            int size = numRelevantItems + trainingDataModel.getItemIDsFromUser(userID).size();
//            if (size < 2 * at) {
//              // Really not enough prefs to meaningfully evaluate this user
//              continue;
//            }
            
            
            
            
            
            
            
            
            long start = System.currentTimeMillis();

            PreferenceArray testPreferencesFromUser = testDataModel.getPreferencesFromUser(userID);
            int intersectionSize = 0;
            List<RecommendedItem> recommendedItems = recommender.recommend(userID, at, rescorer,counter);
            for (RecommendedItem recommendedItem : recommendedItems) {

   			 	Integer recommendationCount= counter.get((int)recommendedItem.getItemID());
   			 	if(recommendationCount==null){
   			 		recommendationCount=0;
   			 	}
   			 	counter.put((int) recommendedItem.getItemID(), recommendationCount+1);
   		 
            	aggregateDiversityMap.put(recommendedItem.getItemID(), 1);
            	Iterator<Preference> iterator = testPreferencesFromUser.iterator();
            	while(iterator.hasNext()){
            		Preference next = iterator.next();
            		if(next.getItemID()==recommendedItem.getItemID()){
            			if(next.getValue()>=4.5){
            				intersectionSize++;		
            			}
            		}
            	}
            }

            int numRecommendedItems = recommendedItems.size();

            // Precision
            if (numRecommendedItems > 0) {
                precisionAll+=numRecommendedItems;
                precisionIntersection+=intersectionSize;
            }

            long end = System.currentTimeMillis();

            log.info("Evaluated with user {} in {}ms", userID, end - start);
            log.info("Precision/recall/fall-out/nDCG/reach: {} / {} / {} / {} / {}",
                    precisionIntersection/precisionAll, recall.getAverage(), fallOut.getAverage(), nDCG.getAverage(),
                    (double) numUsersWithRecommendations / (double) numUsersRecommendedFor);
        }

        return new RMIRStatisticsImpl(
        		precisionIntersection/precisionAll,
                recall.getAverage(),
                fallOut.getAverage(),
                nDCG.getAverage(),
                (double) numUsersWithRecommendations / (double) numUsersRecommendedFor,
                aggregateDiversityMap.size());
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

      private static double log2(double value) {
        return Math.log(value) / LOG2;
      }

	@Override
	public IRStatistics evaluate(RecommenderBuilder recommenderBuilder,
			DataModelBuilder dataModelBuilder, DataModel dataModel,
			IDRescorer rescorer, int at, double relevanceThreshold,
			double evaluationPercentage) throws TasteException {
		throw new NotImplementedException();
	}
}
