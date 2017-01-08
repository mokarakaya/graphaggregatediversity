package scr.evaulator;

import java.math.BigDecimal;

/**
 * check the full formula at ;https://drive.google.com/file/d/0Bza6pDqxvgg-QWo0emtMcVlrM0k/view?usp=sharing
 * Created by mokarakaya on 27.06.2015.
 */
public class EntropyEvalutator extends AbstractEvaluator {

    public EntropyEvalutator(){
        super();
    }

    @Override
    public void add(BigDecimal reciDividedByTotal) {
        total=total.add(reciDividedByTotal.multiply(BigDecimal.valueOf(Math.log(reciDividedByTotal.doubleValue()))));
    }

    @Override
    public BigDecimal getReturn() {
        return total.multiply(BigDecimal.valueOf(-1));
    }
}
