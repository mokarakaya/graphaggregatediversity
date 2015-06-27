package scr.evaulator;

import java.math.BigDecimal;

/**
 * Created by p.bell on 27.06.2015.
 */
public class EntropyEvalutator extends AbstractEvaluator {
    @Override
    public void add(BigDecimal reciDividedByTotal) {
        total=total.add(reciDividedByTotal.multiply(BigDecimal.valueOf(Math.log(reciDividedByTotal.doubleValue()))));
    }

    @Override
    public BigDecimal getReturn() {
        return total.multiply(BigDecimal.valueOf(-1));
    }
}
