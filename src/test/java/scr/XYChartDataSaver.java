package scr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mokarakaya on 21.03.2016.
 */
public class XYChartDataSaver {

    public boolean saveData(List<AbstractTest> tests,String key,String title){
        try {
            //save results to file
            List<String> saveList=new ArrayList<>();
            int longestListSize=getLongestListSize(tests,key);
            for(AbstractTest test: tests){
                Map<Double, Double> result = test.returnMap.get(key);
                Object[] entries = result.entrySet().toArray();
                if(entries.length>saveList.size()-1){
                    saveList.add(0,test.displayName+";"+test.displayName);
                }else{
                    saveList.add(0, saveList.get(0) +";"+ test.displayName + ";" + test.displayName);
                    saveList.remove(1);
                }

                for(int i=0;i<longestListSize;i++){
                    if(entries.length <= i ){
                        if(i+1>=saveList.size()){
                            saveList.add(i+1, ";;");
                        }else{
                            saveList.add(i+1, saveList.get(i+1)+";;");
                            saveList.remove(i+2);
                        }
                    }else{
                        Object entry = entries[i];
                        if(entries.length>saveList.size()-1){
                            saveList.add(i+1, entry.toString().replace("=",";"));
                        }else{
                            saveList.add(i+1,saveList.get(i+1)+";"+entry.toString().replace("=", ";"));
                            saveList.remove(i+2);
                        }
                    }
                }
            }
            File file = new File("c:/javafx/raw/"+title+key+".txt");
            Files.write(file.toPath(), saveList, Charset.forName("UTF-8"));
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
