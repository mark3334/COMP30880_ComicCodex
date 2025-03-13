import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
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

    public static void main(String[] args) {
        ConfigurationFile configFile = new ConfigurationFile();
        for (Map.Entry<String, String> entry : configFile.getConfigMap().entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}
