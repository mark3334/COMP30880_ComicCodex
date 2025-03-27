import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationFile {
    private final File file;
    private Map<String, String> translations;

    public TranslationFile(File file) {
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
        for(String phrase : phrases){
            this.translate(phrase);
        }
        return;
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
        String translation = OpenAIClient.translate(prompt);


        //If the error starts off with error, then we dont want to include it in the TranslationFile
        if (translation.startsWith("Error:")) {
            System.err.println("Translation failed: " + translation);
            return translation; // Just return the error, do not save it
        }

        //Saves new Translation.
        this.translations.put(text, translation);
        this.writeTranslationMapping(text, translation);
        return translation;
    }

    public static void main(String[] args) {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        String filepath = configFile.getValueByKey("TRANSLATIONS_PATH");
        filepath += ("/" + configFile.getValueByKey("SOURCE_LANGUAGE") + "_" + configFile.getValueByKey("TARGET_LANGUAGE"));
        File file = new File(filepath);
        TranslationFile t = new TranslationFile(file);

        //Now get all the phrases to be translated from the VignetteManager
        List<String> phrases = TranslationFile.getAllPhrasesToTranslate();
        t.translateAllPhrases(phrases);

    }

}