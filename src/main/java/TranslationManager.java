import java.util.List;
import java.util.Map;

public class TranslationManager {
    private TranslationFile translationFile;
    private final OpenAIClient client;


    public TranslationManager(String filePath, OpenAIClient client) {
        this.translationFile = new TranslationFile(filePath);
        this.client = new OpenAIClient();
    }

    //TODO
    public void readText(List<sourceText> textEntries) {

    }

    public String translate(String text) {
        Map<String, String> translations = translationFile.loadTranslationsFile(); // Load existing translations

        if (translations.containsKey(text)) {//if translation exists
            return translations.get(text);
        }

        //otherwise if it down exist, we need to ask OPENAI. INCOMPLETE***
        String targetLanguage = translationFile.getTargetLanguage();
        String prompt;
        String translation = "temp";

        //Save new translation
        translationFile.addTranslationMapping(text, translation);
        return translation;
    }
}
