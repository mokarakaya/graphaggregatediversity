package scr.ratingManipulation.purposed;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericRecommendedItem;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.util.*;
import java.util.stream.DoubleStream;

/**
 * Created by p.bell on 02.01.2016.
 */
public class AggregateGraphRecommender extends RMRecommender{

    private Map<Long,Map<Long,Integer>> cooccurrences=new HashMap<>();
    public AggregateGraphRecommender(Recommender recommender, Double threshold) throws TasteException {
        super(recommender, threshold);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany,IDRescorer rescorer) throws TasteException{
        List<RecommendedItem> recommend = recommender.recommend(userID, howMany, rescorer);
        recommend=manipulate(recommend);
        if(recommend.size()>howMany) {
            throw new TasteException("Recommendation List size exceeded");
        }
        for (RecommendedItem recommendedItem : recommend) {
            Integer recommendationCount= counter.get((int)recommendedItem.getItemID());
            if(recommendationCount==null){
                recommendationCount=0;
            }
            counter.put((int) recommendedItem.getItemID(), recommendationCount+1);
        }
        return recommend;
    }

    //TODO: we may recommend an observed item
    //TODO: we should add counter to the game.
    protected List<RecommendedItem> manipulate(List<RecommendedItem> recommend) throws TasteException {
        List<RecommendedItem> result= new ArrayList<RecommendedItem>();
        Random random = new Random();
        for(int itemIndex=0;itemIndex<recommend.size();itemIndex++){
            RecommendedItem item= recommend.get(itemIndex);
            long itemID = item.getItemID();
            for(int i=0;i<threshold*10;i++) {
                int totalCooc=0;
                Map<Long, Integer> cooccurence = getCooccurences(itemID);
                Map<Long, Integer> coocs=new HashMap<>();
                Iterator<Long> iterator = cooccurence.keySet().iterator();
                while(iterator.hasNext()){
                    long coocItemId=iterator.next();
                    double coocItemValue = cooccurence.get(coocItemId);
                    int counterValue=counter.get(coocItemId)==null ? 0: counter.get(coocItemId);
                    int cooc= (int) ((coocItemValue*100)/(recommender.getDataModel().getNumUsersWithPreferenceFor(coocItemId)+counterValue));
                    if(cooc<1){
                      cooc=1;
                    }
                    coocs.put(coocItemId,cooc);
                    totalCooc+=cooc;
                }
                if(itemID==item.getItemID()){
                    int cooc= (int) ((item.getValue()*100)/recommender.getDataModel().getMaxPreference());
                    int randomInt=random.nextInt(100);
                    if(cooc>randomInt){
                        continue;
                    }
                }
                int randomInt=random.nextInt(totalCooc);
                totalCooc=0;
                Iterator<Long> coocIterator = coocs.keySet().iterator();
                while(coocIterator.hasNext()){
                    Long next = coocIterator.next();
                    totalCooc+=coocs.get(next);
                    if(totalCooc>randomInt){
                        itemID=next;
                        break;
                    }
                }
            }
            result.add(new GenericRecommendedItem(itemID,item.getValue()));
        }

        return result;
    }

    private Map<Long, Integer> getCooccurences(long itemID) throws TasteException {
        if(cooccurrences.get(itemID)!=null){
            return cooccurrences.get(itemID);
        }
        Map<Long,Integer> cooccurence=new HashMap<>();
        PreferenceArray preferencesForItem = recommender.getDataModel().getPreferencesForItem(itemID);
        for(Preference preference:preferencesForItem){
            long userID = preference.getUserID();
            PreferenceArray preferencesFromUser = recommender.getDataModel().getPreferencesFromUser(userID);
            for(Preference userPreference: preferencesFromUser){
                if(cooccurence.get(userPreference.getItemID())==null){
                    cooccurence.put(userPreference.getItemID(),1);
                }else{
                    cooccurence.put(userPreference.getItemID(),cooccurence.get(userPreference.getItemID())+1);
                }
            }
        }
        cooccurrences.put(itemID,cooccurence);
        return cooccurence;
    }

}
