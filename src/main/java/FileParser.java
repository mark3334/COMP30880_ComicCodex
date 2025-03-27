import java.io.*;
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
                    System.out.println("Mistake in config line: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error reading file " + file.getAbsolutePath());
        }
    }
}
