package scr;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import junit.framework.TestCase;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.baseRecommender.AverageRatingTest;
import scr.baseRecommender.PopularityTest;
import scr.baseRecommender.RMTest;
import scr.baseRecommender.generic.ItemBasedBaseRecommender;
import scr.draw.XYChartCreator;
import scr.draw.XYChartModel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by p.bell on 11.07.2015.
 */
public class XYChartTest {

    public void testApp(String title,List<AbstractTest> tests) throws IOException, TasteException, InterruptedException {
        // Because we need to init the JavaFX toolkit - which usually Application.launch does
        // I'm not sure if this way of launching has any effect on anything
        new JFXPanel();
        List<Thread>threads= new ArrayList<>();
        for(AbstractTest test: tests){
            Thread thread = new Thread(test);
            thread.start();
            threads.add(thread);
        }

        for(Thread thread: threads){
            thread.join();
        }

        XYChartDataSaver dataSaver= new XYChartDataSaver();
        Iterator<String> iterator = tests.get(0).returnMap.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            dataSaver.saveData(tests,key,title);
            Map<String,Map<Double,Double>> map= new HashMap<>();
            for(AbstractTest test: tests){
                map.put(test.displayName, test.returnMap.get(key));
            }
            XYChartModel model=new XYChartModel();
            model.title=title;
            model.xAxisLabel="precision";
            model.yAxisLabel=key;
            model.series=map;

            try {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Your class that extends Application
                        try {
                            XYChartCreator xyChartCreator =new XYChartCreator(model);
                            xyChartCreator.start(new Stage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //graphs should be rendered in 10000000 milis
        Thread.sleep(10000000);
    }




}
