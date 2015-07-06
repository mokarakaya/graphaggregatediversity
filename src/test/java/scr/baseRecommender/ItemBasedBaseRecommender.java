package scr.baseRecommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

/**
 * Created by p.bell on 06.07.2015.
 */
public class ItemBasedBaseRecommender implements  BaseRecommender {
    @Override
    public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
        ItemSimilarity similarity = (ItemSimilarity) new PearsonCorrelationSimilarity(dataModel);
        return new CachingRecommender(new GenericItemBasedRecommender(dataModel, similarity));
    }
}
