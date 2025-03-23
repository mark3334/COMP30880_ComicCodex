import java.util.List;
import java.util.Map;

public class TranslationManager {
    private TranslationFile translationFile;
    private final OpenAIClient client;


    public TranslationManager(String filePath) {
        this.translationFile = new TranslationFile(filePath);
        this.client = new OpenAIClient();
    }

    public void readText(List<sourceText> textEntries) {
        for (sourceText entry : textEntries) {
            String combined = String.valueOf(entry.getCombinedText()); // Assuming this returns the full text
            String translated = translate(combined);

            System.out.println("Original: " + combined);
            System.out.println("Translate: " + translated);
            System.out.println("--------------------------");
        }
    }

    public String translate(String text) {
        Map<String, String> translations = translationFile.loadTranslationsFile(); // Load existing translations

        if (translations.containsKey(text)) {//if translation exists
            //System.out.println("ALREADY IN MAP: " + text);
            return translations.get(text);
        }

        //otherwise if it dont exist in MAP then we ask GPT.
        String targetLanguage = helper.getTargetLanguage();
        String prompt = "Please translate the following English text to " + targetLanguage + ":\n" + text;
        String translation = OpenAIClient.translateToSpanish(client, prompt);

        //Save new translation
        translationFile.addTranslationMapping(text, translation);
        return translation;
    }


    public static void main(String[] args) {
        TranslationManager t = new TranslationManager("testing2");
        t.readText(helper.readTexts("words.tsv"));
    }
}
