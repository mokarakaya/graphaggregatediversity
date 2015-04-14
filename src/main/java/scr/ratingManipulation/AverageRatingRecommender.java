package scr.ratingManipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.hadoop.MutableRecommendedItem;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.ByValueRecommendedItemComparator;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * Created by p.bell on 07.04.2015.
 */
public class AverageRatingRecommender extends AbstractRecommender{
    
	private final Double threshold ;
	Map<Long,Float> averageRating;
	private Recommender recommender;
	public AverageRatingRecommender(Recommender recommender,Double threshold,Map<Long,Float> averageRating) {
		super(recommender.getDataModel());
		this.recommender=recommender;
		this.threshold=threshold;
		this.averageRating=averageRating;
	}
	


    @Override
	public List<RecommendedItem> recommend(long userID, int howMany,IDRescorer rescorer) throws TasteException {
    	List<RecommendedItem> aboveTh=new ArrayList<>(); 
    	List<RecommendedItem> finalRecommendedItems=new ArrayList<>();
		List<RecommendedItem> recommend = recommender.recommend(userID, 10000, rescorer);
		for (RecommendedItem recommendedItem : recommend) {
			 if(recommendedItem.getValue()<threshold ){
				 break;
			 }
			RecommendedItem item=new MutableRecommendedItem(recommendedItem.getItemID(),averageRating.get(recommendedItem.getItemID()));
			aboveTh.add(item);
		 }
		
		Collections.sort(aboveTh, ByValueRecommendedItemComparator.getInstance());
		for(RecommendedItem item: aboveTh){
			if(finalRecommendedItems.size()==howMany){
				break;
			}
			finalRecommendedItems.add(item);
		}
		for(RecommendedItem item: recommend){
			if(finalRecommendedItems.size()==howMany){
				break;
			}
			if(!alreadyRecommended(finalRecommendedItems,item.getItemID())){
				finalRecommendedItems.add(item);
			}
		}

	     return finalRecommendedItems;
	}

	private boolean alreadyRecommended(List<RecommendedItem> finalRecommendedItems, long itemID) {
		for(RecommendedItem item: finalRecommendedItems){
			if(item.getItemID()==itemID){
				return true;
			}
		}
		return false;
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
}
