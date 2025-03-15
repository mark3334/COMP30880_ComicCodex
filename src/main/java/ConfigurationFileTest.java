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
        assertEquals(8, configMap.size(), "Config map should have 8 entries");
    }
}