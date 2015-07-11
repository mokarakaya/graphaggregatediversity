package scr.baseRecommender;

import java.util.HashMap;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import scr.AbstractTest;
import scr.ratingManipulation.AverageRatingRecommender;


/**
 * Unit test for AverageRatingRecommender.
 */
public abstract class PopularityTest extends AbstractTest
{
	/**
	 * keeps average rating of items
	 */
	private static Map<Long, Float> map = null;
	@Override
	public double getMinThreshold() { return 3; }
	@Override
	public double getMaxThreshold() { return 5.4; }
	@Override
	public double getIncThreshold() { return 0.2; }

	@Override
	public Recommender getRecommender(Recommender recommender, double threshold) throws TasteException {
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
