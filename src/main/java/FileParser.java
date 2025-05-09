import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FileParser{
    // Delimiter as literal for writing/replacing text
    private static final String delimiterLiteral = "|||";

    // Delimiter as regex for reading/splitting
    private static final String delimiterRegex = "\\|\\|\\|"; // Escaped for regex (| is special)

    public static File root = FileParser.getRootDirectoryMethod();

    public static String getDelimiterLiteral() {
        return delimiterLiteral;
    }

    public static File getRootDirectory() {
        return root;
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
        if (file.length() == 0) {
            System.out.println("File is empty: " + file.getAbsolutePath());
            return;
        }

        String regex = getDelimiterRegex();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {

                String[] pair = line.split(regex,2);
                if (pair.length == 2) {
                    String key = pair[0].trim();
                    String value = pair[1].trim();   //replaceAll("\"", "").trim();
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

    public static void readFileToVignetteSchemas(File file, List<VignetteSchema> vignetteSchemas, boolean appendMode) {
        if (!appendMode) {
            vignetteSchemas.clear();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip the header row
                }
                String[] values = line.split("\t", -1);
                VignetteSchema text = new VignetteSchema(
                        values[0], // Left Pose
                        values[1], // Combined Text
                        values[2], // Left Text
                        values[3], // Right Pose
                        values[4]  // Backgrounds
                );
                vignetteSchemas.add(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    private static File getRootDirectoryMethod() {
        File current = new File(System.getProperty("user.dir")); // Current working directory
        // Traverse up to find "COMP30880_ComicCodex"
        while (current != null && !isRootDirectory(current.getName())) {
            current = current.getParentFile();
        }
        if (current == null) {
            System.out.println("Error: COMP30880_ComicCodex directory not found!");
            // System.exit(1);
        }
        return current; // Returns the root directory or null if not found
    }

    public static boolean isRootDirectory(String filename) {
        filename = filename.toLowerCase().trim();
        return filename.startsWith("comp30880_comiccodex") && !filename.contains("jar");
    }

    public static String generateMapLine(String key, String value){
        return key + " " + getDelimiterLiteral() + " " + value;
    }

    public static void ensureUntranslatedExists() {
        String leftScenesPath = FileParser.getRootDirectory() + "/Resources/XMLoutput/left_scenes.xml";
        String wholeScenesPath = FileParser.getRootDirectory() + "/Resources/XMLoutput/whole_scenes.xml";
        String leftScenesUntranslatedPath = FileParser.getRootDirectory() + "/Resources/XMLoutput/left_scenes_untranslated.xml";
        String wholeScenesUntranslatedPath = FileParser.getRootDirectory() + "/Resources/XMLoutput/whole_scenes_untranslated.xml";

        // Check if the untranslated files already exist
        if (new File(leftScenesUntranslatedPath).exists() && new File(wholeScenesUntranslatedPath).exists()) {
            System.out.println("Untranslated files already exist.");
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Parse and process left_scenes.xml
            File leftScenesFile = new File(leftScenesPath);
            if (leftScenesFile.exists()) {
                Document doc1 = builder.parse(leftScenesFile);
                doc1.getDocumentElement().normalize();
                XmlWriter.removeTranslatedPanel(doc1);
                saveDocumentToFile(doc1, leftScenesUntranslatedPath);
            } else {
                System.err.println("File not found: " + leftScenesPath);
            }

            //Parse and process whole_scenes.xml
            File wholeScenesFile = new File(wholeScenesPath);
            if (wholeScenesFile.exists()) {
                Document doc2 = builder.parse(wholeScenesFile);
                doc2.getDocumentElement().normalize();
                XmlWriter.removeTranslatedPanel(doc2);
                saveDocumentToFile(doc2, wholeScenesUntranslatedPath);
            } else {
                System.err.println("File not found: " + wholeScenesPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDocumentToFile(Document doc, String outputPath) {
        try {
            // Create a TransformerFactory and a Transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Pretty print the XML

            // Prepare the DOM document for writing
            DOMSource source = new DOMSource(doc);

            // Set the output file
            StreamResult result = new StreamResult(new File(outputPath));

            // Write the data to the output file
            transformer.transform(source, result);

            System.out.println("File saved to: " + outputPath);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args)  {

        String oldDelimiter = ":"; // \\|\\|\\|"; // escape each '|'
        //replaceDelimiter(getConfigFile(), oldDelimiter);
        //replaceDelimiter(getTranslationFile(), oldDelimiter);
        System.out.println(FileParser.getRootDirectory().getAbsolutePath());
    }
}
