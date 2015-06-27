package scr.evaulator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by p.bell on 22.06.2015.
 */
public abstract class AbstractEvaluator {
    protected BigDecimal total;

    public AbstractEvaluator(){
        total=new BigDecimal(0);
    }
    public abstract void add(BigDecimal reciDividedByTotal);
    public abstract BigDecimal getReturn();

}
