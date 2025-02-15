package scr.baseRecommender.generic;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.CandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.MostSimilarItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

/**
 * Created by mokarakaya on 06.07.2015.
 */
public class ItemBasedBaseRecommender implements BaseRecommender {

    @Override
     public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
        ItemSimilarity similarity = (ItemSimilarity) new PearsonCorrelationSimilarity(dataModel);
        return new GenericItemBasedRecommender(dataModel, similarity,
                new AllUnknownItemsCandidateItemsStrategy(),new AllUnknownItemsCandidateItemsStrategy());
    }
}
