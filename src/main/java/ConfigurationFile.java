import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ConfigurationFile {
    private static ConfigurationFile instance;
    private final Map<String, String> configMap;

    private ConfigurationFile() {
        File file = FileParser.getConfigFile();
        boolean append = false;
        this.configMap = new HashMap<>();
        FileParser.fileToHashmap(file, this.configMap, append);
        String[] coreKeys = {"TARGET_LANGUAGE", "API_KEY", "MODEL", "COMPLETIONS_URL", "MAX_TOKENS_PER_PROMPT"};
        //System.out.println(configMap);
        for(String key : coreKeys){
            assert(this.configMap.containsKey("\""+key+"\"")) : "Config.txt is missing necessary keys";
            if(!this.configMap.containsKey("\""+key+"\"")) System.out.println("Error: Configuration file does not contain all the correct keys");
        }
    }


    private ConfigurationFile(File file) {
        boolean append = false;
        this.configMap = new HashMap<>();
        FileParser.fileToHashmap(file, this.configMap, append);
    }

    public void updateLanguage(){
        Scanner scanner = new Scanner(System.in);

        File configFile = FileParser.getConfigFile();
        String newLanguage;

        while (true) {

            System.out.println("The target language is: " + configMap.get("TARGET_LANGUAGE"));
            System.out.println("Please enter the new target language: ");

            //Get the new target_language and unified format.
            newLanguage = scanner.nextLine().trim();
            newLanguage = newLanguage.toLowerCase();
            newLanguage = newLanguage.substring(0, 1).toUpperCase() + newLanguage.substring(1);

            // Check if the newTarget_language is equal to Source_Language
            if (newLanguage.equalsIgnoreCase(configMap.get("SOURCE_LANGUAGE"))) {
                System.out.println("Error: the Target Language could not be the same as Source Language");
            } else {
                break;
            }
        }
        configMap.put("TARGET_LANGUAGE", newLanguage);

        // Write HashMap to file.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                String line = FileParser.generateMapLine(entry.getKey(), entry.getValue());
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Target language has been updated to " + newLanguage);
        } catch (IOException e) {
            System.out.println("Wrong: " + e.getMessage());
        }
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
        return this.configMap.getOrDefault("\""+keyName+"\"", "Key not found").replaceAll("\"", "");
    }
    public static String get(String key) {
        return getInstance().getValueByKey(key);
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
    public static List<String> getLessonSchedule() {
        ConfigurationFile c = ConfigurationFile.getInstance();
        String key = "LESSON_SCHEDULE";
        String value = c.configMap.get(key);
        // Clean the brackets and quotes and spaces
        value = value.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace(" ", "");
        List<String> schedule = new ArrayList<String>(Arrays.asList(value.split(",")));
        return schedule;
    }

    public static void main(String[] args) {
        /*
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
        */
        ConfigurationFile c = ConfigurationFile.getInstance();
        c.updateLanguage();
        System.out.println(ConfigurationFile.getTargetLanguage());
        List<String> s = getLessonSchedule();
        for(String i : s) System.out.println(i);
    }

}
