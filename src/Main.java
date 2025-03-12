import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Map<String, String> configMap = createHashMapConfig();

        // Printing the HashMap
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static Map<String, String> createHashMapConfig() {
        String configPath = "Config.txt";
        Map<String, String> map = new HashMap<>();
        BufferedReader fileReader = null;
        try{
            File file = new File(configPath);
            fileReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] pair = line.split(":");
                if (pair.length == 2) {
                    String key = pair[0];
                    String value = pair[1];
                    if (!key.isEmpty() && !value.isEmpty())
                        map.put(key, value);
                }
                else{
                    System.out.println("Mistake in config line");
                }


        }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}