import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class helper {
    public static String getTargetLanguage() {
        ConfigurationFile config = new ConfigurationFile();
        return config.getValueByKey("language");
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

    public static void create_submission_zip(int sprintNum){
        //TODO
         File root = helper.getRootDirectory();
         File submissionFolder = new File(root, "Submission");
         if (!submissionFolder.exists()) {
            System.out.println("Submission folder doesn't exist.");
            return;
         }
        // Find files starting with Sprint{i}
        File[] filesToCopy = submissionFolder.listFiles((dir1, name) -> name.startsWith("Sprint" + sprintNum));
        return;
    }

}
