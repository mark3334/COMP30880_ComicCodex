import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.w3c.dom.Document;import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ComicXMLGeneratorTest {

    @Test
    public void testWrapSceneWithComic() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document sceneDoc = builder.newDocument();

        Element scene = sceneDoc.createElement("scene");
        sceneDoc.appendChild(scene);

        Document wrappedDoc = ComicXMLGenerator.wrapSceneWithComic(sceneDoc);

        assertNotNull(wrappedDoc, "Document should not be null");

        Element root = wrappedDoc.getDocumentElement();
        Assertions.assertEquals("comic", root.getNodeName(), "Root element should be <comic>");

        Element importedScene = (Element) root.getFirstChild();
        Assertions.assertEquals("scene", importedScene.getNodeName(), "Child of <comic> should be <scene>");
    }
}
