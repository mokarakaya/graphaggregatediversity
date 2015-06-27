package scr.evaulator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by p.bell on 27.06.2015.
 */
public class HerfindahlEvaluator extends AbstractEvaluator {

    public HerfindahlEvaluator(){
        super();
    }
    @Override
    public void add(BigDecimal reciDividedByTotal) {
        total=total.add(reciDividedByTotal.multiply(reciDividedByTotal));
    }

    @Override
    public BigDecimal getReturn() {
        return new BigDecimal(1).subtract(total);
    }
}
