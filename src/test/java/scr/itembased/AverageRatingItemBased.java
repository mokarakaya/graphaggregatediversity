package scr.itembased;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.baseRecommender.BaseRecommender;
import scr.baseRecommender.ItemBasedBaseRecommender;
import scr.svd.AverageRatingTest;

/**
 * Created by p.bell on 07.07.2015.
 */
public class AverageRatingItemBased extends AverageRatingTest {

    @Override
    public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
        BaseRecommender baseRecommender=new ItemBasedBaseRecommender();
        return baseRecommender.getBaseRecommender(dataModel);
    }
}
