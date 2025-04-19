import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XML_ParserTest {


    @Test
    public void testAddTranslatedPanelsOutputMatchesExpected() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        File root = Helper.getRootDirectory();
        File inputFile = new File(root, "Resources/XMLinput/Sprint4Verbs.xml");


        XML_Parser parser = new XML_Parser(inputFile);
        parser.addTranslatedPanels();

        parser.writeXML("Resources/XMLoutput/", "Verbs_Spanish_Test.xml");


        File testOutputFile = new File(root, "Resources/XMLoutput/Verbs_Spanish_Test.xml");
        List<String> actualLines = Files.readAllLines(testOutputFile.toPath());


        File expectedOutputFile = new File(root, "Resources/XMLoutput/Sprint4verbs.xml");
        List<String> expectedLines = Files.readAllLines(expectedOutputFile.toPath());


        if (actualLines.size() != expectedLines.size()) {
            Assertions.fail("Line count mismatch: expected " + expectedLines.size() +
                    " lines but got " + actualLines.size());
        }

        int size= actualLines.size();
        for (int i = 0; i < size; i++) {
            String actual = i < actualLines.size() ? actualLines.get(i) : "<missing line>";
            String expected = i < expectedLines.size() ? expectedLines.get(i) : "<missing line>";

            Assertions.assertEquals(expected, actual, "Mismatch at line " + (i));
        }

        testOutputFile.delete();

    }


    @Test
    public void testValidateComicXml() throws IOException {

        File root = Helper.getRootDirectory();


        File valid = new File(root, "Resources/XMLinput/Sprint5scenes.xml");
        FileParser.ensureFolderExists(valid);
        assertTrue(XML_Parser.validateComicXml(valid));


        File invalid = new File(root, "Resources/XMLinput/invalidTest.xml");
        FileParser.ensureFolderExists(invalid);
        Assertions.assertFalse(XML_Parser.validateComicXml(invalid));

    }

    @Test
    public void testSprint5FullPipelineExecution() throws Exception {
        File root = Helper.getRootDirectory();
        String inputPath = "Resources/XMLinput/Sprint5scenes.xml";
        String outputFolder = "Resources/XMLoutput/";

        File inputFile = new File(root, inputPath);

        //Assert 1: Load and extract first scene
        XML_Parser parser = new XML_Parser(inputFile);
        List<Node> newScenes = new ArrayList<>();

        Node firstScene = parser.getDoc().getElementsByTagName("scene").item(0);
        //System.out.println("First Scene Print:\n"+XML_Parser.nodeToString(firstScene));
        assertNotNull(firstScene, "First scene should exist in input XML");


        Node scenecopy = firstScene.cloneNode(true);
        String sceneDescription = parser.getNarrativeArc(firstScene);
        System.out.println("Scene Description:\n" + sceneDescription);

        List<String> sceneDialogue = parser.getDialogue(sceneDescription);
        System.out.println("Dialogue:\n" + sceneDialogue); //Dialogue Changes across different runs.

        String fullDialogue = String.join("\n", sceneDialogue);

        System.out.println("Full Dialogue:\n" + fullDialogue);

        parser.addDialogue(scenecopy, fullDialogue);
        newScenes.add(scenecopy);

        //Assert 2: Create dialogue XML and write to disk
        Document dialogueDoc = parser.scenesToDoc(newScenes);
        String dialogueOutputName = "Sprint5_DialogueOutput_Test.xml";
        parser.docToXml(dialogueDoc, outputFolder + dialogueOutputName);

        File dialogueOutputFile = new File(root, outputFolder + dialogueOutputName);
        assertTrue(dialogueOutputFile.exists(), "Dialogue output file should be created");

        //Assert 3: Add translated panels and write final output
        String finalOutputName = "Sprint5_InterwovenOutput_Test.xml";
        XML_Parser parser2 = new XML_Parser(dialogueOutputFile);
        parser2.addTranslatedPanels(); //Addes cloned panel with translation.
        parser2.writeXML(outputFolder, finalOutputName); //Creates new XML file.

        File finalOutputFile = new File(root, outputFolder + finalOutputName);
        assertTrue(finalOutputFile.exists(), "Final interwoven output should be created");

        dialogueOutputFile.delete();
        finalOutputFile.delete();

    }

    @Test
    public void testSplitPanel_TwoBalloons() throws Exception {
        //Create a panel with two balloon.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element panel = doc.createElement("panel");

        // Left side
        Element left = doc.createElement("left");
        Element fig1 = doc.createElement("figure");
        Element name1 = doc.createElement("name");
        name1.setTextContent("Alfie");
        fig1.appendChild(name1);
        left.appendChild(fig1);
        Element balloon1 = doc.createElement("balloon");
        Element content1 = doc.createElement("content");
        content1.setTextContent("Hi from Alfie.");
        balloon1.appendChild(content1);
        left.appendChild(balloon1);

        // Right side
        Element right = doc.createElement("right");
        Element fig2 = doc.createElement("figure");
        Element name2 = doc.createElement("name");
        name2.setTextContent("Betty");
        fig2.appendChild(name2);
        right.appendChild(fig2);
        Element balloon2 = doc.createElement("balloon");
        Element content2 = doc.createElement("content");
        content2.setTextContent("Hello from Betty.");
        balloon2.appendChild(content2);
        right.appendChild(balloon2);

        panel.appendChild(left);
        panel.appendChild(right);

        //Using split method
        XML_Parser parser = new XML_Parser(new File("Resources/XMLinput/Sprint5scenes.xml"));
        List<Element> result = parser.splitPanel(panel);

        // Assert
        assertEquals("Should return 2 panels", 2, result.size());

        for (Element splitPanel : result) {
            NodeList balloons = splitPanel.getElementsByTagName("balloon");
            assertEquals("Each split panel should have exactly 1 balloon", 1, balloons.getLength());
        }

        System.out.println("Test Passed: Two-balloon panel successfully split into two panels.");
    }

    @Test
    public void testSplitPanel_SingleBalloon() throws Exception {
        //Create a panel with only one panel.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element panel = doc.createElement("panel");
        Element left = doc.createElement("left");
        Element fig = doc.createElement("figure");
        Element name = doc.createElement("name");
        name.setTextContent("Alfie");
        fig.appendChild(name);
        left.appendChild(fig);
        Element balloon = doc.createElement("balloon");
        Element content = doc.createElement("content");
        content.setTextContent("Only one.");
        balloon.appendChild(content);
        left.appendChild(balloon);
        panel.appendChild(left);

        // Using split method
        XML_Parser parser = new XML_Parser(new File("Resources/XMLinput/Sprint5scenes.xml"));
        List<Element> result = parser.splitPanel(panel);

        // Assert
        assertEquals("Panel with 1 balloon should not be split", 1, result.size());
        assertEquals("Still has 1 balloon", 1,
                result.get(0).getElementsByTagName("balloon").getLength());

        System.out.println("Test Passed: One-balloon panel not split.");
    }
}
