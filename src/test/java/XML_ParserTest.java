import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


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
    }


    @Test
    public void testValidateComicXml() throws IOException {

        File root = Helper.getRootDirectory();


        File valid = new File(root, "Resources/XMLinput/Sprint5scenes.xml");
        FileParser.ensureFolderExists(valid);
        Assertions.assertTrue(XML_Parser.validateComicXml(valid));


        File invalid = new File(root, "Resources/XMLinput/invalidTest.xml");
        FileParser.ensureFolderExists(invalid);
        Assertions.assertFalse(XML_Parser.validateComicXml(invalid));

    }
}
