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
    private Document doc;
    public XML_Parser(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();
        this.doc = document;
        Element root = this.doc.getDocumentElement();
        System.out.println("Root element: " + root.getNodeName());
        printFigures();
    }
    public void printFigures(){
        Node figuresNode = this.doc.getElementsByTagName("figures").item(0);
        System.out.println("Figures element: " + figuresNode.getNodeName());
        NodeList figureNodes = figuresNode.getChildNodes();
        //For figure in Figures
        for (int i = 0; i < figureNodes.getLength(); i++) {
            Node n = figureNodes.item(i);
            //for(Node attribute : n.getChildNodes())
            if (n.getNodeType() == Node.ELEMENT_NODE) { // ignores #text:  line
                //System.out.println(n.getNodeName() + ": " + n.getTextContent().trim());
                NodeList figureTags = n.getChildNodes();
                for (int j = 0; j < figureTags.getLength(); j++) {
                    Node child = figureTags.item(j);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println("  └ " + child.getNodeName() + ": " + child.getTextContent().trim());
                    }
                }
            }
            System.out.println();
        }
    }


//        public NodeList getBalloons(Document doc){
//            //Nodelist figuresNode = document.getElementsByTagName("figures").item(0);
//        }


          //public print

    public static void main(String[] args) throws ParserConfigurationException {

        String path = "/Resources/Lesson 3/lesson 3 specification.xml";
        File root = Helper.getRootDirectory();
        File f = new File(root, path);
        try {
            XML_Parser parser = new XML_Parser(f);
        }
        catch (Exception e){
            System.out.println("Error: exception building DOM from XML");
            e.printStackTrace();
            return;
        }
    }
}
