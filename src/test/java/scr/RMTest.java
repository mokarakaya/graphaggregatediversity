package scr;

import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;

import scr.ratingManipulation.RMRecommender;


/**
 * Unit test for RMRecommender.
 */
public class RMTest
    extends AbstractTest
{
	@Override
	double getMinThreshold() {
		return 0;
	}

	@Override
	double getMaxThreshold() {
		return 1;
	}

	@Override
	double getIncThreshold() {
		return 0.1;
	}
	@Override
	public Recommender getRecommender(SVDRecommender recommender,
			double threshold) {
		return new RMRecommender(recommender, threshold);
	}
}
