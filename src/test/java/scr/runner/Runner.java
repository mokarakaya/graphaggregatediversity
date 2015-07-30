package scr.runner;

import org.apache.mahout.cf.taste.common.TasteException;

import java.io.IOException;

/**
 * Created by p.bell on 26.07.2015.
 */
public interface Runner {
    //public static final String DATA="Movielens";
    //public static final String DATA="Yahoo Music";
    //public static final String DATA="Bookcrossing";
    public static final String DATA="Movielens100K";
    public void testApp() throws InterruptedException, TasteException, IOException;
}
