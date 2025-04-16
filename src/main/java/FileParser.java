import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FileParser{
    // Delimiter as literal for writing/replacing text
    private static final String delimiterLiteral = "|||";

    // Delimiter as regex for reading/splitting
    private static final String delimiterRegex = "\\|\\|\\|"; // Escaped for regex (| is special)

    public static File root = Helper.getRootDirectory();

    public static String getDelimiterLiteral() {
        return delimiterLiteral;
    }

    public static String getDelimiterRegex() {
        return delimiterRegex;
    }
    public static void fileToHashmap(File file, Map<String, String> hashMap, boolean appendMode) {
        if (!file.exists()) {
            System.out.println("Error: File does not exist: " + file.getAbsolutePath());
            return;
        }

        if (!appendMode) {
            hashMap.clear();
        }

        String regex = getDelimiterRegex();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {

                String[] pair = line.split(regex,2);
                if (pair.length == 2) {
                    String key = pair[0].replaceAll("\"", "").trim();
                    String value = pair[1].replaceAll("\"", "").trim();   //replaceAll("\"", "").trim();
                    if (!key.isEmpty() && !value.isEmpty())
                        hashMap.put(key, value);
                }
                else{
                    System.out.println("Line does not conform to hashmap structure " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error reading file " + file.getAbsolutePath());
        }
    }


    public static void ensureFolderExists(File folder) throws IOException {
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) System.out.println("Created directory: " + folder.getAbsolutePath());
            else throw new IOException("Failed to create directory: " + folder.getAbsolutePath());
        }
    }

    public static void replaceDelimiter(File file, String oldDelimiter) {
        String newDelimiter = getDelimiterLiteral();
        List<String> updatedLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String updatedLine = line.replaceFirst(oldDelimiter, newDelimiter);
                updatedLines.add(updatedLine);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error reading file " + file.getAbsolutePath());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file " + file.getAbsolutePath());
        }
    }

    public static File getTranslationFile() {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        String filepath = configFile.getValueByKey("TRANSLATIONS_PATH");
        filepath = new File(root, filepath).getAbsolutePath();
        filepath += ("/" + configFile.getValueByKey("SOURCE_LANGUAGE") + "_" + configFile.getValueByKey("TARGET_LANGUAGE"));
        System.out.println("Filepath : " + filepath);
        return new File(filepath);
    }
    public static File getConfigFile() {
        return new File(root, "Resources/Config.txt");
    }

    public static File getFile(String pathFromRoot) {
        return new File(root, pathFromRoot);
    }

    public static void main(String[] args)  {

        String oldDelimiter = ":"; // \\|\\|\\|"; // escape each '|'
        //replaceDelimiter(getConfigFile(), oldDelimiter);
        replaceDelimiter(getTranslationFile(), oldDelimiter);
    }
}
