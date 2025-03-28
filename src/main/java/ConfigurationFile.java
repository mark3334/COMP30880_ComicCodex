import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationFile {
    private static ConfigurationFile instance;
    private final Map<String, String> configMap;

    private ConfigurationFile() {
        File root = Helper.getRootDirectory();
        File file = new File(root, "Resources/Config.txt");
        boolean append = false;
        this.configMap = new HashMap<>();
        FileParser.fileToHashmap(file, this.configMap, append);
    }

    private ConfigurationFile(File file) {
        boolean append = false;
        this.configMap = new HashMap<>();
        FileParser.fileToHashmap(file, this.configMap, append);
    }

    public static synchronized ConfigurationFile getInstance() {
        if (instance == null) {
            instance = new ConfigurationFile();
        }
        return instance;
    }

    public static synchronized ConfigurationFile getInstance(File file) {
        if (instance == null) {
            instance = new ConfigurationFile(file);
        }
        return instance;
    }

    public Map<String, String> getConfigMap() {
        return configMap;
    }


    public String getValueByKey(String keyName) {
        return this.configMap.getOrDefault(keyName, "Key not found");
    }

    public static void main(String[] args) {
        ConfigurationFile configFile = ConfigurationFile.getInstance();

        System.out.println("ConfigMap Content: " + configFile.getConfigMap());
        System.out.println("Available keys: " + configFile.getConfigMap().keySet());

        String apiKey = configFile.getValueByKey("API_KEY");
        String model = configFile.getValueByKey("MODEL");
        String completionUrl = configFile.getValueByKey("COMPLETIONS_URL");
        String language = configFile.getValueByKey("TARGET_LANGUAGE");
        System.out.println("API Key: [" + apiKey + "]");
        System.out.println("Model: [" + model + "]");
        System.out.println("Completion URL: [" + completionUrl + "]");
        System.out.println("Target Language:  [" + language + "]");

    }

}
