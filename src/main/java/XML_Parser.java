import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class XML_Parser {

    public XML_Parser(File file)  {
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();
            System.out.println("Root element: " + root);
            Node figuresNode = document.getElementsByTagName("figures").item(0);
            System.out.println("Figures element: " + figuresNode);
            NodeList figureNodes = figuresNode.getChildNodes();
            //For figure in Figures
            for (int i = 0; i < figureNodes.getLength(); i++) {
                Node n = figureNodes.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    System.out.println(n.getNodeName() + ": " + n.getTextContent().trim());
                }
            }
        }
        catch (Exception e){
            System.out.println("Error: exception building DOM from XML");
            e.printStackTrace();
            return;
        }
    }
    public static void main(String[] args) throws ParserConfigurationException {

        String path = "/Resources/Lesson 3/lesson 3 specification.xml";
        File root = Helper.getRootDirectory();
        File f = new File(root, path);
        XML_Parser parser = new XML_Parser(f);

    }
}
