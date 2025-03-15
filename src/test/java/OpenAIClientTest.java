import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OpenAIClientTest {
    private OpenAIClient client;

    /**
     * Initializes the OpenAIClient instance before each test.
     */
    @BeforeEach
    void setUp() {
        client = new OpenAIClient();
    }

    /**
     * Tests whether `saveContext()` correctly stores conversation history.
     */
    @Test
    void testSaveContext() {
        client.saveContext("Hello", "Hi there!");
        client.saveContext("How are you?", "I'm fine, thanks!");

        assertEquals(4, OpenAIClient.getContext().length(), "Should include four context messages");
    }

    /**
     * Tests whether `getContext()` returns the correct JSON structure.
     */
    @Test
    void testGetContext() {
        client.saveContext("Hello", "Hi!");
        assertEquals(2, OpenAIClient.getContext().length(), "Should include two context messages");
    }

}
