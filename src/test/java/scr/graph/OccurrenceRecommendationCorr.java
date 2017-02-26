package scr.graph;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.AllUnknownItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.svd.ParallelSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import scr.draw.XYChartCreator;
import scr.draw.XYChartModel;
import scr.ratingManipulation.proposed.ParallelSGDGraphFactorizer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mokarakaya on 19.02.2017.
 */
public class OccurrenceRecommendationCorr {

    public static void main(String [] args) throws IOException, TasteException, InterruptedException {
        OccurrenceRecommendationCorr graph= new OccurrenceRecommendationCorr();
        //graph.run("Movielens");
        //graph.run("Yahoo Music");
        graph.run("Bookcrossing");

    }

    private void run(String data) throws TasteException, IOException, InterruptedException {
        DataModel dataModel= new FileDataModel(new File("C:/javafx/data/"+ data+".data"));
        LongPrimitiveIterator itemIDs = dataModel.getItemIDs();
        Map<Long,Double> numberOfRatings=new HashMap<>();
        while(itemIDs.hasNext()){
            long itemID = itemIDs.nextLong();
            double numUsersWithPreferenceFor = dataModel.getNumUsersWithPreferenceFor(itemID);
            numberOfRatings.put(itemID,numUsersWithPreferenceFor);
        }
        Map<Long,Double> sortedNumberOfRatings= numberOfRatings.entrySet().stream().sorted((e1,e2)->
                e1.getValue().compareTo(e2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        int numFeatures=100;
        float lambda=new Float( 0.02);
        int numEpochs=20;
        ParallelSGDFactorizer parallelSGDFactorizer = new ParallelSGDFactorizer(dataModel, numFeatures, lambda, numEpochs);
        SVDRecommender svdRecommender = new SVDRecommender(dataModel, parallelSGDFactorizer, new AllUnknownItemsCandidateItemsStrategy());
        LongPrimitiveIterator userIDs = dataModel.getUserIDs();
        Map<Long,Double> recommendation=new HashMap<>();
        Random random= new Random();
        while (userIDs.hasNext()){
            Long userId = userIDs.next();
            if(random.nextInt(10)<8) continue;
            List<RecommendedItem> recommend = svdRecommender.recommend(userId, 20);
            for(RecommendedItem recommendedItem:recommend){
                long itemId = recommendedItem.getItemID();
                recommendation.putIfAbsent(itemId,0.d);
                recommendation.put(itemId,recommendation.get(itemId)+1);
            }
        }

        double numberOfRatingsCount=1;
        int itemCount=0;
        double recommendationCount=0;
        Map<Double,Double> resultMap=new HashMap<>();
        for(long itemId:sortedNumberOfRatings.keySet()){
            if(sortedNumberOfRatings.get(itemId)>numberOfRatingsCount){
                resultMap.put(numberOfRatingsCount,recommendationCount/itemCount);
                numberOfRatingsCount++;
                itemCount=1;
                Double aDouble = recommendation.get(itemId);
                recommendationCount+= aDouble!=null? aDouble:0;
            }else{
                itemCount++;
                Double aDouble = recommendation.get(itemId);
                recommendationCount+= aDouble!=null? aDouble:0;
            }
        }

        generateGraph(resultMap,data);

    }



    private void generateGraph(Map<Double, Double> map,String data) throws IOException, InterruptedException {
        XYChartModel model= new XYChartModel();
        model.title="# of Ratings VS Average Recommendation Count";
        Map<String,Map<Double,Double>>  series= new HashMap<>();
        series.put(data,map);
        model.series= series;
        model.xAxisLabel="# of Ratings ";
        model.yAxisLabel="Average Recommendation Count";
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

}
