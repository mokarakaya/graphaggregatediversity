package scr.evaulator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by p.bell on 27.06.2015.
 */
public class AggregateEvaluator {

    public static final String GINI ="gini";
    public static final String HERF = "herf";
    public Map<String,BigDecimal> getResult(DataModel dataModel, int at, Map<Long, Integer> aggregateDiversityMap, DataModel testDataModel) throws TasteException {


        HerfindahlEvaluator herf= new HerfindahlEvaluator();
        GiniEvaluator gini= new GiniEvaluator();
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
        List<Long> sortedGiniDiversityList = scr.Math.sortByValueAsc(giniDiversityMap);
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
            }
            count++;
        }
        Map<String,BigDecimal> result= new HashMap<>();
        result.put(GINI,gini.getReturn());
        result.put(HERF,herf.getReturn());
        return result;
    }
}
