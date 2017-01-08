package scr.evaulator;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import scr.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * check the full formula at ;https://drive.google.com/file/d/0Bza6pDqxvgg-QWo0emtMcVlrM0k/view?usp=sharing
 * Created by mokarakaya on 22.06.2015.
 */
public class GiniEvaluator extends AbstractEvaluator{

    public GiniEvaluator(){
        super();
    }

    @Override
    public void add(BigDecimal reciDividedByTotal) {
        throw new NotImplementedException();
    }

    public void add(BigDecimal ordered,BigDecimal reciDividedByTotal) {
        total= total.add(ordered.multiply(reciDividedByTotal));
    }

    @Override
    public BigDecimal getReturn() {
        return total.multiply(new BigDecimal(2));
    }


}
