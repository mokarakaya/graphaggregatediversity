package scr.graph;

import org.apache.hadoop.util.hash.Hash;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.AllUnknownItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.svd.ParallelSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mokarakaya on 19.02.2017.
 */
public class OccurrenceRecommendationCorr {

    private static final int RECOMMENDATION_LIMIT=10000;
    private static final int BUCKET_RANGE=100;

    public static void main(String [] args) throws IOException, TasteException, InterruptedException {
        OccurrenceRecommendationCorr graph= new OccurrenceRecommendationCorr();
        List<String> dataList= new ArrayList<>();
        //dataList.add("Movielens");
        dataList.add("Yahoo Music");
        //dataList.add("Bookcrossing");
        graph.run(dataList);
    }

    private void run(List<String> dataList) throws TasteException, IOException, InterruptedException {
        List<GraphItemData> graphItemDataList= new ArrayList<>();

        for(String data: dataList){
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
            int RECOMMENDATION_LIMIT_CURRENT=0;
            while (RECOMMENDATION_LIMIT_CURRENT<RECOMMENDATION_LIMIT){
                Long userId = userIDs.next();
                List<RecommendedItem> recommend;
                try {
                    recommend = svdRecommender.recommend(userId, 20);
                }catch (TasteException exception){
                    continue;
                }

                for(RecommendedItem recommendedItem:recommend){
                    long itemId = recommendedItem.getItemID();
                    recommendation.putIfAbsent(itemId,0.d);
                    recommendation.put(itemId,recommendation.get(itemId)+1);
                    RECOMMENDATION_LIMIT_CURRENT++;
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

            Map<String,Double>printData = new HashMap<>();
            Map<Integer, List<Double>> itemsInBuckets= new HashMap<>();

            Iterator<Double> iterator = resultMap.keySet().iterator();

            while(iterator.hasNext()){
                Double key = iterator.next();
                Integer bucketId = key.intValue() / BUCKET_RANGE;
                itemsInBuckets.putIfAbsent(bucketId,new ArrayList<>());
                itemsInBuckets.get(bucketId).add(resultMap.get(key));
            }

            Iterator<Integer> iterator1 = itemsInBuckets.keySet().iterator();
            while (iterator1.hasNext()){
                Integer bucketId = iterator1.next();
                OptionalDouble average = itemsInBuckets.get(bucketId).stream().mapToDouble(a -> a).average();
                printData.put(bucketId*BUCKET_RANGE +"-"+(bucketId*BUCKET_RANGE+BUCKET_RANGE),average.getAsDouble());
            }


            Map<String,Double> printDataSorted = new LinkedHashMap<>();
            printData.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> printDataSorted.put(x.getKey(), x.getValue()));
            GraphItemData graphItemData= new GraphItemData();
            graphItemData.xAxis=printDataSorted;
            graphItemData.displayName=data;
            graphItemDataList.add(graphItemData);
        }


        GraphData graphData= new GraphData();
        graphData.title="# of Ratings VS Average Recommendation Count";
        graphData.xAxisLabel="# of Ratings";
        graphData.yAxisLabel="Average Recommendation Count";
        graphData.graphItemDataList=graphItemDataList;
        saveData(graphData);

    }

    public boolean saveData(GraphData graphData){
        try {
            String fileName="c:/javafx/raw/"+graphData.title+"_"+graphData.xAxisLabel+"_"+graphData.yAxisLabel+".json";
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(fileName), graphData);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    class GraphItemData {

        public String displayName;
        public Map<String,Double> xAxis;
        public List<Double> yAxis;
    }
    class GraphData {

        public String xAxisLabel;
        public String yAxisLabel;
        public String title;
        public List<GraphItemData> graphItemDataList;
    }
}
