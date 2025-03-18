import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationFileTest {
    private ConfigurationFile configFile;

    /**
     * To initialize the configuration file, that's not the real deal.
     */
    @BeforeEach
    void setUp() throws IOException {
        File tempFile = new File("ConfigTest.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("\"language\" : \"Spanish\"\n");
            writer.write("\"API_KEY\" : \"sk-test-key\"\n");
            writer.write("\"ORG_KEY\" : \"org-test-key\"\n");
            writer.write("\"MODEL\" : \"gpt-4o-mini\"\n");
            writer.write("\"COMPLETIONS_URL\" : \"https://api.openai.com/v1/chat/completions\"\n");
            writer.write("\"EMBEDDINGS_URL\" : \"https://api.openai.com/v1/embeddings\"\n");
            writer.write("\"MODELS_URL\" : \"https://api.openai.com/v1/models\"\n");
            writer.write("\"USER_MODE\" : \"True\"\n");
        }

        configFile = new ConfigurationFile();
    }


    /**
     * Testing if the config map is correctly loaded.
     */
    @Test
    void testConfigMapLoading() {
        Map<String, String> configMap = configFile.getConfigMap();
        assertNotNull(configMap);
        assertEquals(9, configMap.size(), "Config map should have 9 entries");
    }

    @Test
    void testGetValueByKey_() {
        assertEquals("Key not found", configFile.getValueByKey("LOLIPOP"));
        assertEquals("Spanish", configFile.getValueByKey("language"));
        assertEquals("gpt-4o-mini", configFile.getValueByKey("MODEL"));
    }

    @Test
    void testFindIndexByKey() {
        assertTrue(configFile.findIndexByKey("API_KEY") >= 0);
        assertTrue(configFile.findIndexByKey("MODEL") >= 0);
        assertEquals(-1, configFile.findIndexByKey("INVALID_KEY"));
    }
}