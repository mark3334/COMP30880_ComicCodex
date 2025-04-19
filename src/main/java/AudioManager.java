import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AudioManager {

    private static AudioManager instance;

    private Map<String, Integer> indexes;
    private int nextIndex = 1;
    private final String AUDIO_PATH = ConfigurationFile.getInstance().getValueByKey("AUDIO_PATH");
    private String AUDIO_MP3_PATH = ConfigurationFile.getInstance().getValueByKey("AUDIO_MP3_PATH");
    private File mp3Folder;
    private final String indexName = "indexes.txt";
    private File indexFile = FileParser.getFile(AUDIO_PATH + "/" + indexName);


    private final Path indexPath = indexFile.toPath();
    private AudioManager () {
        load();
    }

    private AudioManager (String audioIndexPath, String audioMp3Path) {
        this.AUDIO_MP3_PATH = audioMp3Path;
        this.indexFile = FileParser.getFile(audioIndexPath);
        load();

    }

    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public static synchronized AudioManager getInstance(String audioIndexPath, String audioMp3Path) {
        if (instance == null) {
            instance = new AudioManager(audioIndexPath, audioMp3Path);
        }
        return instance;
    }
    public void load() {
        this.mp3Folder = FileParser.getFile(AUDIO_MP3_PATH);
        indexes = new HashMap<>();

        Map<String, String> audioMap = new HashMap<>();
        try {
            // Ensure audio folder exists (parent of index file)
            FileParser.ensureFolderExists(FileParser.getFile(AUDIO_PATH));

            // Ensure mp3 folder exists
            FileParser.ensureFolderExists(mp3Folder);

            Path indexPath = indexFile.toPath();
            if (!Files.exists(indexPath)) {
                System.out.println("Audio index file not found, creating: " + indexPath);
                Files.createFile(indexPath);
                return;
            }
            FileParser.fileToHashmap(indexFile, audioMap, false);
            for (Map.Entry<String, String> entry : audioMap.entrySet()) {
                try {
                    int index = Integer.parseInt(entry.getValue());
                    indexes.put(entry.getKey(), index);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid index for entry: " + entry.getKey() + " -> " + entry.getValue());
                }
            }
            this.nextIndex = audioMap.keySet().size() + 1;
            System.out.println("Loaded audio index. Next available index: " + nextIndex);

        } catch (Exception e) {
            System.err.println("Error loading audio index: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getOrAdd(String text) {
        if (indexes.containsKey(text)) {
            return indexes.get(text); // If the text exists, return.
        }
        System.out.println("Creating mp3 file for text: " + text);
        int newIndex = nextIndex++;
        File outputFile = new File(mp3Folder, newIndex + ".mp3");

        try {

            // If mp3 file already exists, skip generation and only add index mapping
            if (outputFile.exists()) {
                System.out.println("ERROR : Skipped mp3 (already exists): " + newIndex + ".mp3");
            } else {
                ConfigurationFile config = ConfigurationFile.getInstance();
                String apiKey = config.getValueByKey("API_KEY");
                String model = config.getValueByKey("AUDIO_MODEL");
                String voice = config.getValueByKey("AUDIO_VOICE");

                // Build the json & request
                String json = String.format("""
                {
                    "model": "%s",
                    "input": "%s",
                    "voice": "%s"
                }
            """, model, text, voice);

                //Send the request to openAi
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/audio/speech"))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .header("Accept", "audio/mpeg")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                //Create the audio file
                try (FileOutputStream out = new FileOutputStream(outputFile)) {
                    response.body().transferTo(out);
                }

                System.out.println("Generated: " + newIndex + ".mp3");
            }

            // Add indexes into the file and save
            indexes.put(text, newIndex);
            appendSingleEntry(text, newIndex);

        } catch (Exception e) {
            System.err.println("Failed to generate MP3 for text: " + text);
            e.printStackTrace();
        }

        return newIndex;
    }


    private void appendSingleEntry(String text, int index) {
        try {
            String entry = text +" "+FileParser.getDelimiterLiteral() +" "+ index + "\n";
            Files.writeString(indexPath, entry, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to append index entry: " + e.getMessage());
        }
    }

    public boolean contains(String text) {
        return indexes.containsKey(text);
    }


    public static void main(String[] args) {
        AudioManager audioIndex = AudioManager.getInstance();

        System.out.println("Hashmap Before Adding: "+audioIndex.indexes.toString());


        assertFalse(audioIndex.contains("Hello"));
        assertFalse(audioIndex.contains("Hej"));


        audioIndex.getOrAdd("Ciao");
        audioIndex.getOrAdd("Bonjour");
        audioIndex.getOrAdd("Hola");
        audioIndex.getOrAdd("Merhaba");
        audioIndex.getOrAdd("Au revoir");

        System.out.println("Hashmap After Adding: "+audioIndex.indexes.toString());

        System.out.println("Hello in Italian: "+audioIndex.getOrAdd("Ciao"));
        System.out.println("Hello in Spanish: "+audioIndex.getOrAdd("Hola"));
        System.out.println("Hello in Turkish: "+audioIndex.getOrAdd("Merhaba"));
        System.out.println("Hello in French: "+audioIndex.getOrAdd("Bonjour"));
        System.out.println("Goodbye in French: "+audioIndex.getOrAdd("Au revoir"));


        assertTrue(audioIndex.contains("Ciao"));
        assertTrue(audioIndex.contains("Hola"));

    }
}
