package scr.baseRecommender.generic;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.CachingUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Created by p.bell on 11.07.2015.
 */
public class UserBasedBaseRecommender implements BaseRecommender{

    @Override
    public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
        //UserSimilarity similarity = (UserSimilarity) new PearsonCorrelationSimilarity(dataModel);
        UserSimilarity similarity = (UserSimilarity) new UncenteredCosineSimilarity(dataModel);
        //UserSimilarity similarity = (UserSimilarity) new TanimotoCoefficientSimilarity(dataModel);
        UserNeighborhood userNeighborhood= new NearestNUserNeighborhood(dataModel.getNumUsers(),similarity,dataModel);
        return new CachingRecommender(new GenericUserBasedRecommender(dataModel,new CachingUserNeighborhood(userNeighborhood,dataModel), similarity));
    }
}
