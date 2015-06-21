package scr;

import java.util.HashMap;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import scr.ratingManipulation.AverageRatingRecommender;


/**
 * Unit test for AverageRatingRecommender.
 */
public class PopularityTest 
    extends AbstractTest
{
	/**
	 * keeps average rating of items
	 */
	private static Map<Long, Float> map = null;

	@Override
	double getMinThreshold() {
		return 3;
	}

	@Override
	double getMaxThreshold() {
		return 5.4;
	}

	@Override
	double getIncThreshold() {
		return 0.2;
	}

	@Override
	public Recommender getRecommender(SVDRecommender recommender,
			double threshold) throws TasteException {
		if(map==null){
			map=getPopularityMap(recommender.getDataModel());
		}
		return new AverageRatingRecommender(recommender, threshold, map);
	}
	
	/**
	 * return average rating of items
	 * @param dataModel
	 * @return
	 * @throws TasteException
	 */
	private Map<Long, Float> getPopularityMap(DataModel dataModel) throws TasteException {
		Map<Long, Float> avgRatingMap=new HashMap<Long, Float>();
		LongPrimitiveIterator itemIDs = dataModel.getItemIDs();
		while(itemIDs.hasNext()){
			Long next = itemIDs.next();
			avgRatingMap.put(next, (float) dataModel.getPreferencesForItem(next).length());
		}
		return avgRatingMap;
	}
}
