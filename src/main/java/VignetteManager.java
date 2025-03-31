import java.io.*;
import java.util.*;

public class VignetteManager {
    private String filePath;
    private List<VignetteSchema> vignetteSchemas; //A list that will hold VignetteScheme Objects from the file.
    private Map<String, String> translationMap;
    private TranslationFile  t;
    public VignetteManager() {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
<<<<<<< HEAD
        this.filePath = configFile.getValueByKey("WORD_ASSET_MAPPING"); //Loading file path for vignette data.
=======
        File root = Helper.getRootDirectory();
        String pathFromRoot = configFile.getValueByKey("WORD_ASSET_MAPPING");
        this.filePath = new File(root, pathFromRoot).getAbsolutePath();
>>>>>>> 6bc858d9cf12b661950f7fc426cc5e913a88844e
        File file = new File(this.filePath);
        this.vignetteSchemas = new ArrayList<>();
        boolean append = false;
        FileParser.readFileToVignetteSchemas(file, this.vignetteSchemas, append);

        this.t = TranslationFile.getInstance();

<<<<<<< HEAD

    /**
     *
     * @return a random element from the list, or null if empty
     */
=======
    }
    public static String translate(String s){
        TranslationFile t = TranslationFile.getInstance();
        return t.translate(s);
    }
>>>>>>> 6bc858d9cf12b661950f7fc426cc5e913a88844e
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

<<<<<<< HEAD
    /*
        Returns the spanish translation for english text.
     */
    /**
     * @param english: English text to be translated.
     * @return String: Corresponding Translation in Spanish.
     */
    public String translateToSpanish(String english) {
        return translationMap.get(english);
=======
    public String translateToTarget(String inp) {
        return this.t.translate(inp);
>>>>>>> 6bc858d9cf12b661950f7fc426cc5e913a88844e
    }

    public static void main(String[] args) {
        VignetteManager text_reader = new VignetteManager();
        System.out.println(text_reader.t.translate("His expression looks determined."));
        //text_reader.printAll();
    }
}