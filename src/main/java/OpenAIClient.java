import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class OpenAIClient {
    private static OpenAIClient instance;

    /**
     * Initialize constant variables
     */
    private static final int MAX_CONVERSATION_HISTORY_SIZE = 5;
    private static final LinkedList<JSONObject> messageHistory = new LinkedList<>();

    /**
     * Initialize variables
     */
    private final String apiKey;
    private final String model;
    private final String url;
    private final String language;

    private OpenAIClient() {
        ConfigurationFile config = ConfigurationFile.getInstance();
        this.model = config.getValueByKey("MODEL");
        this.url = config.getValueByKey("COMPLETIONS_URL");
        this.language = config.getValueByKey("TARGET_LANGUAGE");
        this.apiKey = config.getValueByKey("API_KEY");

//        System.out.println("Model: " + model);
//        System.out.println("URL: " + url);
//        System.out.println("Language: " + language);
//        System.out.println("API Key: " + apiKey);
    }

    public static synchronized OpenAIClient getInstance() {
        if (instance == null) {
            instance = new OpenAIClient();
        }
        return instance;
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

    public void emptyContext() {
        messageHistory.clear();
    }

    public String getLanguage() {
        return language;
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
     * @param jsonResponse: Input JSON data
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

    /**
     * User interaction. Realize the interaction between users and chat
     * @param scanner: Get the user input
     */
    public void runUserInteraction(Scanner scanner) {
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

            String response = getChatCompletion(prompt);

            System.out.println("ChatGPT: " + response);

            saveContext(prompt, response);
        }
    }

    /**
     * Translate a given English text to Spanish using OpenAI.
     * @param englishText: The English input text.
     * @return The translated Spanish text.
     */
    public String translate(String englishText) {
        String prompt = "You are to act as a professional translator of the " + language + "language\n";
        prompt += "You must translate the text that is wrapped in the phrase tag\n";
        prompt += "Your response must only include the translation and nothing else\n";
        prompt += "<phrase> " + englishText + "<phrase>";
        String translation = getChatCompletion(prompt);
        translation = translation.trim().replace(".", "");
        System.out.println("ChatGPT: " + translation);

        //OpenAIClient.saveContext(prompt, response);
        if (translation.contains("429")) {
            System.err.println("Rate limit hit. Retrying in 60 seconds...");
            try {
                Thread.sleep(120_000); //120 seconds wait
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); //restore interrupt status
            }

            //Retry the request
            translation = OpenAIClient.getInstance().translate(prompt);
        }


        //If the error starts off with error, then we dont want to include it in the TranslationFile
        if (translation.startsWith("Error:") || translation.equals("Key not Found")) {
            System.err.println("Translation failed: " + translation);
            return translation; // Just return the error, do not save it
        }
        return translation.trim();
    }


    /**
     * @param phrases: List of phrases to be translated
     * @return List: List of Translations in Spanish.
     */
    public List<String> translateAll(List<String> phrases){
        StringBuilder sb = new StringBuilder();
        sb.append("Translate the following English words to ").append(language).append(".");
        sb.append("The format of the output should be the translation of each phrase on a newline:");
        sb.append("Pay attention to tense and person; if \"(plural)\" is included, ")
                .append("make sure the translation reflects the plural form, and remove \"(plural)\" in the translated output.\n");
        sb.append("For example: I eat -> Yo como(If the target language is Spanish), You are going (plural) -> Vosotros vais");

        for(String phrase : phrases){
            sb.append("\n").append(phrase);
        }

        String prompt = sb.toString();
        //System.out.println("Prompt: {" + prompt+"}");

        String response = getChatCompletion(prompt);
        //System.out.println("ChatGPT: " + response);

        //parse response to get List<String>
        List<String> translated = new ArrayList<>();
        for (String line : response.split("\n")) {
            translated.add(line.trim());
        }

        if(translated.size() != phrases.size()){
            System.out.println("ERROR! - incorrect size");
            System.out.println("output size : " + translated.size());
            System.out.println("input size : " + phrases.size());
            System.out.println(translated);
            System.out.println(phrases);
        }

        //System.out.println("Returning List: "+translated.toString());

        return translated;
    }




    public static void main(String[] args) {
        ConfigurationFile configFile = ConfigurationFile.getInstance();
        String apiKey = configFile.getValueByKey("API_KEY");
        System.out.println("API Key: [" + apiKey + "]");
        List<String> s = Arrays.asList("Hello" , "name", "teacher", "student");
        List<String> translatedPhrases = OpenAIClient.getInstance().translateAll(s);

        System.out.println(translatedPhrases.size());

        System.out.println(translatedPhrases.get(0));
        System.out.println(translatedPhrases.get(1));
        System.out.println(translatedPhrases.get(2));
        System.out.println(translatedPhrases.get(3));

        String text = "help";
        String translate = OpenAIClient.getInstance().translate(text);
        System.out.println(text + " : " + translate);

    }

}