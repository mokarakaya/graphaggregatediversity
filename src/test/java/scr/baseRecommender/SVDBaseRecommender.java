package scr.baseRecommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.AllUnknownItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.svd.ParallelSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.baseRecommender.BaseRecommender;

/**
 * Created by p.bell on 06.07.2015.
 */
public class SVDBaseRecommender implements BaseRecommender {


    @Override
    public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
        int numFeatures=100;
        float lambda=new Float( 0.02);
        int numEpochs=20;
        ParallelSGDFactorizer factorizer=new ParallelSGDFactorizer(dataModel, numFeatures, lambda, numEpochs);
        SVDRecommender recommender =new SVDRecommender(dataModel,factorizer,new AllUnknownItemsCandidateItemsStrategy());
        return recommender;
    }
}
