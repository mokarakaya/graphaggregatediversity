package scr;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.apache.mahout.cf.taste.common.TasteException;
import scr.draw.XYChartCreator;
import scr.draw.XYChartModel;
import scr.save.GraphData;
import scr.save.GraphItemData;
import scr.save.XYChartDataSaver;

import java.io.IOException;
import java.util.*;

/**
 * Created by mokarakaya on 11.07.2015.
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
            Map<String,Map<Double,Double>> map= new HashMap<>();
            GraphData graphData= new GraphData();
            graphData.title=title;
            graphData.xAxisLabel="precision";
            graphData.yAxisLabel=key;
            List<GraphItemData> graphItemDataList= new ArrayList<>();
            for(AbstractTest test: tests){
                map.put(test.displayName, test.returnMap.get(key));
                GraphItemData graphItemData= new GraphItemData();
                graphItemData.displayName=test.displayName;
                graphItemData.xAxis= test.returnMap.get(key).keySet();
                graphItemData.yAxis= test.returnMap.get(key).values();
                graphItemDataList.add(graphItemData);
            }
            graphData.graphItemDataList=graphItemDataList;
            dataSaver.saveData(graphData);
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
