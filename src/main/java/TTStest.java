import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//TODO: change the class name to generateAudio OR move the method to AudioIndex file and change the file name to AudioManager.
//TODO: Need test file.

public class TTStest {
    public static void generateMp3File(String inputSting) throws IOException, InterruptedException {
        ConfigurationFile config = ConfigurationFile.getInstance();
        String apiKey = config.getValueByKey("API_KEY");
        String model = config.getValueByKey("AUDIO_MODEL");
        String voice = config.getValueByKey("AUDIO_VOICE");

        // Build the json & request
        String json = String.format("""
            {
                "model": "%s",
                "input": "%s",
                "voice": "%s",
            }
        """,model,inputSting,voice);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/audio/speech"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "audio/mpeg")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        //Send the request to openAi
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        //Create the audio file
        //TODO: output the file to the path we want.
        FileOutputStream out = new FileOutputStream("speech.mp3");
        response.body().transferTo(out);
        out.close();

        System.out.println("Speech generated and saved to speech.mp3");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        generateMp3File("Nice");
    }
}
