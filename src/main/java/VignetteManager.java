import java.io.*;
import java.util.*;

public class VignetteManager {
    private String filePath;
    private List<VignetteSchema> vignetteSchemas;
    private Map<String, String> translationMap;

    public VignetteManager() {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        this.filePath = configFile.getValueByKey("WORD_ASSET_MAPPING");
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

    public List<VignetteSchema> getVignetteSchemas(){
        return this.vignetteSchemas;
    }

    public VignetteSchema getRandomSchema() {
        if (vignetteSchemas.isEmpty()) return null;
        Random rand = new Random();
        return vignetteSchemas.get(rand.nextInt(vignetteSchemas.size()));
    }

    public void printAll() {
        for (VignetteSchema text : this.vignetteSchemas) {
            System.out.println(text);
        }
    }

    public String translateToSpanish(String english) {
        return translationMap.get(english);
    }

    public static void main(String[] args) {
        VignetteManager text_reader = new VignetteManager();
        System.out.println(text_reader.translateToSpanish("His expression looks determined."));
        //text_reader.printAll();
    }
}