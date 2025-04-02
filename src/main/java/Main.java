import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        comicXMLGenerator.writeXmlToFolder();
    }

}