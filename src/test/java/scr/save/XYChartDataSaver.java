package scr.save;

import org.codehaus.jackson.map.ObjectMapper;
import scr.AbstractTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * saves data as json file.
 *
 * [{"label":"plot A", "xAxis" : [1, 2, 3, 4], "yAxis" : [1, 2, 3, 4]},
 * {"label":"plot B", "xAxis" : [2, 4, 6, 8], "yAxis" : [1, 2, 3, 4]}]
 *
 * Created by mokarakaya on 21.03.2016.
 */
public class XYChartDataSaver {

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

    private int getLongestListSize(List<AbstractTest> tests, String key) {
        int longestListSize=0;
        for(AbstractTest test: tests){
            if(test.returnMap.get(key).size()>longestListSize){
                longestListSize=test.returnMap.get(key).size();
            }
        }
        return longestListSize;
    }


}
