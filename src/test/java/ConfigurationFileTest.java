import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationFileTest {
    private final ConfigurationFile configFile = ConfigurationFile.getInstance();

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
        assertEquals("Spanish", configFile.getValueByKey("Target_Language"));
        assertEquals("gpt-4o-mini", configFile.getValueByKey("MODEL"));
    }

}