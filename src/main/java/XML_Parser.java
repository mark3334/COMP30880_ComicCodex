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
import java.util.List;
import java.util.Map;

public class XML_Parser {
    private Document doc;
    private TranslationFile t;

    public XML_Parser(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();
        this.doc = document;
        this.t = TranslationFile.getInstance();
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

    public void Panels() {
        NodeList panelNodes = this.doc.getElementsByTagName("panel");
        NodeList balloonNodes = this.doc.getElementsByTagName("balloon");
        System.out.println("Number of panels  - " + panelNodes.getLength());
        System.out.println("Number of balloons - " + balloonNodes.getLength());
        for (int i = 0; i < panelNodes.getLength(); i++) {
            Node panel = panelNodes.item(i);
            //System.out.println("Node name - " + panel.getNodeName());
            Element panelEl = (Element) panel;
            NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
            System.out.println("Number of balloons - " + balloons.getLength());
            if(balloons.getLength() == 1){
                //copy and print panel to xml.
                Node newNode = panel.cloneNode(true);
                String translation = t.translate(balloons.item(0).getTextContent());
                ((Element) newNode).getElementsByTagName("balloon").item(0).setTextContent(translation);
                printPanel(newNode);
            }
            //Node balloon = panel;
        }
    }
    public void printPanel(Node panel){
        System.out.println(panel.getTextContent());
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

    public List<String> getBalloons() {
        List<String> balloonContents = new ArrayList<>();
        NodeList balloonNodes = this.doc.getElementsByTagName("balloon");

        for (int i = 0; i < balloonNodes.getLength(); i++) {
            Node n = balloonNodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String content = n.getTextContent().trim();
                if (!content.isEmpty()) {
                    balloonContents.add(content);
                }
            }
        }

        return balloonContents;
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
            TranslationFile t = TranslationFile.getInstance();
            t.translateAllPhrases(parser.getBalloons());
        }
        catch (Exception e){
            System.out.println("Error: exception building DOM from XML");
            e.printStackTrace();
        }
    }
}
