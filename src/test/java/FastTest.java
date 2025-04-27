import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class FastTest {

    @Test
    public void testin2(){
            try {
                // Parse the first XML file
                File xmlFile1 = new File(FileParser.getRootDirectory() + "/Resources/XMLoutput/left_scenes.xml");
                System.out.println("File pathhhh: "+xmlFile1.getAbsolutePath());

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc1 = builder.parse(xmlFile1);
                doc1.getDocumentElement().normalize();

                XmlWriter.removeTranslatedPanel(doc1);

                saveDocumentToFile(doc1, FileParser.getRootDirectory() + "/Resources/XMLoutput/left_scenes_untranslated.xml");


            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void saveDocumentToFile(Document doc, String outputPath) {
        try {
            // Create a TransformerFactory and a Transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Pretty print the XML

            // Prepare the DOM document for writing
            DOMSource source = new DOMSource(doc);

            // Set the output file
            StreamResult result = new StreamResult(new File(outputPath));

            // Write the data to the output file
            transformer.transform(source, result);

            System.out.println("File saved to: " + outputPath);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
