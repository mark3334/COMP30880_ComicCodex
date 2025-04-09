import java.io.File;

public final class Helper {
    public static String getTargetLanguage() {
        ConfigurationFile config = ConfigurationFile.getInstance();
        return config.getValueByKey("TARGET_LANGUAGE");
    }


    public static File getRootDirectory() {
        File current = new File(System.getProperty("user.dir")); // Current working directory
        // Traverse up to find "COMP30880_ComicCodex"
        while (current != null && !current.getName().equals("COMP30880_ComicCodex")) {
            current = current.getParentFile();
        }
        if (current == null) {
            System.out.println("Error: COMP30880_ComicCodex directory not found!");
        }
        return current; // Returns the root directory or null if not found
    }



    public static void main(String[] args) {
        System.out.println(Helper.getTargetLanguage());
    }

}
