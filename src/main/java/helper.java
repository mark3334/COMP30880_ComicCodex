import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.*;
import java.io.*;
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

    public static void create_submission_zip(int sprintNum) throws IOException {
        //TODO
         File root = helper.getRootDirectory();
         File submissionFolder = new File(root, "Submission");
         if (!submissionFolder.exists()) {
            System.out.println("Submission folder doesn't exist.");
            return;
         }
        // Find files starting with Sprint{i}
        File[] filesToCopy = submissionFolder.listFiles((dir1, name) -> name.startsWith("Sprint" + sprintNum));
        String arr[] = submissionFolder.list();
        String prefix = "Sprint" + Integer.toString(sprintNum);
        List<String> filteredNames = new ArrayList<>();
        if (arr != null) {
            // Print all the files in the array
            System.out.println("Files in the submission folder:");
            for (String fileName : arr) {
                System.out.println(fileName);
                if (fileName.startsWith(prefix)) {
                    filteredNames.add(fileName);
                }
            }

            System.out.println("Filtered Files:");
            for (String filteredName : filteredNames) {
                System.out.println(filteredName);
            }
        }
        else {
            System.out.println("The submission folder is empty or does not exist.");
        }
        List<File> filteredFiles = new ArrayList<>();;
        for(String fileName : filteredNames){
            File f = new File(submissionFolder, fileName);
            if (f.isFile()) {
                filteredFiles.add(f);
            }
        }
        File config = new File(root, "Resources/Config.txt");
        filteredFiles.add(config);
        String zipFile = "Compressed" + "Sprint" + Integer.toString(sprintNum) + ".zip";
        //Now there is a list of file objects to be zipped together
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            byte[] buffer = new byte[1024];
            System.out.println("Files to be zipped:");
            for (File fileToZip : filteredFiles) {
                System.out.println(fileToZip.getName());
                try (FileInputStream fis = new FileInputStream(fileToZip)) {
                    zos.putNextEntry(new ZipEntry(fileToZip.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }

         return;
    }

    public static void main(String[] args) {
        try{
            create_submission_zip(2);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
