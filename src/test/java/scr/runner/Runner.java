package scr.runner;

import org.apache.mahout.cf.taste.common.TasteException;

import java.io.IOException;

/**
 * Created by mokarakaya on 26.07.2015.
 */
public interface Runner {
    //String DATA="Movielens";
    //String DATA="Yahoo Music";
    //String DATA="Bookcrossing";
    String DATA="Movielens100K";
    void testApp() throws InterruptedException, TasteException, IOException;
}
