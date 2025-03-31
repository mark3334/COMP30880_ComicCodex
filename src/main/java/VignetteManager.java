import java.io.*;
import java.util.*;

public class VignetteManager {
    private String filePath;
    private List<VignetteSchema> vignetteSchemas;
    private Map<String, String> translationMap;
    private TranslationFile  t;
    public VignetteManager() {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        File root = Helper.getRootDirectory();
        String pathFromRoot = configFile.getValueByKey("WORD_ASSET_MAPPING");
        this.filePath = new File(root, pathFromRoot).getAbsolutePath();
        File file = new File(this.filePath);
        this.vignetteSchemas = new ArrayList<>();
        boolean append = false;
        FileParser.readFileToVignetteSchemas(file, this.vignetteSchemas, append);

        this.t = TranslationFile.getInstance();

    }
    public static String translate(String s){
        TranslationFile t = TranslationFile.getInstance();
        return t.translate(s);
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

    public String translateToTarget(String inp) {
        return this.t.translate(inp);
    }

    public static void main(String[] args) {
        VignetteManager text_reader = new VignetteManager();
        System.out.println(text_reader.t.translate("His expression looks determined."));
        //text_reader.printAll();
    }
}