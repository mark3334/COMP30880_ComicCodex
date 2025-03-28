import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VignetteManager {
    private String filePath;
    private List<VignetteSchema> vignetteSchemas;

    public VignetteManager() {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        this.filePath = configFile.getValueByKey("WORD_ASSET_MAPPING");
        File file = new File(this.filePath);
        this.vignetteSchemas = new ArrayList<>();
        boolean append = false;
        FileParser.readFileToVignetteSchemas(file, this.vignetteSchemas, append);
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
    public static void main(String[] args) {
        VignetteManager text_reader = new VignetteManager();
        text_reader.printAll();
    }
}