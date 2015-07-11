package scr.baseRecommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.LoglikelihoodSimilarity;

/**
 * Created by p.bell on 06.07.2015.
 */
public interface ItemBasedBaseRecommender extends BaseRecommender {

     default public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
        //ItemSimilarity similarity = (ItemSimilarity) new PearsonCorrelationSimilarity(dataModel);
        //ItemSimilarity similarity = (ItemSimilarity) new UncenteredCosineSimilarity(dataModel);
        ItemSimilarity similarity = (ItemSimilarity) new TanimotoCoefficientSimilarity(dataModel);
        return new CachingRecommender(new GenericItemBasedRecommender(dataModel, similarity));
    }
}
