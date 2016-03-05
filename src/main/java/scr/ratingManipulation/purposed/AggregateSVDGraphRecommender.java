package scr.ratingManipulation.purposed;

import org.apache.commons.lang.NotImplementedException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.AllUnknownItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.svd.ParallelSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.CandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.util.Collection;
import java.util.List;

/**
 * Created by p.bell on 02.03.2016.
 */
public class AggregateSVDGraphRecommender extends AbstractRecommender {

    private Recommender recommender;
    public AggregateSVDGraphRecommender(DataModel dataModel, CandidateItemsStrategy candidateItemsStrategy) throws TasteException {
        super(dataModel, candidateItemsStrategy);
        int numFeatures=100;
        float lambda=new Float( 0.02);
        int numEpochs=20;
        ParallelSGDGraphFactorizer factorizer=new ParallelSGDGraphFactorizer(dataModel, numFeatures, lambda, numEpochs);
        this.recommender=new SVDRecommender(dataModel,factorizer,candidateItemsStrategy);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
        return recommender.recommend(userID,howMany,rescorer);
    }

    @Override
    public float estimatePreference(long userID, long itemID) throws TasteException {
        throw new NotImplementedException();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        throw new NotImplementedException();
    }
}
