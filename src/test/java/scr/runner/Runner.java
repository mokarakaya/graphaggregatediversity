package scr.runner;

import org.apache.mahout.cf.taste.common.TasteException;

import java.io.IOException;

/**
 * Created by mokarakaya on 26.07.2015.
 */
public interface Runner {
    void testApp() throws InterruptedException, TasteException, IOException;
}
