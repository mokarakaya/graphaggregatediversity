package scr.ratingManipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.hadoop.MutableRecommendedItem;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.ByValueRecommendedItemComparator;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * Created by p.bell on 07.04.2015.
 */
public class RMRecommender extends AbstractRecommender{
    
	private final Double threshold ;
	public RMRecommender(Recommender recommender,Double threshold) {
		super(recommender.getDataModel());
		this.recommender=recommender;
		this.threshold=threshold;
	}
	private Recommender recommender;


    private List<RecommendedItem> manipulate(List<RecommendedItem> recommend,Map<Integer,Integer> counter) throws TasteException {
        List<RecommendedItem> result= new ArrayList<RecommendedItem>();
        for(RecommendedItem item: recommend){
            double value=  item.getValue();
            Integer recommendationCount=counter.get((int)item.getItemID());
            if(recommendationCount==null){
            	recommendationCount=0;
            }
            double multiplier= new Double(recommendationCount) / (this.getDataModel().getNumUsers());
            multiplier= Math.pow(multiplier, (threshold));
            Double doubleValue=new Double(value*(1-multiplier));
            result.add(new MutableRecommendedItem(item.getItemID(),doubleValue.floatValue()));
        }
        Collections.sort(result, ByValueRecommendedItemComparator.getInstance());
        return  result;
    }
	public List<RecommendedItem> recommend(long userID, int howMany,
			IDRescorer rescorer,Map<Integer,Integer> counter) throws TasteException {
		 List<RecommendedItem> recommend = recommender.recommend(userID, 10000, rescorer);
		 recommend=manipulate(recommend,counter);
		 recommend=recommend.subList(0,howMany);
	     return recommend;
	}
	@Override
	public float estimatePreference(long userID, long itemID)
			throws TasteException {
		throw new NotImplementedException();
	}
	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<RecommendedItem> recommend(long userID, int howMany,
			IDRescorer rescorer) throws TasteException {
		throw new NotImplementedException();
	}
}
