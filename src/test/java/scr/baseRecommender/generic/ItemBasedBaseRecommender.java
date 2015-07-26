package scr.baseRecommender.generic;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.AllSimilarItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.SamplingCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.CandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.MostSimilarItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

/**
 * Created by p.bell on 06.07.2015.
 */
public class ItemBasedBaseRecommender implements BaseRecommender {

    @Override
     public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
        //ItemSimilarity similarity = (ItemSimilarity) new PearsonCorrelationSimilarity(dataModel);
        ItemSimilarity similarity = (ItemSimilarity) new UncenteredCosineSimilarity(dataModel);
        //ItemSimilarity similarity = (ItemSimilarity) new TanimotoCoefficientSimilarity(dataModel);
        CandidateItemsStrategy candidateItemsStrategy=new SamplingCandidateItemsStrategy(dataModel.getNumUsers(),dataModel.getNumItems());
        SamplingCandidateItemsStrategy mostSimilarItemsCandidateItemsStrategy=new SamplingCandidateItemsStrategy(dataModel.getNumUsers(),dataModel.getNumItems());
        return new CachingRecommender(new GenericItemBasedRecommender(dataModel, similarity,candidateItemsStrategy,mostSimilarItemsCandidateItemsStrategy));
    }
}
