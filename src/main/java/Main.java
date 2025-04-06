import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args)  {
        File root = Helper.getRootDirectory();
        String path = "Resources/Verbs/specification.xml";
        File f = new File(root, path);
        try {
            XML_Parser parser = new XML_Parser(f);
            TranslationFile t = TranslationFile.getInstance();
            //t.translateAllPhrases(parser.getBalloons());
            parser.addTranslatedPanels();
            parser.writeXML();

        }
        catch (Exception e){
            System.out.println("Error: exception building DOM from XML");
            e.printStackTrace();
        }
    }

}