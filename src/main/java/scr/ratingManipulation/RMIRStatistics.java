package scr.ratingManipulation;

import org.apache.mahout.cf.taste.eval.IRStatistics;

import java.math.BigDecimal;
import java.util.Map;

public interface RMIRStatistics extends IRStatistics{

	double getAggregateDiversity();

	Map<String,BigDecimal> getAggregateMap();
}
