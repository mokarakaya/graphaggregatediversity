package scr;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.AllUnknownItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.ParallelSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.common.RandomUtils;

import scr.ratingManipulation.AverageRatingRecommender;
import scr.ratingManipulation.RMIRStatistics;
import scr.ratingManipulation.RMRecommender;
import scr.ratingManipulation.RMRecommenderIRStatsEvaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit test for simple App.
 */
public class AverageRatingTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AverageRatingTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AverageRatingTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws IOException 
     * @throws TasteException 
     */
    public void testApp() throws IOException, TasteException
    {


    	DataModel fileDataModel= new FileDataModel(new File("c:/development/data/ml-1m/ratings.dat"));
    	double evaluationPercentage=0.7;
		FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<>(
	                1 + (int) (evaluationPercentage * fileDataModel.getNumUsers()));
	        FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<>(
	                1 + (int) (evaluationPercentage * fileDataModel.getNumUsers()));
	        
	    splitPrefs(evaluationPercentage, fileDataModel, trainingPrefs, testPrefs);
	    DataModel dataModel=new GenericDataModel(trainingPrefs);
	    final Map<Long,Float> avgRatingMap=getAverageRatingMap(dataModel);
    	for(double i=4;i<=5;i+=0.1){
    	
    		final double  threshold=i;
	    	RecommenderBuilder builder = new RecommenderBuilder() {
				@Override
				public Recommender buildRecommender(DataModel dataModel)
						throws TasteException {
			        int numFeatures=100;
			        float lambda=new Float( 0.02);
			        int numEpochs=20;
					ParallelSGDFactorizer factorizer=new ParallelSGDFactorizer(dataModel, numFeatures, lambda, numEpochs);
			        SVDRecommender recommender =new SVDRecommender(dataModel,factorizer,new AllUnknownItemsCandidateItemsStrategy());
			        AverageRatingRecommender rmRecommender= new AverageRatingRecommender(recommender,threshold,avgRatingMap);
					return rmRecommender;
				}
			};
	        
			RMRecommenderIRStatsEvaluator evaluator=new RMRecommenderIRStatsEvaluator();
	        RMIRStatistics evaluate = evaluator.evaluate(builder, null, dataModel, null, 20, 4.5, 0.8,trainingPrefs,testPrefs);
	        System.out.println(evaluate.getPrecision()+";"+evaluate.getAggregateDiversity());
    	}
        assertTrue( true );
    }
    
	private Map<Long, Float> getAverageRatingMap(DataModel dataModel) throws TasteException {
		Map<Long, Float> avgRatingMap=new HashMap<Long, Float>();
		LongPrimitiveIterator itemIDs = dataModel.getItemIDs();
		while(itemIDs.hasNext()){
			Long next = itemIDs.next();
			PreferenceArray preferencesForItem = dataModel.getPreferencesForItem(next);
			float avg=0;
			Iterator<Preference> iterator = preferencesForItem.iterator();
			while(iterator.hasNext()){
				Preference preference = iterator.next();
				avg+=preference.getValue();
			}
			avgRatingMap.put(next, avg/preferencesForItem.length());
		}
		return avgRatingMap;
	}
	
    private void splitPrefs(double evaluationPercentage, DataModel dataModel,FastByIDMap<PreferenceArray> trainingPrefs 
    		,FastByIDMap<PreferenceArray> testPrefs ) throws TasteException{
         
    	Random random = RandomUtils.getRandom();
        LongPrimitiveIterator it = dataModel.getUserIDs();
         while (it.hasNext()) {
             long userID = it.nextLong();
             PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
             Iterator<Preference> iterator = preferencesFromUser.iterator();
             List<Preference> oneUserTrainingPrefs= new ArrayList<>();
             List<Preference> oneUserTestPrefs= new ArrayList<>();
             while(iterator.hasNext()){
             	Preference next = iterator.next();
             	if (random.nextDouble() < evaluationPercentage){
             		oneUserTestPrefs.add(next);
                 }else{
                	 oneUserTrainingPrefs.add(next);
                 }            	
             }
             trainingPrefs.put(userID, new GenericUserPreferenceArray(oneUserTrainingPrefs));
             testPrefs.put(userID, new GenericUserPreferenceArray(oneUserTestPrefs));
         }
    }
}
