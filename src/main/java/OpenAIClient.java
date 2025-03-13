import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.json.JSONObject;

public class OpenAIClient {
    private String apiKey;
    private String model;
    private String url;

    public OpenAIClient(ConfigurationFile config) {
        ConfigurationFile configFile = new ConfigurationFile();
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
            requestBody.put("messages", new JSONObject[] {
                    new JSONObject().put("role", "user").put("content", prompt)
            });

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            return parseResponse(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching response";
        }
    }

    private String parseResponse(String jsonResponse) {
        JSONObject responseObj = new JSONObject(jsonResponse);
        if (responseObj.has("choices")) {
            return responseObj.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        }
        return "Invalid response format";
    }

    public static void main(String[] args) {
        ConfigurationFile config = new ConfigurationFile();
        OpenAIClient client = new OpenAIClient(config);
        String prompt = "Tell me a joke";
        String response = client.getChatCompletion(prompt);
        System.out.println("Response: " + response);
    }
}
