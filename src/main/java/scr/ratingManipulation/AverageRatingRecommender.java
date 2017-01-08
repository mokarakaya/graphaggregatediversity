package scr.ratingManipulation;

import org.apache.commons.lang.NotImplementedException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.hadoop.MutableRecommendedItem;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.ByValueRecommendedItemComparator;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.util.*;

/**
 * Created by mokarakaya on 07.04.2015.
 * Adomavicius method
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
		List<RecommendedItem> recommend = recommender.recommend(userID, recommender.getDataModel().getNumItems(), rescorer);
		for (RecommendedItem recommendedItem : recommend) {
			 if(recommendedItem.getValue()<threshold ){
				 break;
			 }
			 //multiply by -1 to sort ascending. we should recommend item with least avgRatings first
			RecommendedItem item=new MutableRecommendedItem(recommendedItem.getItemID(), (-1)*averageRating.get(recommendedItem.getItemID()));
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
