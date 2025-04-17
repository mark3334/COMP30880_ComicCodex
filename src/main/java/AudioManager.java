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
    private final String AUDIO_INDEX_PATH = ConfigurationFile.getInstance().getValueByKey("AUDIO_INDEX_PATH");
    private final String AUDIO_MP3_PATH = ConfigurationFile.getInstance().getValueByKey("AUDIO_MP3_PATH");;

    private AudioManager () {
//        System.out.println(AUDIO_INDEX_PATH);
//        System.out.println(AUDIO_MP3_PATH);
//        System.out.println(nextIndex);
//        System.out.println(indexes);
        load();
    }

    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void load() {
        indexes = new HashMap<>();
        int maxIndex = 0;

        try {
            Path indexPath = Path.of(AUDIO_INDEX_PATH);

            if (!Files.exists(indexPath)) {
                File root = Helper.getRootDirectory();
                File folder = new File(root, "Resources/Audio");
                FileParser.ensureFolderExists(folder);
                System.out.println("Audio index file not found, creating: " + AUDIO_INDEX_PATH);
                Files.createFile(indexPath); //Create empty file
                return;
            }

            for (String line : Files.readAllLines(indexPath)) {
                if (line.isBlank()) continue;

                String[] parts = line.split(FileParser.getDelimiterRegex()); // Split using regex for "|||"
                if (parts.length != 2) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                String text = parts[0].trim();
                String indexStr = parts[1].trim();

                try {
                    int index = Integer.parseInt(indexStr);
                    indexes.put(text, index);
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

    public int getOrAdd(String text) {
        if (indexes.containsKey(text)) {
            return indexes.get(text); // If the text exists, return.
        }

        int newIndex = nextIndex++;
        File audioFolder = new File(FileParser.getRootDirectory(), "Resources/Audio");
        File outputFile = new File(audioFolder, newIndex + ".mp3");

        try {
            FileParser.ensureFolderExists(audioFolder);

            // If mp3 file already exists, skip generation and only add index mapping
            if (outputFile.exists()) {
                System.out.println("Skipped mp3 (already exists): " + newIndex + ".mp3");
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


    public void appendSingleEntry(String text, int index) {
        try {
            Path indexPath = Path.of(AUDIO_INDEX_PATH);
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
        HashMap<String, Integer> index = new HashMap<>();
        AudioManager audioIndex = new AudioManager();

        System.out.println("Hashmap Before Adding: "+audioIndex.indexes.toString());


        assertFalse(audioIndex.contains("Hello"));
        assertFalse(audioIndex.contains("Hej"));


        audioIndex.getOrAdd("Ciao");
        audioIndex.getOrAdd("Bonjour");
        audioIndex.getOrAdd("Hola");
        audioIndex.getOrAdd("Merhaba");


        System.out.println("Hashmap After Adding: "+audioIndex.indexes.toString());

        System.out.println("Hello in Italian: "+audioIndex.getOrAdd("Ciao"));
        System.out.println("Hello in Spanish: "+audioIndex.getOrAdd("Hola"));
        System.out.println("Hello in Turkish: "+audioIndex.getOrAdd("Merhaba"));
        System.out.println("Hello in French: "+audioIndex.getOrAdd("Bonjour"));


        assertTrue(audioIndex.contains("Ciao"));
        assertTrue(audioIndex.contains("Hola"));

    }
}
