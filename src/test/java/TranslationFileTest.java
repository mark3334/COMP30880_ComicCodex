import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TranslationFileTest {

    @Test
    void testGetTargetLanguage(){
        ConfigurationFile config = new ConfigurationFile();
        assertEquals("Spanish", config.getValueByKey("language"));
    }
}
