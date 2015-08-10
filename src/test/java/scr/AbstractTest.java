package scr;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RunnableFuture;

import junit.framework.TestCase;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.common.RandomUtils;

import scr.baseRecommender.generic.BaseRecommender;
import scr.evaulator.AggregateEvaluator;
import scr.ratingManipulation.RMIRStatistics;
import scr.ratingManipulation.RMRecommenderIRStatsEvaluator;
import scr.runner.Runner;

import static junit.framework.Assert.assertTrue;


/**
 * Unit test for simple App.
 */
public abstract class AbstractTest  implements BaseRecommender,Runnable
{
	public BaseRecommender baseRecommender;
	public Map<String,Map<Double,Double>> returnMap;

	@Override
	public void run() {
		try {
			testApp();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}
	/**
     * Rigourous Test :-)
     * @throws IOException 
     * @throws TasteException 
     */
    public void testApp() throws IOException, TasteException
    {
    	DataModel dataModel= new FileDataModel(new File("C:/javafx/data/"+ Runner.DATA+".data"));
		returnMap=new HashMap<>();
    	double evaluationPercentage=0.7;
		FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<>(
	                1 + (int) (evaluationPercentage * dataModel.getNumUsers()));
	        FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<>(
	                1 + (int) (evaluationPercentage * dataModel.getNumUsers()));
	        
	    splitPrefs(evaluationPercentage, dataModel, trainingPrefs, testPrefs);
	    dataModel=new GenericDataModel(trainingPrefs);
		final Recommender recommender =getBaseRecommender(dataModel);
    	for(double i=getMinThreshold();i<=getMaxThreshold();i+=getIncThreshold()){
    		final double  threshold=i;
	    	RecommenderBuilder builder = new RecommenderBuilder() {
				@Override
				public Recommender buildRecommender(DataModel dataModel)
						throws TasteException {
			        Recommender rmRecommender= getRecommender(recommender,threshold);
					return rmRecommender;
				}
			};
	        
			RMRecommenderIRStatsEvaluator evaluator=new RMRecommenderIRStatsEvaluator();
	        RMIRStatistics evaluate = evaluator.evaluate(builder, null, dataModel, null, 20, 4.5,trainingPrefs,testPrefs);
			final Map<String, BigDecimal> aggregateMap = evaluate.getAggregateMap();

			if(returnMap.get(AggregateEvaluator.AGGREGATE)==null){
				returnMap.put(AggregateEvaluator.AGGREGATE,new HashMap<Double,Double>());
			}
			returnMap.get(AggregateEvaluator.AGGREGATE).put(evaluate.getPrecision(),evaluate.getAggregateDiversity());

			//gini
			if(returnMap.get(AggregateEvaluator.GINI)==null){
				returnMap.put(AggregateEvaluator.GINI, new HashMap<Double,Double>());
			}
			returnMap.get(AggregateEvaluator.GINI).put(evaluate.getPrecision(),aggregateMap.get(AggregateEvaluator.GINI).doubleValue());

			if(returnMap.get(AggregateEvaluator.HERF)==null){
				returnMap.put(AggregateEvaluator.HERF, new HashMap<Double,Double>());
			}
			returnMap.get(AggregateEvaluator.HERF).put(evaluate.getPrecision(),aggregateMap.get(AggregateEvaluator.HERF).doubleValue());

			if(returnMap.get(AggregateEvaluator.ENTROPY)==null){
				returnMap.put(AggregateEvaluator.ENTROPY, new HashMap<Double,Double>());
			}
			returnMap.get(AggregateEvaluator.ENTROPY).put(evaluate.getPrecision(),aggregateMap.get(AggregateEvaluator.ENTROPY).doubleValue());

			System.out.println(this.getClass().getSimpleName()+";"+i+";"+ evaluate.getPrecision() + ";" + evaluate.getAggregateDiversity() + ";" +
					aggregateMap.get(AggregateEvaluator.GINI) + ";" + aggregateMap.get(AggregateEvaluator.HERF) + ";" +
					aggregateMap.get(AggregateEvaluator.ENTROPY));
    	}
    }
	public abstract double getMinThreshold();
	public abstract double getMaxThreshold();
	public abstract double getIncThreshold();
	@Override
	public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
		return baseRecommender.getBaseRecommender(dataModel);
	}
    public abstract Recommender getRecommender(Recommender baseRecommender, double threshold) throws TasteException;
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
