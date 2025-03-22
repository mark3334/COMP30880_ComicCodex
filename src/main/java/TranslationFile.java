import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TranslationFile {
    private final File file;

    public TranslationFile(String filePath) {
        this.file = new File(filePath);
        try {
            if (file.createNewFile()) { //Checks if the already exists.
                System.out.println("Translation file created: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error creating translation file: " + e.getMessage());
        }
    }


    //TODO
    public Map<String, String> loadTranslationsFile() { //Loads Translations from the File into Memory (Map).
        Map<String, String> translations = new HashMap<String, String>();


        return translations;
    }

    //TODO
    public void addTranslationMapping(String source, String target) { //Appends this new mapping into the TranslationsFile.

    }

    public static String getTargetLanguage() {
        ConfigurationFile config = new ConfigurationFile();
        return config.getValueByKey("language");
    }
}