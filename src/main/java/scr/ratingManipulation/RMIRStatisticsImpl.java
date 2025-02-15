/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scr.ratingManipulation;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public final class RMIRStatisticsImpl implements RMIRStatistics, Serializable {

    private final double precision;
    private final double aggregateDiversity;
    private final double recall;
    private final double fallOut;
    private final double ndcg;
    private final double reach;
    private final double individualDiversity;
    private final Map<String,BigDecimal> aggregateMap;

    RMIRStatisticsImpl(double precision, double recall, double fallOut, double ndcg, double reach,
                       double aggregateDiversity, Map<String,BigDecimal> aggregateMap,double individualDiversity) {
        this.individualDiversity=individualDiversity;
        this.aggregateMap=aggregateMap;
        Preconditions.checkArgument(Double.isNaN(precision) || (precision >= 0.0 && precision <= 1.0),
                "Illegal precision: " + precision);
        Preconditions.checkArgument(Double.isNaN(recall) || (recall >= 0.0 && recall <= 1.0), "Illegal recall: " + recall);
        Preconditions.checkArgument(Double.isNaN(fallOut) || (fallOut >= 0.0 && fallOut <= 1.0),
                "Illegal fallOut: " + fallOut);
        Preconditions.checkArgument(Double.isNaN(ndcg) || (ndcg >= 0.0 && ndcg <= 1.0), "Illegal nDCG: " + ndcg);
        Preconditions.checkArgument(Double.isNaN(reach) || (reach >= 0.0 && reach <= 1.0), "Illegal reach: " + reach);
        this.precision = precision;
        this.recall = recall;
        this.fallOut = fallOut;
        this.ndcg = ndcg;
        this.reach = reach;
        this.aggregateDiversity=aggregateDiversity;
    }

    @Override
    public double getPrecision() {
        return precision;
    }

    @Override
    public double getRecall() {
        return recall;
    }

    @Override
    public double getFallOut() {
        return fallOut;
    }

    @Override
    public double getF1Measure() {
        return getFNMeasure(1.0);
    }

    @Override
    public double getFNMeasure(double b) {
        double b2 = b * b;
        double sum = b2 * precision + recall;
        return sum == 0.0 ? Double.NaN : (1.0 + b2) * precision * recall / sum;
    }

    @Override
    public double getNormalizedDiscountedCumulativeGain() {
        return ndcg;
    }

    @Override
    public double getReach() {
        return reach;
    }

    @Override
    public String toString() {
        return "IRStatisticsImpl[precision:" + precision + ",recall:" + recall + ",fallOut:"
                + fallOut + ",nDCG:" + ndcg + ",reach:" + reach + ']';
    }

	@Override
	public double getAggregateDiversity() {
		return aggregateDiversity;
	}

    @Override
    public double getIndividualDiversity() {
        return individualDiversity;
    }

    @Override
    public Map<String,BigDecimal> getAggregateMap() {
        return aggregateMap;
    }


}
