import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String schedule = ConfigurationFile.getInstance().getValueByKey("LESSON_SCHEDULE");
        System.out.println("Lesson Schedule - " + schedule);

        ConfigurationFile cf = ConfigurationFile.getInstance();
        cf.updateLanguage();
        System.out.println("Creating conjugation lesson ... ");
        XmlWriter.createConjugationLesson();
        System.out.println("Creating left scene, whole scene ... ");
        XmlWriter.writeTranslatedVignettes();
        System.out.println("Creating sprint6_FinalAudioFileTranslated" + ConfigurationFile.getTargetLanguage());
        // TODO
        XmlWriter xmlWriter = new XmlWriter(FileParser.getFile("Resources/XMLinput/Sprint4Verbs.xml"));
        xmlWriter.createComicFullLesson();

    }


}
