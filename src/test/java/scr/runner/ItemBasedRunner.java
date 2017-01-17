package scr.runner;

import junit.framework.TestCase;
import org.apache.mahout.cf.taste.common.TasteException;
import scr.AbstractTest;
import scr.XYChartTest;
import scr.baseRecommender.AverageRatingTest;
import scr.baseRecommender.PopularityTest;
import scr.baseRecommender.RMTest;
import scr.baseRecommender.generic.BaseRecommender;
import scr.baseRecommender.generic.ItemBasedBaseRecommender;
import scr.baseRecommender.generic.SVDBaseRecommender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mokarakaya on 12.07.2015.
 */
public class ItemBasedRunner extends TestCase implements  Runner{

    public void testApp() throws InterruptedException, TasteException, IOException {
        BaseRecommender baseRecommender= new ItemBasedBaseRecommender();
        XYChartTest test= new XYChartTest();
        List<AbstractTest> tests=new ArrayList<>();
        tests.add(new RMTest(baseRecommender));
        tests.add(new PopularityTest(baseRecommender));
        tests.add(new AverageRatingTest(baseRecommender));
        //test.testApp("Item Based "+DATA,tests);

    }
}
