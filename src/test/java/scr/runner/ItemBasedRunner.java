package scr.runner;

import junit.framework.TestCase;
import org.apache.mahout.cf.taste.common.TasteException;
import scr.XYChartTest;
import scr.baseRecommender.AverageRatingTest;
import scr.baseRecommender.PopularityTest;
import scr.baseRecommender.RMTest;
import scr.baseRecommender.generic.BaseRecommender;
import scr.baseRecommender.generic.ItemBasedBaseRecommender;
import scr.baseRecommender.generic.SVDBaseRecommender;

import java.io.IOException;

/**
 * Created by p.bell on 12.07.2015.
 */
public class ItemBasedRunner extends TestCase implements  Runner{

    public void testApp() throws InterruptedException, TasteException, IOException {
        BaseRecommender baseRecommender= new ItemBasedBaseRecommender();
        XYChartTest test= new XYChartTest();
        test.testApp("Item Based "+DATA,new RMTest(baseRecommender),
                    new PopularityTest(baseRecommender),new AverageRatingTest(baseRecommender));

    }
}
