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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        printBalloons();
    }
    public void printFigures(){
        Node figuresNode = this.doc.getElementsByTagName("figures").item(0);
        System.out.println("Figures element: " + figuresNode.getNodeName());
        NodeList figureNodes = figuresNode.getChildNodes();
        ArrayList<Map<String,String>> Figures = new ArrayList<>();
        String key = "", value = "";
        //For figure in Figures
        System.out.println("Number of figure Nodes: " + figureNodes.getLength());
        for (int i = 0; i < figureNodes.getLength(); i++) {
            Node n = figureNodes.item(i);
            //for(Node attribute : n.getChildNodes())
            if (n.getNodeType() == Node.ELEMENT_NODE) { // ignores #text:  line empty text line counts a node I think
                Map<String,String> figureMap = new HashMap<>();
                //System.out.println(n.getNodeName() + ": " + n.getTextContent().trim());
                NodeList figureTags = n.getChildNodes();
                for (int j = 0; j < figureTags.getLength(); j++) {
                    Node child = figureTags.item(j);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        key = child.getNodeName();
                        value = child.getTextContent().trim();
                        figureMap.put(key,value);
                    }
                }
                System.out.println(figureMap);
                Figures.add(figureMap);;
            }
        }
        System.out.println(Figures);
    }


    public void printBalloons(){
        NodeList balloonNodes = this.doc.getElementsByTagName("balloon");
        for (int i = 0; i < balloonNodes.getLength(); i++) {
            Node n = balloonNodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Balloon text content: " + n.getTextContent().trim());
            }
        }
    }

    public NodeList getBalloonNodes(){
        return this.doc.getElementsByTagName("balloon");
    }


    public static void main(String[] args) throws ParserConfigurationException {
        File root = Helper.getRootDirectory();
        //String path = "/Resources/Lesson 3/lesson 3 specification.xml";
        String path = "Resources/Verbs/specification.xml";
        String outputPath = "Resources/Verbs/" + Helper.getTargetLanguage();
        File fOutput =  new File(root, path);
        //check if output file exists else create it
        System.out.println(outputPath);
        File f = new File(root, path);
        try {
            XML_Parser parser = new XML_Parser(f);
        }
        catch (Exception e){
            System.out.println("Error: exception building DOM from XML");
            e.printStackTrace();
        }
    }
}
