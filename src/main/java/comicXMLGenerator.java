import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class comicXMLGenerator {

    public static Document generateSceneXML(VignetteSchema schema) throws ParserConfigurationException {
        return SceneGeneratorManager.generateScene(schema);
    }

    public static void SceneExporter() {
        try {
            VignetteSchema schema = new VignetteManager().getRandomSchema();
            Document sceneDoc = generateSceneXML(schema);

            Document fullDoc = wrapSceneWithComic(sceneDoc);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            File root = Helper.getRootDirectory();
            String filePath = new File(root, "Resources/scene_" + timestamp + ".xml").getAbsolutePath();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(fullDoc), new StreamResult(new File(filePath)));

            System.out.println("XML written to: " + filePath);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add the `<comic>` tag after the document has been generated.
     */
    private static Document wrapSceneWithComic(Document sceneDoc) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document fullDoc = builder.newDocument();

        Element comic = fullDoc.createElement("comic");
        fullDoc.appendChild(comic);

        Node importedScene = fullDoc.importNode(sceneDoc.getDocumentElement(), true);
        comic.appendChild(importedScene);

        return fullDoc;
    }
}
