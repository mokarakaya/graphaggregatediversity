package scr.evaulator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by p.bell on 10.01.2017.
 */
public class CosineSimilarityCached {

    private final Map<Long,Map<Long,Double>> cachedSimilarities;
    private final DataModel dataModel;
    public CosineSimilarityCached(DataModel dataModel){
        this.dataModel=dataModel;
        cachedSimilarities= new HashMap<>();
    }

    public double getSimilarity(long itemI, long itemJ) throws TasteException {
        long max= Math.max(itemI,itemJ);
        long min= Math.min(itemI,itemJ);
        if(cachedSimilarities.containsKey(min) && cachedSimilarities.get(min).containsKey(max)){
            return cachedSimilarities.get(min).get(max);
        }
        double similarity=calculateSimilarity(min,max);
        cachedSimilarities.putIfAbsent(min,new HashMap<>());
        cachedSimilarities.get(min).put(max,similarity);
        return similarity;
    }

    private double calculateSimilarity(long min, long max) throws TasteException {
        double dot=getDotProduct(min, max);
        double magnitudeMin=getMagnitude(min);
        double magnitudeMax=getMagnitude(max);
        return dot / (magnitudeMin*magnitudeMax) ;
    }

    private double getDotProduct(long min, long max) throws TasteException {
        double dot=0;
        PreferenceArray preferencesForItemMin = dataModel.getPreferencesForItem(min);
        for(Preference preference: preferencesForItemMin){
            Float preferenceValue = dataModel.getPreferenceValue(preference.getUserID(), max);
            if(preferenceValue!=null){
                dot+= preference.getValue()*preferenceValue;
            }
        }
        return dot;
    }
    private double getMagnitude(long itemId) throws TasteException {
        double magnitude=0;
        PreferenceArray preferencesForItem = dataModel.getPreferencesForItem(itemId);
        for(Preference preference: preferencesForItem){
            magnitude+= preference.getValue()*preference.getValue();
        }
        return Math.sqrt(magnitude);
    }




}
