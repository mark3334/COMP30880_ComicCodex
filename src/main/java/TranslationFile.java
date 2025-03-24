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

    /**
     * Loads Translations from the File into Memory (Map<String, String>) and returns this.
     */
    public Map<String, String> loadTranslationsFile() { //Loads Translations from the File into Memory (Map).
        Map<String, String> translations = new HashMap<String, String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" : ");
                if (parts.length == 2) {
                    translations.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        catch (IOException e) {
            System.err.println("Error reading translation file: " + e.getMessage());
        }

        return translations;
    }

    public void writeTranslatedVignetteToTSV(sourceText original, String translatedCombinedText, String translatedLeftText) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String line = String.join("\t",
                    original.getLeftPose(),
                    translatedLeftText,
                    translatedCombinedText,
                    String.join(", ", original.getRightPose()),
                    String.join(", ", original.getBackgrounds())
            );
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to TSV: " + e.getMessage());
        }
    }
}