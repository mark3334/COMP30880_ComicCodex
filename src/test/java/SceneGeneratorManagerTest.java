import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

public class SceneGeneratorManagerTest {

    @Test
    public void testGenerateSceneReturnsDocument() throws ParserConfigurationException {
        VignetteManager manager = new VignetteManager();
        VignetteSchema schema = manager.getRandomSchema();

        assertNotNull(schema, "Schema should not be null for testing");

        Document doc = SceneGeneratorManager.generateScene(schema);
        assertNotNull(doc, "Generated scene document should not be null");

        assertEquals("scene", doc.getDocumentElement().getTagName(), "Root element should be <scene>");
    }

    @Test
    public void testGenerateDomWithFullModel() throws ParserConfigurationException {
        VignetteManager manager = new VignetteManager();
        VignetteSchema schema = manager.getRandomSchema();

        assertNotNull(schema);

        Document doc = SceneGeneratorManager.generateDom(schema, "full");
        assertNotNull(doc);

        assertEquals("scene", doc.getDocumentElement().getTagName());
    }

    @Test
    public void testGenerateDomWithInvalidModelThrowsException() {
        VignetteManager manager = new VignetteManager();
        VignetteSchema schema = manager.getRandomSchema();

        assertNotNull(schema);

        assertThrows(IllegalArgumentException.class, () -> {
            SceneGeneratorManager.generateDom(schema, "unknown_model_type");
        });
    }
}
