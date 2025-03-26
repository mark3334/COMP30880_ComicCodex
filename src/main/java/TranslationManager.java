import java.util.List;
import java.util.Map;

public class TranslationManager {
    private final TranslationFile translationFile;

    public TranslationManager(String filePath) {
        this.translationFile = new TranslationFile(filePath);
    }

    /**
     * Sends a GPT call to translate the text phrase, if the translation doesn't exist already in file.
     * Extra Note: Doesn't add anything to Translation File is error is received back.
     * @param text: Original Text in English
     */
    public String translate(String text) {
        Map<String, String> translations = translationFile.getTranslations(); // Load existing translations

        if (translations.containsKey(text)) {//if translation exists
            //System.out.println("ALREADY IN MAP: " + text);
            return translations.get(text);
        }

        if(text.isEmpty()) { //if the input text is empty
            return "";
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

        return translation;
    }

    /**
     * Full pipeline: read → translate → write
     */
    public void processAll(List<VignetteSchema> list) {
        for (VignetteSchema vignette : list) {
            String translatedCombined = translate(vignette.getCombinedText().toString());
            String translatedLeft = translate(vignette.getLeftText().toString());

            System.out.println("Original combinedText: " + vignette.getCombinedText());
            System.out.println("Translate: " + translatedCombined);
            System.out.println("Original left_text: " + vignette.getLeftText());
            System.out.println("Translate: " + translatedLeft);
            System.out.println("--------------------------");

            translationFile.writeTranslatedVignetteToTSV(vignette, translatedCombined, translatedLeft);
        }
        System.out.println("Translation complete.");
    }

    public static void main(String[] args) {
        String filePath = "Resources/words.tsv";
        TextReader text_reader = new TextReader(filePath);
        TranslationManager t = new TranslationManager("Resources/Translations.txt");
        t.processAll(text_reader.getTexts());
    }
}