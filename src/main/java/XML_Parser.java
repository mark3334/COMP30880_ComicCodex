import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
        if (t.allTranslated(getBalloons())) System.out.println("All verbs conjugations are translated");
        else t.translateAllPhrases(getBalloons());
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
    
    public void writeXML() throws TransformerException, IOException {
        File root = Helper.getRootDirectory();
        String fileName = "Verbs_" + Helper.getTargetLanguage();
        String path = "Resources/XMLoutput/";

        File folder = new File(root, path);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                System.out.println("Created directory: " + folder.getAbsolutePath());
            } else {
                throw new IOException("Failed to create directory: " + folder.getAbsolutePath());
            }
        }

        path += fileName;
        File outputFile =  new File(root, path);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(this.doc), new StreamResult(outputFile));
        System.out.println("XML written to: " + outputFile.getAbsolutePath());

    }
    public void addTranslatedPanels() {
        if (!t.allTranslated(getBalloons())) {
            System.out.println("All verbs conjugations are NOT translated");
            return;
        }
        NodeList panelNodes = this.doc.getElementsByTagName("panel");
        NodeList balloonNodes = this.doc.getElementsByTagName("balloon");
        System.out.println("Number of panels  - " + panelNodes.getLength());
        System.out.println("Number of balloons - " + balloonNodes.getLength());
        List<Node> panelsToDuplicate = new ArrayList<>();

        for (int i = 0; i < panelNodes.getLength(); i++) {
            Node panel = panelNodes.item(i);
            NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
            if (balloons.getLength() == 1) {
                panelsToDuplicate.add(panel);
            }
        }

        for (Node panel : panelsToDuplicate) {
            //Create a clone of the panel get the translation of its text and set it to the translation
            //then insert that into the Document before the original panel.
            Node clone =  panel.cloneNode(true);
            Node balloon = ((Element) clone).getElementsByTagName("balloon").item(0);
            String translation = t.translate(balloon.getTextContent().trim());
            balloon.setTextContent(translation);

            //Here the cloned panels are put right after the original
            //This can easily be reversed.
            Node parent = panel.getParentNode();
            Node nextSibling = panel.getNextSibling();
            if (nextSibling != null) {
                parent.insertBefore(clone, nextSibling);
            } else {
                parent.appendChild(clone);
            }

        }
    }

    public void printPanel(Node panel){
        System.out.println(panel.getTextContent());
    }

    public void printBalloons(){
        List<String> balloonContents = getBalloons();
        for(String s : balloonContents){
            System.out.println(s);
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
