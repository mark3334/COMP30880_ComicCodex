import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        ConfigurationFile configFile = new ConfigurationFile();
        Map<String, String> configMap = configFile.getConfigMap();
        // Printing the HashMap
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }


}