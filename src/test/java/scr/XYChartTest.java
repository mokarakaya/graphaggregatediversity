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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by p.bell on 11.07.2015.
 */
public class XYChartTest {

    public void testApp(String title,RMTest rmTest, PopularityTest popularityTest,AverageRatingTest averageRatingTest) throws IOException, TasteException, InterruptedException {
        // Because we need to init the JavaFX toolkit - which usually Application.launch does
        // I'm not sure if this way of launching has any effect on anything
        new JFXPanel();
        Thread rmTestThread = new Thread(rmTest);
        rmTestThread.start();

        Thread popularityThread=new Thread(popularityTest);
        popularityThread.start();

        Thread averageRatingThread= new Thread(averageRatingTest);
        averageRatingThread.start();

        rmTestThread.join();
        popularityThread.join();
        averageRatingThread.join();

        Iterator<String> iterator = rmTest.returnMap.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            Map<String,Map<Double,Double>> map= new HashMap<>();
            map.put("RM",rmTest.returnMap.get(key));
            map.put("Popularity", popularityTest.returnMap.get(key));
            map.put("AverageRating",averageRatingTest.returnMap.get(key));
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
