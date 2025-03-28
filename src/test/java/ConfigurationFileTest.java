import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationFileTest {
    private static ConfigurationFile configFile;

    @BeforeAll
    static void setUp() {
        configFile = ConfigurationFile.getInstance();
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
    }

    @Test
    void testGetValueByKeyReturnsValue() {
        String model = configFile.getValueByKey("MODEL");
        assertNotEquals("Key not found", model, "Should return actual value for Model");
    }

    @Test
    void testGetValueByKeyReturnsError() {
        String randomKey = configFile.getValueByKey("RANDOM_KEY");
        assertEquals("Key not found", randomKey, "Should \"Key not found\" for wrong key");
    }
}