package scr;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

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
	public Map<String,Map<Double,Double>> returnMapInternal;
	public Map<Integer,Map<String,Map<Double,Double>>> returnMapInternalTotal;
	public String displayName;
	public int repeat;
	public String data;


	@Override
	public void run() {
		try {
			testApp(data);
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
    public void testApp(String data) throws IOException, TasteException
    {
    	DataModel dataModel= new FileDataModel(new File("C:/javafx/data/"+ data+".data"));
		returnMapInternalTotal=new HashMap<>();
    	double evaluationPercentage=0.9;
		//min value is 1
		if(repeat<1){
			repeat=1;
		}
		for(int k=0;k<repeat;k++) {
			returnMapInternal=new HashMap<>();
			FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<>(
					1 + (int) (evaluationPercentage * dataModel.getNumUsers()));
			FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<>(
					1 + (int) (evaluationPercentage * dataModel.getNumUsers()));

			splitPrefs(evaluationPercentage, dataModel, trainingPrefs, testPrefs);
			dataModel = new GenericDataModel(trainingPrefs);
			final Recommender recommender = getBaseRecommender(dataModel);
			for (double i = getMinThreshold(); i <= getMaxThreshold(); i += getIncThreshold()) {
				final double threshold = i;
				RecommenderBuilder builder = new RecommenderBuilder() {
					@Override
					public Recommender buildRecommender(DataModel dataModel)
							throws TasteException {
						Recommender rmRecommender = getRecommender(recommender, threshold);
						return rmRecommender;
					}
				};

				RMRecommenderIRStatsEvaluator evaluator = new RMRecommenderIRStatsEvaluator();
				RMIRStatistics evaluate = evaluator.evaluate(builder, null, dataModel, null, 20, 4.5, trainingPrefs, testPrefs);
				final Map<String, BigDecimal> aggregateMap = evaluate.getAggregateMap();

				if (returnMapInternal.get(AggregateEvaluator.AGGREGATE) == null) {
					returnMapInternal.put(AggregateEvaluator.AGGREGATE, new HashMap<Double, Double>());
				}
				returnMapInternal.get(AggregateEvaluator.AGGREGATE).put(evaluate.getPrecision(), evaluate.getAggregateDiversity());

				//gini
				if (returnMapInternal.get(AggregateEvaluator.GINI) == null) {
					returnMapInternal.put(AggregateEvaluator.GINI, new HashMap<Double, Double>());
				}
				returnMapInternal.get(AggregateEvaluator.GINI).put(evaluate.getPrecision(), aggregateMap.get(AggregateEvaluator.GINI).doubleValue());

				if (returnMapInternal.get(AggregateEvaluator.HERF) == null) {
					returnMapInternal.put(AggregateEvaluator.HERF, new HashMap<Double, Double>());
				}
				returnMapInternal.get(AggregateEvaluator.HERF).put(evaluate.getPrecision(), aggregateMap.get(AggregateEvaluator.HERF).doubleValue());

				if (returnMapInternal.get(AggregateEvaluator.ENTROPY) == null) {
					returnMapInternal.put(AggregateEvaluator.ENTROPY, new HashMap<Double, Double>());
				}
				returnMapInternal.get(AggregateEvaluator.ENTROPY).put(evaluate.getPrecision(), aggregateMap.get(AggregateEvaluator.ENTROPY).doubleValue());

				if (returnMapInternal.get(AggregateEvaluator.INDDIVERSITY) == null) {
					returnMapInternal.put(AggregateEvaluator.INDDIVERSITY, new HashMap<Double, Double>());
				}
				returnMapInternal.get(AggregateEvaluator.INDDIVERSITY).put(evaluate.getPrecision(), evaluate.getIndividualDiversity());

				System.out.println(this.getClass().getSimpleName() + ";"+k +";"+ i + ";" + evaluate.getPrecision() + ";" + evaluate.getAggregateDiversity() + ";" +
						aggregateMap.get(AggregateEvaluator.GINI) + ";" + aggregateMap.get(AggregateEvaluator.HERF) + ";" +
						aggregateMap.get(AggregateEvaluator.ENTROPY));
			}
			returnMapInternalTotal.put(k,returnMapInternal);
		}
		generateReturnMap();

    }

    // TODO: should get average score of all runs
	private void generateReturnMap(){
		returnMap=new HashMap<>();
		for(int  i=0;i<repeat;i++){
			Map<String, Map<Double, Double>> result = returnMapInternalTotal.get(i);
			Iterator<String> iterator = result.keySet().iterator();
			while(iterator.hasNext()){
				String key= iterator.next();
				returnMap.putIfAbsent(key,new HashMap<>());
				Map<Double, Double> resultMetric = result.get(key);
				if(returnMap.get(key).size()==0){
					returnMap.put(key,resultMetric);
				}else{
					Iterator<Double> iteratorResultMetric = resultMetric.keySet().iterator();
					Iterator<Double> returnMapIterator = returnMap.get(key).keySet().iterator();
					Map<Double,Double> newReturnMapResults=new HashMap<>();
					while(iteratorResultMetric.hasNext()){
						Double precision = iteratorResultMetric.next();
						Double returnMapAccuracy = returnMapIterator.next();
						double newPrecision = (returnMapAccuracy* repeat + precision) / (repeat + 1);
						double newAgg=(returnMap.get(key).get(returnMapAccuracy)* repeat + resultMetric.get(precision)) / (repeat + 1);
						newReturnMapResults.put(newPrecision,newAgg);
					}
				}

			}
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
             	if (random.nextDouble() > evaluationPercentage){
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
