import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TranslationFile {
    private final File file;
    private Map<String, String> translations;
    public TranslationFile(String filePath) {
        this.file = new File(filePath);
        this.translations = new HashMap<>();
        this.loadTranslationsFile();
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
    public void loadTranslationsFile() { //Loads Translations from the File into Memory (Map).
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" : ");
                if (parts.length == 2) {
                    this.translations.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        catch (IOException e) {
            System.err.println("Error reading translation file: " + e.getMessage());
        }
    }

    /**
     * Appends new translations into the Translation File.
     * @param source: Original Text in English
     * @param target: Text in Target Language
     */
    public void addTranslationMapping(String source, String target) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file, true))) {
            w.write(source + " : " + target);
            w.newLine();
        } catch (IOException e) {
            System.err.println("Error writing translation file: " + e.getMessage());
        }
    }

    public Map<String, String> getTranslations(){return this.translations;}

    public void writeTranslatedVignetteToTSV(VignetteSchema original, String translatedCombinedText, String translatedLeftText) {
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