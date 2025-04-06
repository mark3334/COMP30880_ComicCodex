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
import java.io.IOException;

public class ComicXMLGenerator {

    /**
     * Generates a DOM XML Document from a given VignetteSchema.
     * This method uses the SceneGeneratorManager to select the appropriate
     * scene generation template and return an XML structure.
     *
     * @param schema A VignetteSchema that holds the scene's pose/text/background data.
     * @return Document representing the generated XML scene.
     * @throws ParserConfigurationException if document creation fails.
     */
    public static Document generateSceneXML(VignetteSchema schema) throws ParserConfigurationException {
        return SceneGeneratorManager.generateScene(schema);
        //Testing Problems, might return null if not a type of SceneGenInterface.
    }

    /**
     * Loads a random VignetteSchema, generates an XML scene from it,
     * wraps the scene in a <comic> root tag, and saves it as an XML file
     * in the Resources/XMLoutput folder. Adds a timestamp to the filename.
     *
     * @throws TransformerException if there is an issue writing XML.
     * @throws IOException if there is a problem creating the output directory.
     */
    public static void writeXmlToFolder(Document doc, String filename, File folder) throws TransformerException, IOException {
//    File root = Helper.getRootDirectory();
//    File folder = new File(root, "Resources/XMLoutput");
//
//    //Get a random vignette schema and generate its scene.
//    VignetteSchema schema = new VignetteManager().getRandomSchema();
//    Document sceneDoc = generateSceneXML(schema);
//    Document fullDoc = wrapSceneWithComic(sceneDoc);

    //Create the output folder if it doesn't exist
    if (!folder.exists()) {
        boolean created = folder.mkdirs();
        if (created) {
            System.out.println("Created directory: " + folder.getAbsolutePath());
        } else {
            throw new IOException("Failed to create directory: " + folder.getAbsolutePath());
        }
    }

    File outputFile = new File(folder, filename);

    //Create a timestamped filename
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
//    String timestamp = LocalDateTime.now().format(formatter);
//    File outputFile = new File(folder, "scene_" + timestamp + ".xml");

    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.transform(new DOMSource(doc), new StreamResult(outputFile));

    System.out.println("XML written to: " + outputFile.getAbsolutePath());
}

    /**
     * Wraps a generated scene XML document inside a new <comic> root element.
     * This ensures the output follows the required structure for comics lessons.
     *
     * @param sceneDoc The original document with a <scene> root.
     * @return A new Document with <comic> as the root and <scene> nested inside.
     * @throws ParserConfigurationException if document creation fails.
     */
    public static Document wrapSceneWithComic(Document sceneDoc) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document fullDoc = builder.newDocument();

        //Create root <comic> element
        Element comic = fullDoc.createElement("comic");
        fullDoc.appendChild(comic);

        //Import the <scene> node from the original sceneDoc
        Node importedScene = fullDoc.importNode(sceneDoc.getDocumentElement(), true);
        comic.appendChild(importedScene);

        return fullDoc;
    }
}
