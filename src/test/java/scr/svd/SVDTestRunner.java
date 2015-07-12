package scr.svd;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import junit.framework.TestCase;
import org.apache.mahout.cf.taste.common.TasteException;
import scr.draw.XYChartCreator;
import scr.draw.XYChartModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by p.bell on 11.07.2015.
 */
public class SVDTestRunner extends TestCase{

    public void testApp() throws IOException, TasteException, InterruptedException {
        // Because we need to init the JavaFX toolkit - which usually Application.launch does
        // I'm not sure if this way of launching has any effect on anything
        new JFXPanel();
        RmTestSVD rmTestSVD= new RmTestSVD();
        Thread rmTestSVDThread = new Thread(rmTestSVD);
        rmTestSVDThread.start();

        PopularitySVD popularitySVD= new PopularitySVD();
        Thread popularitySVDThread=new Thread(popularitySVD);
        popularitySVDThread.start();

        AverageRatingSVD averageRatingSVD= new AverageRatingSVD();
        Thread averageRatingSVDThread= new Thread(averageRatingSVD);
        averageRatingSVDThread.start();

        rmTestSVDThread.join();
        popularitySVDThread.join();
        averageRatingSVDThread.join();


        Iterator<String> iterator = rmTestSVD.returnMap.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            Map<String,Map<Double,Double>> map= new HashMap<>();
            map.put("RM",rmTestSVD.returnMap.get(key));
            map.put("Popularity", popularitySVD.returnMap.get(key));
            map.put("AverageRating",averageRatingSVD.returnMap.get(key));
            XYChartModel model=new XYChartModel();
            model.title="SVD Movielens";
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
        Thread.sleep(10000000);
    }

}
