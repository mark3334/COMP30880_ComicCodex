import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenAIClientTest {
    private OpenAIClient client;

    /**
     * Initializes the OpenAIClient instance before each test.
     */

    /**
     * Tests whether `saveContext()` correctly stores conversation history.
     */
    @BeforeEach
    void setUp() {
        client = OpenAIClient.getInstance();
        client.emptyContext(); // Reset conversation history
    }

    @Test
    void testSaveContext() {
        client.saveContext("Hello", "Hi there!");
        client.saveContext("How are you?", "I'm fine, thanks!");

        assertEquals(4, client.getContext().length(), "Should include four context messages");
    }

    /**
     * Tests whether `getContext()` returns the correct JSON structure.
     */
    @Test
    void testGetContext_testEmptyContext() {
        client.saveContext("Hello", "Hi!");
        JSONArray context = client.getContext();
        assertEquals(2, context.length(), "Should include two context messages (prompt + response)");
        assertEquals("user", context.getJSONObject(0).getString("role"));
        assertEquals("assistant", context.getJSONObject(1).getString("role"));
    }

    @Test
    void testEmptyContext() {
        client.saveContext("Test", "Response");
        client.emptyContext();
        assertEquals(0, client.getContext().length(), "Context should be empty after clearing");
    }

    @Test
    void testGetLanguage() {
        String lang = client.getLanguage();
        assertNotNull(lang);
        assertFalse(lang.isEmpty());
        System.out.println("Language from config: " + lang);
    }

    @Test
    void testTranslate() {

        String response = client.translate("Hello");
        assertNotNull(response);
        assertFalse(response.trim().isEmpty());
        System.out.println("Translation: " + response);
    }

    @Test
    void testTranslateAll() {
        List<String> input = Arrays.asList("Hello", "Name", "Teacher", "Student");
        List<String> translated = client.translateAll(input);

        assertNotNull(translated);
        assertEquals(4, translated.size());
        for (String word : translated) {
            assertFalse(word.trim().isEmpty());
        }

        System.out.println("Translations: " + translated);
    }

}
