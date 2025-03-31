import java.io.*;
import java.util.*;

public class VignetteManager {
    private String filePath;
    private List<VignetteSchema> vignetteSchemas; //A list that will hold VignetteScheme Objects from the file.
    private Map<String, String> translationMap;

    public VignetteManager() {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        this.filePath = configFile.getValueByKey("WORD_ASSET_MAPPING"); //Loading file path for vignette data.
        File file = new File(this.filePath);
        this.vignetteSchemas = new ArrayList<>();
        boolean append = false;
        FileParser.readFileToVignetteSchemas(file, this.vignetteSchemas, append);

        this.translationMap = new HashMap<>();
        String folderPath = configFile.getValueByKey("TRANSLATIONS_PATH");
        String translationFileName = "English_Spanish";
        File translationFile = new File(folderPath, translationFileName);
        FileParser.fileToHashmap(translationFile, this.translationMap, false);
    }


    /**
     *
     * @return a random element from the list, or null if empty
     */
    public List<VignetteSchema> getVignetteSchemas(){
        return this.vignetteSchemas;
    }

    /**
     *
     * @return a random element from the list, or null if empty
     */
    public VignetteSchema getRandomSchema() {
        if (vignetteSchemas.isEmpty()) return null;
        Random rand = new Random();
        return vignetteSchemas.get(rand.nextInt(vignetteSchemas.size()));
    }

    /**
     * Prints all loaded vignettes schemas.
     */
    public void printAll() {
        for (VignetteSchema text : this.vignetteSchemas) {
            System.out.println(text);
        }
    }

    /*
        Returns the spanish translation for english text.
     */
    /**
     * @param english: English text to be translated.
     * @return String: Corresponding Translation in Spanish.
     */
    public String translateToSpanish(String english) {
        return translationMap.get(english);
    }

    public static void main(String[] args) {
        VignetteManager text_reader = new VignetteManager();
        System.out.println(text_reader.translateToSpanish("His expression looks determined."));
        //text_reader.printAll();
    }
}