package scr.graph;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;
import scr.baseRecommender.generic.SVDBaseRecommender;
import scr.draw.XYChartCreator;
import scr.draw.XYChartModel;
import scr.runner.Runner;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by mokarakaya on 06.10.2015.
 */
public class AggregateGraph {
/*
    public static void main(String [] args) throws IOException, TasteException, InterruptedException {
        AggregateGraph aggregateGraph= new AggregateGraph();
        aggregateGraph.run();

    }

    private  void run() throws IOException, TasteException, InterruptedException {
        DataModel dataModel= new FileDataModel(new File("C:/javafx/data/"+ Runner.DATA+".data"));
        Map<Double,List<Double>> tempList= new HashMap<>();
        LongPrimitiveIterator itemIDs = dataModel.getItemIDs();
        while(itemIDs.hasNext()){
            long itemID = itemIDs.nextLong();
            double numUsersWithPreferenceFor = dataModel.getNumUsersWithPreferenceFor(itemID);
            if(!tempList.containsKey(numUsersWithPreferenceFor)){
                tempList.put(numUsersWithPreferenceFor,new ArrayList<>());
            }
            tempList.get(numUsersWithPreferenceFor).add(getAvgOfCoocs(itemID, dataModel));
        }
        Map<Double,Double> map=sumCoocs(tempList);
        generateGraph(map);
    }

    private void generateGraph(Map<Double, Double> map) throws IOException, InterruptedException {
        XYChartModel model= new XYChartModel();
        model.title=Runner.DATA;
        Map<String,Map<Double,Double>>  series= new HashMap<>();
        series.put(Runner.DATA,map);
        model.series= series;
        model.xAxisLabel="# of ratings";
        model.yAxisLabel="average # of ratings of coocs";
        // Because we need to init the JavaFX toolkit - which usually Application.launch does
        // I'm not sure if this way of launching has any effect on anything
        new JFXPanel();
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    // Your class that extends Application
                    try {
                        XYChartCreator xyChartCreator = new XYChartCreator(model);
                        xyChartCreator.start(new Stage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.sleep(10000000);
    }

    private Map<Double, Double> sumCoocs(Map<Double, List<Double>> tempList) {
        Map<Double, Double> map=new HashMap<>();
        Iterator<Double> iterator = tempList.keySet().iterator();
        while(iterator.hasNext()){
            Double next = iterator.next();
            double sum=0;
            List<Double> doubles = tempList.get(next);
            for(Double value:doubles){
                sum+=value;
            }
            map.put(next,sum/doubles.size());
        }
        return map;
    }

    private Double getAvgOfCoocs(long itemID, DataModel dataModel) throws TasteException {
        Map<Long, Integer> cooccurences = getCooccurences(itemID, dataModel);
        Iterator<Long> iterator = cooccurences.keySet().iterator();
        double coocCount=0;
        while(iterator.hasNext()){
            Long next = iterator.next();
            coocCount+=dataModel.getNumUsersWithPreferenceFor(next);
        }
        return coocCount/cooccurences.size();
    }
    private Map<Long, Integer> getCooccurences(long itemID, DataModel dataModel) throws TasteException {
        Map<Long,Integer> cooccurence=new HashMap<>();
        PreferenceArray preferencesForItem = dataModel.getPreferencesForItem(itemID);
        for(Preference preference:preferencesForItem){
            long userID = preference.getUserID();
            PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
            for(Preference userPreference: preferencesFromUser){
                if(cooccurence.get(userPreference.getItemID())==null){
                    cooccurence.put(userPreference.getItemID(),1);
                }else{
                    cooccurence.put(userPreference.getItemID(),cooccurence.get(userPreference.getItemID())+1);
                }
            }
        }
        return cooccurence;
    }
*/
}
