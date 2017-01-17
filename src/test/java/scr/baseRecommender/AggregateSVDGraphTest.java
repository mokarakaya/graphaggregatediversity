package scr.baseRecommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.AllUnknownItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.AbstractTest;
import scr.baseRecommender.generic.BaseRecommender;
import scr.ratingManipulation.proposed.AggregateSVDGraphRecommender;
import scr.runner.Runner;


/**
 * Unit test for RMRecommender.
 */
public class AggregateSVDGraphTest extends AbstractTest
{

	public AggregateSVDGraphTest(BaseRecommender baseRecommender,String data){
		this.baseRecommender=baseRecommender;
		this.displayName="GraphSVD";
		this.repeat=1;
		this.data=data;
	}
	public AggregateSVDGraphTest(BaseRecommender baseRecommender,String data,int repeat){
		this.baseRecommender=baseRecommender;
		this.displayName="GraphSVD";
		this.repeat=repeat;
		this.data=data;
	}
	@Override
	public double getMinThreshold() {
		return 0;
	}
	@Override
	public double getMaxThreshold() {
		switch(data){
			case "Bookcrossing":
			case "Yahoo Music":{
				return 1;
			}default:{
				return 10;
			}
		}
	}
	@Override
	public double getIncThreshold() {
		switch(data){
			case "Bookcrossing":
			case "Yahoo Music":{
				return 0.1;
			}default:{
				return 1;
			}
		}
	}

	@Override
	public Recommender getRecommender(Recommender baseRecommender, double threshold) throws TasteException {
		return new AggregateSVDGraphRecommender(baseRecommender.getDataModel(),new AllUnknownItemsCandidateItemsStrategy(),threshold);
	}
}
