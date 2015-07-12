package scr.svd;

import org.apache.mahout.cf.taste.common.TasteException;
import scr.baseRecommender.RMTest;
import scr.baseRecommender.generic.SVDBaseRecommender;

import java.io.IOException;


/**
 * Created by p.bell on 06.07.2015.
 */
public class RmTestSVD extends RMTest implements SVDBaseRecommender,Runnable {


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
