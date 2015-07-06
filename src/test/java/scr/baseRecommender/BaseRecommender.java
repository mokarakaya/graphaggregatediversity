package scr.baseRecommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * Created by p.bell on 06.07.2015.
 */
public interface BaseRecommender {

    public Recommender getBaseRecommender(DataModel dataModel) throws TasteException;
}
