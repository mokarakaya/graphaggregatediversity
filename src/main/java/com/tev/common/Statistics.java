
package com.tev.common;

/**
 * <p>
 * Implementations encapsulate statistics about an evaluator
 * </p>
 * 
 */
public interface Statistics {
  
  /**
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Information_retrieval#Precision">Precision</a>.
   * </p>
   */
  double getPrecision();
  
  /**
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Information_retrieval#Recall">Recall</a>.
   * </p>
   */
  double getRecall();
  
  /**
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Information_retrieval#Fall-Out">Fall-Out</a>.
   * </p>
   */
  long getAggDiversity();
  double getIndDiversity();
  double getSerendipity();
  double getNovelty();
  /**
   * <p>
   * 
   * </p>
   */
  
}
