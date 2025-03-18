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
        createHashMapConfig();
    }

    private void createHashMapConfig() {
        File current = new File(System.getProperty("user.dir")); // Current working directory
        // Traverse up to find "COMP30880_ComicCodex"
        while (current != null && !current.getName().equals("COMP30880_ComicCodex")) {
            current = current.getParentFile();
        }
        if (current == null) {
            System.out.println("COMP30880_ComicCodex directory not found!");
        }
        this.configMap = new HashMap<>();
        BufferedReader fileReader = null;
        try{
            File file = new File(current, "config.txt");
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
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, String> getConfigMap() {
        return configMap;
    }

    public  int findIndexByKey(String keyName) {
        List<Map.Entry<String, String>> entryList = new ArrayList<>(this.getConfigMap().entrySet());
        for (int i = 0; i < entryList.size(); i++) {
            String cleanedKey = entryList.get(i).getKey().replace("\"", "").trim();
            if (cleanedKey.equalsIgnoreCase(keyName)) {
                return i;
            }
        }
        return -1;
    }

    public String getValueByKey(String keyName) {
        List<Map.Entry<String, String>> entryList = new ArrayList<>(this.getConfigMap().entrySet());
        int index = findIndexByKey(keyName);
        if (index != -1) {
            return entryList.get(index).getValue();
        }
        return "Key not found";
    }

    public static void main(String[] args) {
        ConfigurationFile configFile = new ConfigurationFile();

        System.out.println("ConfigMap Content: " + configFile.getConfigMap());

        String apiKey = configFile.getValueByKey("API_KEY");
        String model = configFile.getValueByKey("MODEL");
        String completionUrl = configFile.getValueByKey("COMPLETIONS_URL");

        System.out.println("API Key: [" + apiKey + "]");
        System.out.println("Model: [" + model + "]");
        System.out.println("Completion URL: [" + completionUrl + "]");
    }



}
