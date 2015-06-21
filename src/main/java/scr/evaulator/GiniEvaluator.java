package scr.evaulator;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import scr.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by p.bell on 22.06.2015.
 */
public class GiniEvaluator extends AbstractEvaluator{

    public GiniEvaluator(){
        super();
    }

    public BigDecimal getResult(DataModel dataModel, int at, Map<Long, Integer> aggregateDiversityMap, DataModel testDataModel) throws TasteException {
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
                BigDecimal gini = new BigDecimal((candidateItems - count) / candidateItems);
                gini = gini.multiply(new BigDecimal(reci).divide(new BigDecimal(numberOfUsers), 10, RoundingMode.DOWN));
                total = total.add(gini);
            }
            count++;
        }
        return total.multiply(new BigDecimal(2));
    }
}
