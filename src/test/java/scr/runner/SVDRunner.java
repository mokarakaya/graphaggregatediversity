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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by mokarakaya on 12.07.2015.
 */
public class SVDRunner extends TestCase implements  Runner{

    public void testApp() throws InterruptedException, TasteException, IOException {

        List<String> datasets=new ArrayList<>();
        datasets.add("Movielens100K");
        /*datasets.add("Movielens");
        datasets.add("Yahoo Music");
        datasets.add("Bookcrossing");*/
        ExecutorService executor = Executors.newFixedThreadPool(8);
        for(final String dataset: datasets){
            Runnable worker= () -> {
                try {
                    runTests(dataset);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TasteException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            executor.execute(worker);
        }

        executor.shutdown();
        if (!executor.awaitTermination(10L, TimeUnit.HOURS)) {
            System.err.println("Threads didn't finish in 10 hours!");
            System.err.println("Number of rejected tasks:"+ executor.shutdownNow().size());
        }

    }

    public void runTests(String data) throws InterruptedException, TasteException, IOException {
        BaseRecommender baseRecommender= new SVDBaseRecommender();
        XYChartTest test= new XYChartTest();
        List<AbstractTest>tests=new ArrayList<>();
        tests.add(new AggregateSVDGraphTest(baseRecommender,data,1));
        tests.add(new AggregateGraphTest(baseRecommender,data,1));
        //tests.add(new RMTest(baseRecommender));
        tests.add(new PopularityTest(baseRecommender,data,1));
        tests.add(new AverageRatingTest(baseRecommender,data,1));
        test.testApp("SVD "+data, tests);
    }
}
