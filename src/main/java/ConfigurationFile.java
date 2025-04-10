import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
        String[] coreKeys = {"TARGET_LANGUAGE", "API_KEY", "MODEL", "COMPLETIONS_URL", "MAX_TOKENS_PER_PROMPT"};
        for(String key : coreKeys){
            if(!this.configMap.containsKey(key)) System.out.println("Error: Configuration file does not contain all the correct keys");
        }
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

    public static int getTokenLimit() {
        ConfigurationFile c = ConfigurationFile.getInstance();
        String key = "MAX_TOKENS_PER_PROMPT";
        String limit = c.configMap.get(key);
        return Integer.parseInt(limit);
    }

    public static String getTargetLanguage() {
        ConfigurationFile c = ConfigurationFile.getInstance();
        String key = "TARGET_LANGUAGE";
        return c.configMap.get(key);
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
