import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AudioIndex {

    private static AudioIndex instance;

    private Map<String, Integer> indexes;
    private int nextIndex = 1;
    private final String AUDIO_INDEX_PATH = ConfigurationFile.getInstance().getValueByKey("AUDIO_INDEX_PATH");
    private final String AUDIO_MP3_PATH = ConfigurationFile.getInstance().getValueByKey("AUDIO_MP3_PATH");;

    private AudioIndex () {
//        System.out.println(AUDIO_INDEX_PATH);
//        System.out.println(AUDIO_MP3_PATH);
//        System.out.println(nextIndex);
//        System.out.println(indexes);
        load();
    }

    public static synchronized AudioIndex getInstance() {
        if (instance == null) {
            instance = new AudioIndex();
        }
        return instance;
    }

    public void load() {
        indexes = new HashMap<>();
        int maxIndex = 0;

        try {
            Path indexPath = Path.of(AUDIO_INDEX_PATH);

            if (!Files.exists(indexPath)) {
                System.out.println("Audio index file not found, creating: " + AUDIO_INDEX_PATH);
                Files.createFile(indexPath); //Create empty file
                return;
            }

            for (String line : Files.readAllLines(indexPath)) {
                if (line.isBlank()) continue;

                String[] parts = line.split("\\|\\|\\|"); // Split using regex for "|||"
                if (parts.length != 3) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                String text = parts[0].trim();
                String language = parts[1].trim();
                String indexStr = parts[2].trim();

                try {
                    int index = Integer.parseInt(indexStr);
                    String key = text + " ||| " + language;
                    indexes.put(key, index);
                    if (index > maxIndex) maxIndex = index;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid index value in line: " + line);
                }
            }

            nextIndex = maxIndex + 1;
            System.out.println("Loaded audio index. Next available index: " + nextIndex);

        } catch (Exception e) {
            System.err.println("Error loading audio index: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getOrAdd(String text, String language) {
        String key = text + " ||| " + language;
        if (indexes.containsKey(key)) {
            return indexes.get(key);
        }

        //Otherwise need to Add this new (text + language) => index, and valid .mp3 file.
        //TODO sudo code
        int newIndex = nextIndex++;
        //Need to make TTS Call Successfully, and then only put() below.
        indexes.put(key, newIndex);
        appendSingleEntry(text, language, newIndex); //Save new index to disk.

        return newIndex;
    }

    public void appendSingleEntry(String text, String language, int index) {
        try {
            Path indexPath = Path.of(AUDIO_INDEX_PATH);
            String entry = text + " ||| " + language + " ||| " + index + "\n";
            Files.writeString(indexPath, entry, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to append index entry: " + e.getMessage());
        }
    }

    public boolean contains(String text, String language) {
        return indexes.containsKey(text + " ||| " + language);
    }


    public static void main(String[] args) {
        HashMap<String, Integer> index = new HashMap<>();
        AudioIndex audioIndex = new AudioIndex();

        System.out.println("Hashmap Before Adding: "+audioIndex.indexes.toString());


        assertFalse(audioIndex.contains("Hello", "Lingala"));
        assertFalse(audioIndex.contains("Hello", "Hindi"));


        audioIndex.getOrAdd("Hello", "French");
        audioIndex.getOrAdd("Hello", "Urdu");
        audioIndex.getOrAdd("Hello", "Arabic");
        audioIndex.getOrAdd("Hello", "Irish");


        System.out.println("Hashmap After Adding: "+audioIndex.indexes.toString());

        System.out.println("Hello in French: "+audioIndex.getOrAdd("Hello", "French"));
        System.out.println("Hello in Urdu: "+audioIndex.getOrAdd("Hello", "Urdu"));
        System.out.println("Hello in Arabic: "+audioIndex.getOrAdd("Hello", "Arabic"));
        System.out.println("Hello in Irish: "+audioIndex.getOrAdd("Hello", "Irish"));


        assertTrue(audioIndex.contains("Hello", "French"));
        assertTrue(audioIndex.contains("Hello", "Urdu"));

    }
}
