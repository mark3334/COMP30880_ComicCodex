import java.util.List;
import java.util.Map;

public class TranslationManager {
    private final TranslationFile translationFile;

    public TranslationManager(String filePath) {
        this.translationFile = new TranslationFile(filePath);
    }

    /**
     * Reads the CombinedText from the sourceText instances and sends to translate method.
     * @param textEntries: List of sourceText instances having
     */
    public void readText(List<sourceText> textEntries) {
        for (sourceText entry : textEntries) {
            String combined = String.valueOf(entry.getCombinedText()); // Assuming this returns the full text
            String left_text = String.valueOf(entry.getLeftText()); //Assuming this returns the full left_text
            String combined_translated = translate(combined);
            String left_translated = translate(left_text);

            System.out.println("Original combinedText: " + combined);
            System.out.println("Translate: " + combined_translated);
            System.out.println("Original left_text: " + left_text);
            System.out.println("Translate: " + left_translated);
            System.out.println("--------------------------");
        }
    }

    /**
     * Sends a GPT call to translate the text phrase, if the translation doesn't exist already in file.
     * Extra Note: Doesn't add anything to Translation File is error is received back.
     * @param text: Original Text in English
     */
    public String translate(String text) {
        Map<String, String> translations = translationFile.loadTranslationsFile(); // Load existing translations

        if (translations.containsKey(text)) {//if translation exists
            //System.out.println("ALREADY IN MAP: " + text);
            return translations.get(text);
        }

        //otherwise if it dont exist in MAP then we ask GPT.
        String targetLanguage = helper.getTargetLanguage();
        String prompt = "Please translate the following English text to " + targetLanguage + ":\n" + text + "Please note, you only need to reply with the translated text. \" +\n" +
                "                \"For example, if I ask you Hello, you should simply reply Bonjour.";
        String translation = OpenAIClient.translate(prompt);


        //If the error starts off with error, then we dont want to include it in the TranslationFile
        if (translation.startsWith("Error:")) {
            System.err.println("Translation failed: " + translation);
            return translation; // Just return the error, do not save it
        }

        //Save new translation
        translationFile.addTranslationMapping(text, translation);
        return translation;
    }

    public static void main(String[] args) {
        TranslationManager t = new TranslationManager("TextFiles/Translations.txt");
        String filePath = "Resources/words.tsv";
        TextReader text_reader = new TextReader(filePath);
        t.readText(text_reader.getTexts());
        //t.readText(helper.readTexts("words.tsv"));
    }
}