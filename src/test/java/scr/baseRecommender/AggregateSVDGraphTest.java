package scr.baseRecommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.AllUnknownItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.AbstractTest;
import scr.baseRecommender.generic.BaseRecommender;
import scr.ratingManipulation.purposed.AggregateGraphRecommender;
import scr.ratingManipulation.purposed.AggregateSVDGraphRecommender;


/**
 * Unit test for RMRecommender.
 */
public class AggregateSVDGraphTest extends AbstractTest
{

	public AggregateSVDGraphTest(BaseRecommender baseRecommender){
		this.baseRecommender=baseRecommender;
		this.displayName="GraphSVD";
	}
	@Override
	public double getMinThreshold() { return 0; }
	@Override
	public double getMaxThreshold() { return 10; }
	@Override
	public double getIncThreshold() { return 1; }//

	@Override
	public Recommender getRecommender(Recommender baseRecommender, double threshold) throws TasteException {
		return new AggregateSVDGraphRecommender(baseRecommender.getDataModel(),new AllUnknownItemsCandidateItemsStrategy(),threshold);
	}
}
