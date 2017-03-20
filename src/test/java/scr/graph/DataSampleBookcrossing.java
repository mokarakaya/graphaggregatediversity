package scr.graph;

import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.common.TasteException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by mokarakaya on 16.04.2016.
 */
public class DataSampleBookcrossing {
    static int count=0;
    static int itemCount=0;
    static int userCount=0;
    static Map<String,Integer> itemMap= new HashMap<String,Integer>();
    static Map<String,Integer> userMap= new HashMap<String,Integer>();
    public static void main(String [] args) throws IOException, TasteException, InterruptedException {
        String fileName = "C:/javafx/data/BookcrossingRaw/BookcrossingTheshhold.data";
        //String fileName = "C:/javafx/data/Movielens.data";
        sample(fileName);
        System.out.println(userCount+";"+itemCount);
    }
    public static void sample(String fileName) throws IOException {

        final File fileWrite = new File("C:/javafx/data/BookcrossingRaw/Bookcrossing.data");
        Random random= new Random();
        //read file into stream, try-with-resources
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.endsWith("255") || !line.startsWith("\"User")) {
                    if (count >= 100000) {
                        return;
                    }
                    line = line.replace("\"", "");
                    line = line.trim();
                    String[] split = line.split(",");
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
                    long rating = Long.parseLong(split[2]);


                    String newline = convertedUserId + "," + convertedItemId + "," + rating;
                    System.out.println(line + ";" + newline);
                    count++;
                        try {
                            FileUtils.writeStringToFile(fileWrite, newline + "\n", "UTF-8", true);

                        } catch (MalformedInputException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
