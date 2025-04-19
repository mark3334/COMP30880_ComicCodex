import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException {
        System.out.println(("Pay attention to tense and person; if \\\"(plural)\\\" is included, \" +\n" +
                "                \"make sure the translation reflects the plural form, and remove \\\"(plural)\\\" in the translated output"));
        XmlWriter.main(args);
    }
}