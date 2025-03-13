import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationFile {
    private Map<String, String> configMap;

    public ConfigurationFile() {
        this.configMap = new HashMap<>();
        createHashMapConfig();
    }

    private void createHashMapConfig() {
        String configPath = "Config.txt";
        Map<String, String> map = new HashMap<>();
        BufferedReader fileReader = null;
        try{
            File file = new File(configPath);
            fileReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] pair = line.split(":",2);
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
            this.configMap = map;
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
    }

    public Map<String, String> getConfigMap() {
        return configMap;
    }

    public static int findIndexByKey(String keyName) {
        ConfigurationFile configFile = new ConfigurationFile();
        List<Map.Entry<String, String>> entryList = new ArrayList<>(configFile.getConfigMap().entrySet());
        for (int i = 0; i < entryList.size(); i++) {
            String cleanedKey = entryList.get(i).getKey().replace("\"", "").trim();
            if (cleanedKey.equalsIgnoreCase(keyName)) {
                return i;
            }
        }
        return -1;
    }

    public static String getValueByKey(String keyName) {
        ConfigurationFile configFile = new ConfigurationFile();
        List<Map.Entry<String, String>> entryList = new ArrayList<>(configFile.getConfigMap().entrySet());
        int index = findIndexByKey(keyName);
        if (index != -1) {
            return entryList.get(index).getValue();
        }
        return "Key not found";
    }

    public static void main(String[] args) {
        ConfigurationFile configFile = new ConfigurationFile();

        System.out.println("ConfigMap Content: " + configFile.getConfigMap());

        String apiKey = getValueByKey("API_KEY");
        String model = getValueByKey("MODEL");
        String completionUrl = getValueByKey("COMPLETIONS_URL");

        System.out.println("API Key: [" + apiKey + "]");
        System.out.println("Model: [" + model + "]");
        System.out.println("Completion URL: [" + completionUrl + "]");
    }



}
