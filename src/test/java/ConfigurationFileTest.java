import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationFileTest {
    private static ConfigurationFile configFile;
    private static File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("Configuration_Test_File", ".txt"); //creates the temporary file.

        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("\"TARGET_LANGUAGE\" ||| \"Spanish\"");
            writer.println("\"SOURCE_LANGUAGE\" ||| \"English\"");
            writer.println("\"API_KEY\" ||| \"xyzbda\"");
            writer.println("\"MODEL\" ||| \"gpt-4\"");
            writer.println("\"COMPLETIONS_URL\" ||| \"https://ChocolateCheeseChips.com\"");
        }


        configFile = ConfigurationFile.getInstance(tempFile); //Injecting the custom file.
    }

    @Test
    void testInstanceIsNotNull() {
        assertNotNull(configFile, "ConfigurationFile instance should not be null");
    }

    @Test
    void testInstanceReturnsSameInstance() {
        ConfigurationFile configFile2 = ConfigurationFile.getInstance();
        assertSame(configFile, configFile2, "Both instance Should be the same object in memory");
    }

    @Test
    void testGetConfigMap() {
        Map<String, String> configMap = configFile.getConfigMap();
        assertNotNull(configMap);
        assertFalse(configMap.isEmpty(), "Config Map should not be empty");
        assertEquals(5, configMap.size(), "Config Map should have five entry");
    }

    @Test
    void testGetValueByKeyReturnsValue() {
        String target_language = configFile.getValueByKey("TARGET_LANGUAGE");
        String source_language = configFile.getValueByKey("SOURCE_LANGUAGE");
        String api_Key = configFile.getValueByKey("API_KEY");
        String model = configFile.getValueByKey("MODEL");
        String completions_url = configFile.getValueByKey("COMPLETIONS_URL");


        assertEquals("gpt-4", model, "Should return actual value for Model");
        assertEquals("xyzbda", api_Key, "Should return actual value for API Key");
        assertEquals("Spanish", target_language, "Should return actual value for Language");
        assertEquals("https://ChocolateCheeseChips.com", completions_url, "Should return actual value for URL");
    }

    @Test
    void testGetValueByKeyReturnsError() {
        String randomKey = configFile.getValueByKey("RANDOM_KEY");
        assertEquals("Key not found", randomKey, "Should \"Key not found\" for wrong key");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null) {
            System.out.println("Deleting temp config file"+ tempFile.getAbsolutePath());
            Files.deleteIfExists(tempFile.toPath());
        }
    }
}