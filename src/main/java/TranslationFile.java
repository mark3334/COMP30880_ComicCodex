import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationFile {
    private final File file;
    private static TranslationFile instance;
    private Map<String, String> translations;

    private TranslationFile(File file) {
        this.file = file;
        this.translations = new HashMap<>();
        try {
            if (file.createNewFile()) {
                System.out.println("Translation file created: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error creating translation file: " + e.getMessage());
        }
        this.loadTranslationsFile();
    }

    public static synchronized TranslationFile getInstance() {
        if (instance == null) {
            ConfigurationFile configFile = ConfigurationFile.getInstance();
            File root = Helper.getRootDirectory();
            String filepath = configFile.getValueByKey("TRANSLATIONS_PATH");
            filepath = new File(root, filepath).getAbsolutePath();
            filepath += ("/" + configFile.getValueByKey("SOURCE_LANGUAGE") + "_" + configFile.getValueByKey("TARGET_LANGUAGE"));
            System.out.println("Filepath : " + filepath);
            File file = new File(filepath);
            instance = new TranslationFile(file);
        }
        return instance;
    }

    public static synchronized TranslationFile getInstance(File testFile) {
        instance = new TranslationFile(testFile); // override singleton for test
        return instance;
    }

    public void loadTranslationsFile() { //Loads Translations from the File into Memory (Map).
        boolean append = true;
        FileParser.fileToHashmap(this.file, this.translations, append);
    }

    public void writeTranslationMapping(String source, String target) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file, true))) {
            w.write(source + " : " + target);
            w.newLine();
        } catch (IOException e) {
            System.err.println("Error writing translation file: " + e.getMessage());
        }
    }

    public Map<String, String> getTranslations(){return this.translations;}

    public static List<String> getAllPhrasesToTranslate(){
        VignetteManager vignetteManager = new VignetteManager();
        vignetteManager.printAll();
        List<VignetteSchema> vignetteSchemas = vignetteManager.getVignetteSchemas();
        List<String> phrases =  new ArrayList<>();;
        for(VignetteSchema schema : vignetteSchemas){
            phrases.addAll(schema.getLeftText());
            phrases.addAll(schema.getCombinedText());
        }
        return phrases;

    }

    public void translateAllPhrases(List<String> phrases){
        //TODO
        String prompt;
        for(String phrase : phrases){
            prompt = trimRemoveQuotesPhrase(phrase);
            this.translate(prompt);
        }
    }

    public static String trimRemoveQuotesPhrase(String phrase){
        if (phrase == null) {
            return null;
        }
        return phrase.trim().replace("\"", "");
    }

    public String translate(String text) {
        if (this.translations.containsKey(text)) {
            //System.out.println("ALREADY IN MAP: " + text);
            return translations.get(text);
        }

        if(text.isEmpty()) { //if the input text is empty
            return "";
        }

        //otherwise if it doesn't exist in MAP then we ask GPT.
        String targetLanguage = Helper.getTargetLanguage();
        String prompt = "Please translate the following English text to " + targetLanguage + ":\n" + text + "Please note, you only need to reply with the translated text. \" +\n" +
                "                \"For example, if I ask you Hello, you should simply reply Bonjour.";
        String translation = OpenAIClient.getInstance().translate(prompt);


        if (translation.contains("429")) {
            System.err.println("Rate limit hit. Retrying in 60 seconds...");
            try {
                Thread.sleep(120_000); //120 seconds wait
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); //restore interrupt status
            }

            //Retry the request
            translation = OpenAIClient.getInstance().translate(prompt);
        }


        //If the error starts off with error, then we dont want to include it in the TranslationFile
        if (translation.startsWith("Error:") || translation.equals("Key not Found")) {
            System.err.println("Translation failed: " + translation);
            return translation; // Just return the error, do not save it
        }

        //Saves new Translation.
        this.translations.put(text, translation);
        this.writeTranslationMapping(text, translation);
        return translation;
    }

    public static void main(String[] args) {
        TranslationFile t = TranslationFile.getInstance();

        //Now get all the phrases to be translated from the VignetteManager
        List<String> phrases = TranslationFile.getAllPhrasesToTranslate();
        t.translateAllPhrases(phrases);

    }

}