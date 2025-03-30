import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationFileTest {

    private static TranslationFile translationFile;
    private static File tempFile;
    private static Map<String, String> translations;

    private static TranslationFile instance;
    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("Translation_Test_", ".txt");

        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("Hello : Hola");
            writer.println("Spanish : Española");
            writer.println("Car : Auto");
            writer.println("Pink : Rosa");
        }

        translationFile = TranslationFile.getInstance(tempFile);
    }

    @Test
    void testInstanceIsNotNull() {
        assertNotNull(translationFile, "Instance should not be null");
    }

    @Test
    void testGetTranslationsIsNotNull() {
        translations = translationFile.getTranslations();

        assertNotNull(translations);
        assertEquals("Hola", translations.get("Hello")); //Pre-defined translations exist.
        assertEquals("Española", translations.get("Spanish"));
        assertEquals("Auto", translations.get("Car"));
        assertEquals("Rosa", translations.get("Pink"));
    }

    @Test
    void testInstanceReturnsSameInstance() {
        TranslationFile translationFile2 = TranslationFile.getInstance();
        assertSame(translationFile, translationFile2, "Instance should be the same");
    }

    @Test
    void testGetTranslations() {
        Map<String, String> translations = translationFile.getTranslations();
        assertNotNull(translations, "Translations should not be null");
    }


    @Test
    void testTranslate() {
        String response = translationFile.translate("Hello"); //Should Find it in the translations HashMap.
        assertEquals("Hola", response);
        //System.out.println("Response 1: "+response);

        String response2 = translationFile.translate("Chicken"); //Needs to make a GPT Call.
        assertEquals("Pollo", response2);
        //System.out.println(response2);
    }

    @Test
    void testWriteTranslationMapping() {
        translationFile.writeTranslationMapping("Table", "Mesa");
        translationFile.loadTranslationsFile();
        assertEquals("Mesa", translationFile.getTranslations().get("Table")); //New translations added work.
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile.toPath());
    }

}
