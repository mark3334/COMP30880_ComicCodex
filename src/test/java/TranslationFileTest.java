import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TranslationFileTest {

    @Test
    void testGetTargetLanguage(){
        TranslationFile tFile = new TranslationFile("TranslationFile.txt");
        assertEquals("Spanish", tFile.getTargetLanguage());
    }

    @Test
    void testLoadTranslationsFile(){
        //TODO

    }

    @Test
    void TestAddTranslationMapping(){
        TranslationFile tFile = new TranslationFile("TranslationFile.txt");
        tFile.addTranslationMapping("Spanish_Test_Input", "Spanish+Test_Output");
    }
}
