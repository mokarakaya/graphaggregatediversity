package scr;

import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;

import scr.ratingManipulation.RMRecommender;


/**
 * Unit test for RMRecommender.
 */
public class SVDTest 
    extends AbstractTest
{

	@Override
	public Recommender getRecommender(SVDRecommender recommender,
			double threshold) {
		return new RMRecommender(recommender, threshold);
	}
}
