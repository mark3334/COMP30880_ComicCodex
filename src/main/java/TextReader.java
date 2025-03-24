import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextReader {
//    public static void main(String[] args) {
//        String filePath = "Resources/words.tsv";
//        List<sourceText> texts = new ArrayList<>();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            boolean firstLine = true;
//
//            while ((line = br.readLine()) != null) {
//                if (firstLine) {
//                    firstLine = false;
//                    continue;
//                }
//
//                String[] values = line.split("\t", -1);
//                if (values.length < 5) continue;
//
//                sourceText text = new sourceText(
//                        values[0],
//                        values[1],
//                        values[2],
//                        values[3],
//                        values[4]
//                );
//                texts.add(text);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        OpenAIClient client = new OpenAIClient();
//        TranslationFile translationFile = new TranslationFile("translations.txt");
//
//        Map<String, String> existingTranslations = translationFile.loadTranslationsFile();
//
//        for (sourceText text : texts) {
//            String source = String.valueOf(text.getCombinedText());
//            String translated;
//
//            if (existingTranslations.containsKey(source)) {
//                translated = existingTranslations.get(source);
//            } else {
//                String prompt = "Please translate the following English text to " + translationFile.getTargetLanguage() + ":\n" + source;
//                translated = client.translateToSpanish(client, prompt);
//
//                translationFile.addTranslationMapping(source, translated);
//                existingTranslations.put(source, translated);
//            }
//
//            System.out.println("Original: " + source);
//            System.out.println("Translate: " + translated);
//            System.out.println("--------------------------");
//        }
//    }
}