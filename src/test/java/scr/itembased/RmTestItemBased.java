package scr.itembased;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.baseRecommender.BaseRecommender;
import scr.baseRecommender.ItemBasedBaseRecommender;
import scr.baseRecommender.SVDBaseRecommender;
import scr.svd.RMTest;

/**
 * Created by p.bell on 06.07.2015.
 */
public class RmTestItemBased  extends RMTest{


    @Override
    public Recommender getBaseRecommender(DataModel dataModel) throws TasteException {
        BaseRecommender baseRecommender=new ItemBasedBaseRecommender();
        return baseRecommender.getBaseRecommender(dataModel);
    }
}
