package scr.baseRecommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.AbstractTest;
import scr.baseRecommender.generic.BaseRecommender;
import scr.ratingManipulation.proposed.AggregateGraphRecommender;


/**
 * Unit test for RMRecommender.
 */
public class AggregateGraphTest extends AbstractTest
{

	public AggregateGraphTest(BaseRecommender baseRecommender,String data){
		this.baseRecommender=baseRecommender;
		this.displayName="Graph";
		this.data=data;
	}
	public AggregateGraphTest(BaseRecommender baseRecommender,String data,int repeat){
		this.baseRecommender=baseRecommender;
		this.displayName="Graph";
		this.repeat=repeat;
		this.data=data;
	}
	@Override
	public double getMinThreshold() { return 0; }
	@Override
	public double getMaxThreshold() { return 10; }
	@Override
	public double getIncThreshold() { return 1; }//

	@Override
	public Recommender getRecommender(Recommender baseRecommender, double threshold) throws TasteException {
		return new AggregateGraphRecommender(baseRecommender, threshold);
	}
}
