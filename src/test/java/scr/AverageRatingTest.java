package scr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;

import scr.ratingManipulation.AverageRatingRecommender;


/**
 * Unit test for AverageRatingRecommender.
 */
public class AverageRatingTest 
    extends AbstractTest
{
	/**
	 * keeps average rating of items
	 */
	private static Map<Long, Float> map = null;

	@Override
	public Recommender getRecommender(SVDRecommender recommender,
			double threshold) throws TasteException {
		if(map==null){
			map=getAverageRatingMap(recommender.getDataModel());
		}
		return new AverageRatingRecommender(recommender, threshold, map);
	}
	@Override
	double getMinThreshold() {
		return 7;
	}

	@Override
	double getMaxThreshold() {
		return 11;
	}

	@Override
	double getIncThreshold() {
		return 0.4;
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
