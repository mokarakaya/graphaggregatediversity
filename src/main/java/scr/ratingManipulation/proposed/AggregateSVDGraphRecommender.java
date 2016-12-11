package scr.ratingManipulation.proposed;

import org.apache.commons.lang.NotImplementedException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.CandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by p.bell on 02.03.2016.
 */
public class AggregateSVDGraphRecommender extends AbstractRecommender {

    private Recommender recommender;
    private ParallelSGDGraphFactorizer factorizer;
    protected Map<Integer,Integer> counter;
    private double mu0 = 0.01;
    public AggregateSVDGraphRecommender(DataModel dataModel, CandidateItemsStrategy candidateItemsStrategy,double threshold) throws TasteException {
        super(dataModel, candidateItemsStrategy);
        counter=new HashMap<>();
        int numFeatures=100;
        float lambda=new Float( 0.02);
        int numEpochs=20;
        factorizer=new ParallelSGDGraphFactorizer(dataModel, numFeatures, lambda, numEpochs,threshold);
        this.recommender=new SVDRecommender(dataModel,factorizer,candidateItemsStrategy);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
        List<RecommendedItem> recommend = recommender.recommend(userID, howMany, rescorer);
        for(RecommendedItem item: recommend){
            Integer recommendationCount= counter.get((int)item.getItemID());
            if(recommendationCount==null){
                recommendationCount=0;
            }
            counter.put((int) item.getItemID(), recommendationCount+1);
            factorizer.update(new GenericPreference(userID,item.getItemID(),item.getValue()),mu0,counter.get((int)item.getItemID()));
        }
        return recommend;
    }

    @Override
    public float estimatePreference(long userID, long itemID) throws TasteException {
        throw new NotImplementedException();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        throw new NotImplementedException();
    }
}
