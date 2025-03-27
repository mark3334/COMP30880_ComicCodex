import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationFile {
    private static ConfigurationFile instance;
    private  Map<String, String> configMap;

    private ConfigurationFile() {
        createHashMapConfig();
    }
    public static synchronized ConfigurationFile getInstance() {
        if (instance == null) {
            instance = new ConfigurationFile();
        }
        return instance;
    }
    private void createHashMapConfig() {
        File root = helper.getRootDirectory();
        this.configMap = new HashMap<>();
        BufferedReader fileReader = null;
        try{
            File file = new File(root, "Resources/Config.txt");
            fileReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] pair = line.split(":",2);
                if (pair.length == 2) {
                    String key = pair[0];
                    String value = pair[1].trim().replaceAll("\"", "").trim();
                    if (!key.isEmpty() && !value.isEmpty())
                        this.configMap.put(key, value);
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
                    System.out.println("Filereader couldn't be closed");
                    e.printStackTrace();
                }
            }
        }
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

        String apiKey = configFile.getValueByKey("API_KEY");
        String model = configFile.getValueByKey("MODEL");
        String completionUrl = configFile.getValueByKey("COMPLETIONS_URL");

        System.out.println("API Key: [" + apiKey + "]");
        System.out.println("Model: [" + model + "]");
        System.out.println("Completion URL: [" + completionUrl + "]");
    }

}
