package scr.graph;

import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.common.TasteException;

import java.io.*;
import java.nio.charset.MalformedInputException;
import java.util.*;

/**
 * Created by mokarakaya on 16.04.2016.
 */
public class DataSampleBookcrossingThreshold {
    static int count=0;
    static int itemCount=0;
    static int userCount=0;
    static Map<String,Integer> itemMap= new HashMap<String,Integer>();
    static Map<String,Integer> userMap= new HashMap<String,Integer>();
    static Map<Integer,Integer> itemCountMap= new HashMap<Integer,Integer>();
    static Map<Integer,Integer> userCountMap= new HashMap<Integer,Integer>();
    static List<Rating> ratingList=new ArrayList<>();
    public static void main(String [] args) throws IOException, TasteException, InterruptedException {
        String fileName = "C:/javafx/data/BookcrossingRaw/BX-Book-Ratings.csv";
        //String fileName = "C:/javafx/data/Movielens.data";
        sample(fileName);
        saveFile();
        System.out.println(userCount+";"+itemCount);
    }

    private static void saveFile() {
        final File fileWrite = new File("C:/javafx/data/BookcrossingRaw/BookcrossingTheshhold.data");
        try {
            for(Rating rating: ratingList) {
                if(itemCountMap.get(rating.itemId)>1) {
                    String newline= rating.userId + "," + rating.itemId + "," + rating.rating;
                    FileUtils.writeStringToFile(fileWrite, newline + "\n", "UTF-8", true);
                }
            }

        } catch (MalformedInputException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sample(String fileName) throws IOException {


        Random random= new Random();
        //read file into stream, try-with-resources
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.endsWith("255") || !line.startsWith("\"User")) {
                    if (count >= 400000) {
                        return;
                    }
                    line = line.replace("\"", "");
                    line = line.trim();
                    String[] split = line.split(";");
                    if(split[2].startsWith("Book")) continue;
                    String userId = split[0];
                    Integer convertedUserId = userMap.get(userId);
                    if (convertedUserId == null) {
                        userMap.put(userId, userCount);
                        convertedUserId = userCount;
                        userCount++;
                    }

                    String itemId = split[1];

                    Integer convertedItemId = itemMap.get(itemId);
                    if (convertedItemId == null) {
                        itemMap.put(itemId, itemCount);
                        convertedItemId = itemCount;
                        itemCount++;
                    }
                    itemCountMap.putIfAbsent(convertedItemId,0);
                    itemCountMap.put(convertedItemId,itemCountMap.get(convertedItemId)+1);
                    long rating = Long.parseLong(split[2]);

                    /*if (rating == 0) {
                        rating = 1;
                    } else {
                        rating = rating / 2;
                    }*/
                    String newline = convertedUserId + "," + convertedItemId + "," + rating;
                    Rating r= new Rating();
                    r.userId= convertedUserId;
                    r.itemId= convertedItemId;
                    r.rating=rating;
                    ratingList.add(r);
                    System.out.println(line + ";" + newline);
                    count++;

                }

            }

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
class Rating{
    int itemId;
    int userId;
    long rating;
}