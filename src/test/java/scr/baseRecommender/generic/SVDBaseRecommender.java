package scr.baseRecommender.generic;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.AllUnknownItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.svd.ParallelSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * Created by mokarakaya on 06.07.2015.
 */
public class SVDBaseRecommender implements BaseRecommender {


    @Override
    public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
        int numFeatures=100;
        float lambda=new Float( 0.02);
        int numEpochs=20;
        double mu0=0.01;
        double decayFactor=1.0;
        int stepOffset=0;
        double forgettingExponent=0.0;
        int numThreads=4;
        ParallelSGDFactorizer factorizer=new ParallelSGDFactorizer(dataModel, numFeatures, lambda, numEpochs,
        mu0,decayFactor, stepOffset, forgettingExponent,numThreads);
        SVDRecommender recommender =new SVDRecommender(dataModel,factorizer,new AllUnknownItemsCandidateItemsStrategy());
        return recommender;
    }
}
