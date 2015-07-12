package scr.svd;

import org.apache.mahout.cf.taste.common.TasteException;
import scr.baseRecommender.AverageRatingTest;
import scr.baseRecommender.generic.SVDBaseRecommender;

import java.io.IOException;

/**
 * Created by p.bell on 07.07.2015.
 */
public class AverageRatingSVD extends AverageRatingTest implements SVDBaseRecommender,Runnable {
    @Override
    public void run() {
        try {
            testApp();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TasteException e) {
            e.printStackTrace();
        }
    }
}
