import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OpenAIClientTest {


    /**
     * Initializes the OpenAIClient instance before each test.
     */

    /**
     * Tests whether `saveContext()` correctly stores conversation history.
     */
    @BeforeEach
    void setUp() {
        OpenAIClient.emptyContext();
    }
    //emptyContext
    @Test
    void testSaveContext() {
        OpenAIClient.saveContext("Hello", "Hi there!");
        OpenAIClient.saveContext("How are you?", "I'm fine, thanks!");

        assertEquals(4, OpenAIClient.getContext().length(), "Should include four context messages");
    }

    /**
     * Tests whether `getContext()` returns the correct JSON structure.
     */
    @Test
    void testGetContext() {
        OpenAIClient.saveContext("Hello", "Hi!");
        assertEquals(2, OpenAIClient.getContext().length(), "Should include two context messages");
    }

}
