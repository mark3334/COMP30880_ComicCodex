import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FileParser{
    public static void fileToHashmap(File file, Map<String, String> hashMap, boolean appendMode) {
        if (!file.exists()) {
            System.out.println("Error: File does not exist: " + file.getAbsolutePath());
            return;
        }

        if (!appendMode) {
            hashMap.clear();
        }

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] pair = line.split(":",2);
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

    public static void ensureFolderExists(File folder) throws IOException {
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) System.out.println("Created directory: " + folder.getAbsolutePath());
            else throw new IOException("Failed to create directory: " + folder.getAbsolutePath());
        }
    }
}
