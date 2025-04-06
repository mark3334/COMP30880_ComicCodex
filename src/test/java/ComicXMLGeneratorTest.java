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

    @Test
    public void testGenerateSceneXML() throws ParserConfigurationException {

        //No left text scene.
        VignetteSchema testSchema = new VignetteSchema("Giga_Chad", "Wow", "", "Suprised","Gym");
//        System.out.println(testSchema.toString());
//        System.out.println(Arrays.toString(SceneGeneratorManager.getFeatureVector(testSchema)));


        //Only combined text scene.
        VignetteSchema testSchema2 = new VignetteSchema("Giga_Chad", "Wow", "", "","Gym");
//        System.out.println(testSchema2.toString());
//        System.out.println(Arrays.toString(SceneGeneratorManager.getFeatureVector(testSchema2)));


        //Only left text scene.
        VignetteSchema testSchema3 = new VignetteSchema("Giga_Chad", "", "Wow", "","");
//        System.out.println(testSchema3.toString());
//        System.out.println(Arrays.toString(SceneGeneratorManager.getFeatureVector(testSchema3)));

        //Full text Scene.
        VignetteSchema testSchema4 = new VignetteSchema("Giga_Chad", "Wow", "Wow", "Suprised","Gym");
//        System.out.println(testSchema4.toString());
//        System.out.println(Arrays.toString(SceneGeneratorManager.getFeatureVector(testSchema4)));


        VignetteSchema[] schemas = {
                testSchema, testSchema2, testSchema3, testSchema4
        };

        for (int i = 0; i < schemas.length; i++) {
            Document result = ComicXMLGenerator.generateSceneXML(schemas[i]);
            assertNotNull(result, "Result " + (i + 1) + " should not be null");
        }
    }
}
