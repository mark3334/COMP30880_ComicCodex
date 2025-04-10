import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class XML_ParserTest {

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
