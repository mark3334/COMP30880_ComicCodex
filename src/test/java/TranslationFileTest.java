import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationFileTest {

    @Test
    void testGetTargetLanguage(){
        TranslationFile tFile = new TranslationFile("Test_Text_Files/TranslationFileTest.txt");
        assertEquals("Spanish", OpenAIClient.getLanguage());
    }

}
