import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpenAIClient {
    /**
     * Initialize constant variables
     */
    private static final int MAX_CONVERSATION_HISTORY_SIZE = 5;

    /**
     * Initialize variables
     */
    private String apiKey;
    private String model;
    private String url;
    private final LinkedList<JSONObject> messageHistory = new LinkedList<>();

    /**
     * OpenAIClient construct function.
     */
    public OpenAIClient() {
        ConfigurationFile config = new ConfigurationFile();
        this.apiKey = config.getValueByKey("API_KEY");
        this.model = config.getValueByKey("MODEL");
        this.url = config.getValueByKey("COMPLETIONS_URL");
    }

    /**
     * Save conversation history (maximum conversation history is 5)
     * @param prompt: User input
     * @param response: OpenAI response
     */
    public void saveContext(String prompt, String response) {
        messageHistory.add(new JSONObject().put("role", "user").put("content", prompt));
        messageHistory.add(new JSONObject().put("role", "assistant").put("content", response));

        if (messageHistory.size() > MAX_CONVERSATION_HISTORY_SIZE * 2) {
            messageHistory.removeFirst();
        }
    }

    /**
     * Returns the conversation as JSON
     */
    public JSONArray getContext() {
        return new JSONArray(messageHistory);
    }

    /**
     * Create an OPENAI request body
     * @param prompt: Add the user input
     * @return: return the body
     */
    private JSONObject requestBody(String prompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);

        JSONArray messages = getContext();
        messages.put(new JSONObject().put("role", "user").put("content", prompt));
        requestBody.put("messages", messages);

        return requestBody;
    }

    /**
     * Send the API request and get response from ChatGPT
     * @param prompt: Add the user input
     */
    public String getChatCompletion(String prompt) {
        try {
            URL endpoint = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject requestBody = requestBody(prompt);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Error: OpenAI API returned status code " + responseCode;
            }

            return readResponse(conn);
        } catch (Exception e) {
            return "Error processing request: " + e.getMessage();
        }
    }

    /**
     * Parsing API responses
     * @param conn: HttpURLConnection
     * @throws IOException:
     */
    private String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return parseResponse(response.toString());
        }
    }

    /**
     * Parsing the JSON data returned by the OpenAI API
     * @param jsonResponse
     */
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

        System.out.println("""
        --------------------------------
        | Welcome to ComicCodex project |
        | Press 'exit' to exit.         |
        --------------------------------
        """);

        while (true) {
            System.out.print("User: ");
            String prompt = scanner.nextLine();
            if ("exit".equalsIgnoreCase(prompt)) {
                System.out.println("Goodbye！");
                break;
            }

            String response = client.getChatCompletion(prompt);
            client.saveContext(prompt, response);
            System.out.println("ChatGPT: " + response);
        }
    }
}

        /*
        if(!userMode)
        {
            System.out.println("Test start: ");
            for (String prompt : prompts) {
                System.out.println("User(test): " + prompt);
                String response = client.getChatCompletion(prompt);
                System.out.println("ChatGPT: " + response);
            }
        }

         */
        /*
        String mode_string = client.getconfig().getValueByKey("USER_MODE");

        boolean userMode = Boolean.parseBoolean(mode_string.toLowerCase());
        ArrayList<String> prompts = new ArrayList<>();
        prompts.add("What's the weather");
        */