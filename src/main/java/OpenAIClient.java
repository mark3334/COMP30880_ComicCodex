import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

//TODO make class a Singleton
public class OpenAIClient {
    /**
     * Initialize constant variables
     */
    private static final int MAX_CONVERSATION_HISTORY_SIZE = 5;

    /**
     * Initialize variables
     */
    private static final String apiKey;
    private static final String model;
    private static final String url;
    private static final LinkedList<JSONObject> messageHistory = new LinkedList<>();
    private static final String language;

    static {
        ConfigurationFile config = ConfigurationFile.getInstance();
        model = config.getValueByKey("MODEL");
        url = config.getValueByKey("COMPLETIONS_URL");
        language = config.getValueByKey("TARGET_LANGUAGE");
        apiKey = config.getValueByKey("API_KEY");
        System.out.println("Model: " + model);
        System.out.println("URL: " + url);
        System.out.println("Language: " + language);
        System.out.println("API Key: " + apiKey);
    }
    /**
     * Save conversation history (maximum conversation history is 5)
     * @param prompt: User input
     * @param response: OpenAI response
     */
    public static void saveContext(String prompt, String response) {
        messageHistory.add(new JSONObject().put("role", "user").put("content", prompt));
        messageHistory.add(new JSONObject().put("role", "assistant").put("content", response));

        if (messageHistory.size() > MAX_CONVERSATION_HISTORY_SIZE * 2) {
            messageHistory.removeFirst();
        }
    }
    public static void emptyContext() {
        messageHistory.clear();
    }
    public static String getLanguage() {
        return OpenAIClient.language;
    }
    /**
     * Returns the conversation as JSON
     */
    public static JSONArray getContext() {
        return new JSONArray(messageHistory);
    }

    /**
     * Create an OPENAI request body
     * @param prompt: Add the user input
     * @return: return the body
     */
    private static JSONObject requestBody(String prompt) {
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
    public static String getChatCompletion(String prompt) {
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
    private static String readResponse(HttpURLConnection conn) throws IOException {
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
     * @param jsonResponse: Input JSON data
     */
    private static String parseResponse(String jsonResponse) {
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

    /**
     * User interaction. Realize the interaction between users and chat
     * @param scanner: Get the user input
     */
    public static void runUserInteraction(Scanner scanner) {
        while (true) {

            System.out.print("User: ");
            String prompt = scanner.nextLine().trim();

            if (prompt.isEmpty()) {
                System.out.println("Please enter a message, or please be patient while waiting for a response");
                continue;
            }

            if ("exit".equalsIgnoreCase(prompt)) {
                System.out.println("Goodbye！");
                break;
            }

            System.out.println("(Processing... Please wait)");

            String response = OpenAIClient.getChatCompletion(prompt);

            System.out.println("ChatGPT: " + response);

            saveContext(prompt, response);
        }
    }

    /**
     * Translate a given English text to Spanish using OpenAI.
     * @param englishText: The English input text.
     * @return The translated Spanish text.
     */
    public static String translate(String englishText) {
        String language = OpenAIClient.language;
        String prompt = "Translate the following English word to " + language + ":\n" + englishText;
        String response = getChatCompletion(prompt);

        System.out.println("ChatGPT: " + response);

        OpenAIClient.saveContext(prompt, response);

        return response.trim();
    }

    public static void main(String[] args) {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        String apiKey = configFile.getValueByKey("API_KEY");
        System.out.println("API Key: [" + apiKey + "]");
    }

}