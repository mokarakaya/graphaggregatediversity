package scr.evaulator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import scr.MathOperations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mokarakaya on 27.06.2015.
 */
public class AggregateEvaluator {

    //we evaluate the recommenders with four different evaluators.
    public static final String GINI ="gini";
    public static final String HERF = "herf";
    public static final String ENTROPY= "entropy";
    public static final String AGGREGATE="aggregate";
    public Map<String,BigDecimal> getResult(DataModel dataModel, int at, Map<Long, Integer> aggregateDiversityMap, DataModel testDataModel) throws TasteException {


        HerfindahlEvaluator herf= new HerfindahlEvaluator();
        GiniEvaluator gini= new GiniEvaluator();
        EntropyEvalutator entropy= new EntropyEvalutator();
        Map<Long, Integer> giniDiversityMap=new HashMap<>();
        LongPrimitiveIterator iterator = dataModel.getItemIDs();
        while(iterator.hasNext()) {
            Long itemId = iterator.next();
            if(aggregateDiversityMap.get(itemId)==null){
                giniDiversityMap.put(itemId,0);
            }else{
                giniDiversityMap.put(itemId,aggregateDiversityMap.get(itemId));
            }
        }
        List<Long> sortedGiniDiversityList = MathOperations.sortByValueAsc(giniDiversityMap);
        double candidateItems = dataModel.getNumItems() + 1;
        double numberOfUsers=testDataModel.getNumUsers()*at;
        int count=1;
        for(Long itemId:sortedGiniDiversityList) {
            Integer reci=aggregateDiversityMap.get(itemId);
            if(reci!=null) {
                BigDecimal ordered = new BigDecimal((candidateItems - count) / candidateItems);
                BigDecimal reciDividedByTotal=new BigDecimal(reci).divide(new BigDecimal(numberOfUsers), 10, RoundingMode.DOWN);
                gini.add(ordered,reciDividedByTotal);
                herf.add(reciDividedByTotal);
                entropy.add(reciDividedByTotal);
            }
            count++;
        }
        Map<String,BigDecimal> result= new HashMap<>();
        result.put(GINI,gini.getReturn());
        result.put(HERF,herf.getReturn());
        result.put(ENTROPY,entropy.getReturn());
        return result;
    }
}
