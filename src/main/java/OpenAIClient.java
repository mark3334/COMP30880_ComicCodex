import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpenAIClient {
    private String apiKey;
    private String model;
    private String url;
    //public ConfigurationFile configFile = new ConfigurationFile();

    public OpenAIClient() {
        this.apiKey = ConfigurationFile.getValueByKey("API_KEY");
        this.model = ConfigurationFile.getValueByKey("MODEL");
        this.url = ConfigurationFile.getValueByKey("COMPLETIONS_URL");
    }

    public String getChatCompletion(String prompt) {
        try {
            URL endpoint = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);

            // Using JSONArray to store the user message
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", prompt));
            requestBody.put("messages", messages);

            // try to send the request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return parseResponse(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching response";
        }
    }

    private String parseResponse(String jsonResponse) {
        JSONObject responseObj = new JSONObject(jsonResponse);
        if (responseObj.has("choices")) {
            String response =  responseObj.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            if(response.equals("I’m sorry, but I can’t assist with that. "))
            {
                System.out.println("Denial of service from OpenAI");
            }
            return response;
        }
        return "Invalid response format";
    }

    public static void main(String[] args) {
        OpenAIClient client = new OpenAIClient();
        Scanner scanner = new Scanner(System.in);

        System.out.println("--------------------------------");
        System.out.println("| Welcome to ComicCodex project|");
        System.out.println("| Press 'exit' to exit.        |");
        System.out.println("--------------------------------");
        String mode_string = ConfigurationFile.getValueByKey("USER_MODE");
        boolean userMode = Boolean.parseBoolean(mode_string.toLowerCase());
        ArrayList<String> prompts = new ArrayList<>();
        prompts.add("What's the weather");

        while (userMode) {
            System.out.print("User: ");
            String prompt = scanner.nextLine();

            if ("exit".equalsIgnoreCase(prompt)) {
                System.out.println("Goodbye！");
                break;
            }

            String response = client.getChatCompletion(prompt);
            System.out.println("ChatGPT: " + response);
        }
        if(!userMode)
        {
            System.out.println("Test start: ");
            for (String prompt : prompts) {
                System.out.println("User(test): " + prompt);
                String response = client.getChatCompletion(prompt);
                System.out.println("ChatGPT: " + response);
            }
        }

        scanner.close();
    }
}
