import java.io.*;
import java.util.*;

public class VignetteManager {
    private String filePath;
    private List<VignetteSchema> vignetteSchemas; //A list that will hold VignetteScheme Objects from the file.

    public VignetteManager() {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        File root = Helper.getRootDirectory();
        String pathFromRoot = configFile.getValueByKey("WORD_ASSET_MAPPING");
        this.filePath = new File(root, pathFromRoot).getAbsolutePath();
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