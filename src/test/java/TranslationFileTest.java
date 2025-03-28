import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationFileTest {

    private static TranslationFile instance;
    @BeforeAll
    static void setUp() {
        instance = TranslationFile.getInstance();
    }

    @Test
    void testInstanceIsNotNull() {
        assertNotNull(instance, "Instance should not be null");
    }

    @Test
    void testInstanceReturnsSameInstance() {
        TranslationFile translationFile2 = TranslationFile.getInstance();
        assertSame(instance, translationFile2, "Instance should be the same");
    }

    @Test
    void testGetTranslations() {
        Map<String, String> translations = instance.getTranslations();
        assertNotNull(translations, "Translations should not be null"); //Should already have the Translations Dictionary.
    }


    @Test
    void testTranslate() {
        String response = instance.translate("Hello");
        System.out.println(response);
    }

}
