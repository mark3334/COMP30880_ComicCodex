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

    @Test
    void TestAddAndLoadTranslations(){
        TranslationFile tFile = new TranslationFile("Test_Text_Files/TranslationFileTest.txt");
        tFile.addTranslationMapping("Spanish_Test_Input", "Spanish+Test_Output");
        tFile.addTranslationMapping("Hello", "Bonjour");
        tFile.addTranslationMapping("Bye", "Ciao");

         Map<String, String> mappings = tFile.loadTranslationsFile();

        assertEquals("Spanish+Test_Output", mappings.get("Spanish_Test_Input"));
        assertEquals("Bonjour", mappings.get("Hello"));
        assertEquals("Ciao", mappings.get("Bye"));

        assertEquals(3, mappings.size());
    }
}
