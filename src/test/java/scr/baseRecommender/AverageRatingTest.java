package scr.baseRecommender;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;

import scr.AbstractTest;
import scr.baseRecommender.generic.BaseRecommender;
import scr.ratingManipulation.AverageRatingRecommender;
import scr.runner.Runner;


/**
 * Unit test for AverageRatingRecommender.
 */
public class AverageRatingTest extends AbstractTest
{
	public AverageRatingTest(BaseRecommender baseRecommender){
		this.baseRecommender=baseRecommender;
		this.displayName="AverageRating";
	}
	public AverageRatingTest(BaseRecommender baseRecommender,int repeat){
		this.baseRecommender=baseRecommender;
		this.displayName="AverageRating";
		this.repeat=repeat;
	}
	/**
	 * keeps average rating of items
	 */
	private static Map<Long, Float> map = null;
	@Override
	public double getMinThreshold() {
		switch(Runner.DATA){
			case "Movielens":{
				return 3;
			}case "Movielens100K":{
				return 3;
			}case "Bookcrossing":{
				return 6;
			}default:{
				throw new RuntimeException("th for dataset not found");
			}
		}
	}
	@Override
	public double getMaxThreshold() {
		switch(Runner.DATA){
			case "Movielens":{
				return 5.4;
			}case "Movielens100K":{
				return 5.4;
			}case "Bookcrossing":{
				return 10.8;
			}default:{
				throw new RuntimeException("th for dataset not found");
			}
		}
	}
	@Override
	public double getIncThreshold() {
		switch(Runner.DATA){
			case "Movielens":{
				return 0.2;
			}case "Movielens100K":{
				return 0.2;
			}case "Bookcrossing":{
				return 0.4;
			}default:{
				throw new RuntimeException("th for dataset not found");
			}
		}
	}

	@Override
	public Recommender getRecommender(Recommender recommender,double threshold) throws TasteException {
		if(map==null){
			map=getAverageRatingMap(recommender.getDataModel());
		}
		return new AverageRatingRecommender(recommender, threshold, map);
	}
	/**
	 * return average rating of items
	 * @param dataModel
	 * @return
	 * @throws TasteException
	 */
	private Map<Long, Float> getAverageRatingMap(DataModel dataModel) throws TasteException {
		Map<Long, Float> avgRatingMap=new HashMap<>();
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
}
