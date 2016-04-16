package scr.graph;

import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.common.TasteException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by p.bell on 16.04.2016.
 */
public class DataSample {
    static int count=0;
    static int itemCount=0;
    static Map<String,Integer> itemMap= new HashMap<String,Integer>();
    public static void main(String [] args) throws IOException, TasteException, InterruptedException {
        String fileName = "C:/javafx/data/Webscope_R1/ydata-ymusic-user-artist-ratings-v1_0.txt";
        //String fileName = "C:/javafx/data/Movielens.data";
        sample(fileName);
    }
    public static void sample(String fileName) throws IOException {

        final File fileWrite = new File("C:/javafx/data/Webscope_R1/Yahoo Music.data");
        Random random= new Random();
        //read file into stream, try-with-resources

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            stream.forEach(line -> {
                if (!line.endsWith("255")) {
                    if(count>=300000){
                        return;
                    }
                    line=line.replace("\t", ",");
                    String[] split = line.split(",");
                    String itemId = split[1];
                    Integer convertedItemId = itemMap.get(itemId);
                    if(convertedItemId==null){
                        itemMap.put(itemId,itemCount);
                        convertedItemId=itemCount;
                        itemCount++;
                    }
                    long rating=Long.parseLong(split[2]);
                    rating=rating/20;
                    if(rating==0){
                        rating=1;
                    }
                    line=split[0]+","+convertedItemId+","+rating;
                    try {
                        FileUtils.writeStringToFile(fileWrite, line.replace("\t", ",") + "\n", "UTF-8", true);
                        count++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
}
