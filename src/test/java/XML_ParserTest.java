import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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


        File expectedOutputFile = new File(root, "Resources/XMLoutput/Verbs_Spanish");
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

}
