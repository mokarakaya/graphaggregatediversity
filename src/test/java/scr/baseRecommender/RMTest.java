package scr.baseRecommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.Recommender;

import scr.AbstractTest;
import scr.ratingManipulation.RMRecommender;


/**
 * Unit test for RMRecommender.
 */
public abstract class RMTest extends AbstractTest
{
	@Override
	public double getMinThreshold() { return 0; }
	@Override
	public double getMaxThreshold() { return 1; }
	@Override
	public double getIncThreshold() { return 0.1; }

	@Override
	public Recommender getRecommender(Recommender baseRecommender, double threshold) throws TasteException {
		return new RMRecommender(baseRecommender, threshold);
	}


}
