import java.io.*;
import java.util.ArrayList;
import java.util.List;

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