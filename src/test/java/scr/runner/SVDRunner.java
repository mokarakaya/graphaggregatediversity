package scr.runner;

import junit.framework.TestCase;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.AbstractTest;
import scr.XYChartTest;
import scr.baseRecommender.*;
import scr.baseRecommender.generic.BaseRecommender;
import scr.baseRecommender.generic.SVDBaseRecommender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mokarakaya on 12.07.2015.
 */
public class SVDRunner extends TestCase implements  Runner{

    public void testApp() throws InterruptedException, TasteException, IOException {



        runTests("Movielens");
        //runTests("Yahoo Music");
        runTests("Bookcrossing");
        //runTests("Movielens100K");

    }

    public void runTests(String data) throws InterruptedException, TasteException, IOException {
        BaseRecommender baseRecommender= new SVDBaseRecommender();
        XYChartTest test= new XYChartTest();
        List<AbstractTest>tests=new ArrayList<>();
        tests.add(new AggregateSVDGraphTest(baseRecommender,data,1));
        tests.add(new AggregateGraphTest(baseRecommender,data,1));
        //tests.add(new RMTest(baseRecommender));
        //tests.add(new PopularityTest(baseRecommender,1));
        //tests.add(new AverageRatingTest(baseRecommender,1));
        test.testApp("SVD "+data, tests);
    }
}
