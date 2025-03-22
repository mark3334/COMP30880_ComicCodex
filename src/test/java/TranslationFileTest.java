import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TranslationFileTest {

    @Test
    void testGetTargetLanguage(){
        TranslationFile tFile = new TranslationFile("TranslationFile.txt");
        assertEquals("Spanish", tFile.getTargetLanguage());
    }
}
