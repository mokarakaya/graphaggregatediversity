package scr.ratingManipulation;

import org.apache.mahout.cf.taste.eval.IRStatistics;

public interface RMIRStatistics extends IRStatistics{

	double getAggregateDiversity();

	double getGiniDiversity();
}
