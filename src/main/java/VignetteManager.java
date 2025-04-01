import java.io.*;
import java.util.*;

public class VignetteManager {
    private String filePath;
    private List<VignetteSchema> vignetteSchemas; //A list that will hold VignetteScheme Objects from the file.

    public VignetteManager() {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        this.filePath = configFile.getValueByKey("WORD_ASSET_MAPPING"); //Loading file path for vignette data.
        File file = new File(this.filePath);
        this.vignetteSchemas = new ArrayList<>();
        boolean append = false;
        FileParser.readFileToVignetteSchemas(file, this.vignetteSchemas, append);
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

    public static void main(String[] args) {
        VignetteManager text_reader = new VignetteManager();
        text_reader.printAll();
    }
}