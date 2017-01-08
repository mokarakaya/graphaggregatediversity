package scr.baseRecommender.generic;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.runner.Runner;

import java.io.File;
import java.io.IOException;

/**
 * Created by mokarakaya on 06.10.2015.
 */
public class TestEval {

    public static void main(String [] args) throws IOException, TasteException {
        DataModel dataModel= new FileDataModel(new File("C:/javafx/data/Yahoo Music.data"));
        RecommenderBuilder builder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                SVDBaseRecommender recommender= new SVDBaseRecommender ();
                return recommender.getBaseRecommender(dataModel);
            }
        };
        //RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        double evaluate=evaluator.evaluate(builder, null, dataModel, 0.8, 1);
        System.out.println(evaluate);
    }
}
