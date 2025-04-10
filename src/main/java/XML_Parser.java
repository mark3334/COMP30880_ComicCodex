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
import java.io.StringWriter;
import java.util.*;

public class XML_Parser {
    private Document doc;
    private TranslationFile t;
    private File file;
    public XML_Parser(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();
        this.doc = document;
        this.t = TranslationFile.getInstance();
        this.file = file;
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

    public static String nodeToString(Node node) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Node> getRandomScenes(int k) {
        NodeList sceneNodes = this.doc.getElementsByTagName("scene");
        int numScenes = sceneNodes.getLength();
        if (k > numScenes) {
            throw new IllegalArgumentException("k must be <= the number of scenes");
        }

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < numScenes; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        List<Integer> randomSample = indices.subList(0, k);

        List<Node> result = new ArrayList<>();
        for (int i : randomSample) {
            Node n = sceneNodes.item(i);
            result.add(n);
        }

        return result;
    }
    public Node getRandomScene() {
        NodeList sceneNodes = this.doc.getElementsByTagName("scene");
        if(sceneNodes.getLength() == 0){
            System.out.println("Error : no scenes");
        }
        Random random = new Random();
        int randomIndex = random.nextInt(sceneNodes.getLength());
        return sceneNodes.item(randomIndex);
    }

    private void SceneToPrompt(Node sceneNode) {
        NodeList panels = ((Element) sceneNode).getElementsByTagName("panel");

        int panelIndex = 1;

        for (int i = 1; i < panels.getLength(); i++) {

            Element panel = (Element) panels.item(i);
            String setting = getText(panel, "setting", "an unknown place");
            String below = getText(panel, "below", "").trim();

            List<String> descriptions = new ArrayList<>();

            for (String pos : Arrays.asList("left", "middle", "right")) {
                NodeList posNode = panel.getElementsByTagName(pos);
                if (posNode.getLength() > 0) {
                    Element posElement = (Element) posNode.item(0);
                    NodeList figures = posElement.getElementsByTagName("figure");
                    if (figures.getLength() > 0) {
                        Element fig = (Element) figures.item(0);
                        String name = getText(fig, "name", "someone");
                        String pose = getText(fig, "pose", "doing something");
                        descriptions.add(name + " is " + pose);
                    }
                }
            }

            String main = String.join(", ", descriptions);
            String extra = below.isEmpty() ? "" : " " + below;

            System.out.println(i + ". (" + setting + ") " + main + "." + extra);
        }
    }

    private String getText(Element parent, String tag, String defaultValue) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() > 0 && list.item(0).getTextContent() != null) {
            return list.item(0).getTextContent().trim();
        }
        return defaultValue;
    }

    public void writeXML(String path, String fileName) throws TransformerException, IOException {
        File root = Helper.getRootDirectory();
        //String fileName = "Verbs_" + Helper.getTargetLanguage();

        File folder = new File(root, path);
        FileParser.ensureFolderExists(folder);

        path += fileName;
        File outputFile =  new File(root, path);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(this.doc), new StreamResult(outputFile));
        System.out.println("XML written to: " + outputFile.getAbsolutePath());

    }


    /*
        Gets the balloon string texts and translate them using the Translation singleton.
     */
    public void ensureTranslatedPanel(){
        List<String> balloonContents = getBalloons();
        if (t.allTranslated(balloonContents)){
            System.out.println("All balloon text contents are translated");
            return;
        }
        else t.translateAllPhrases(balloonContents);
        if (!t.allTranslated(balloonContents)) System.out.println("Error : Balloon text contents could not be translated");
    }

    /*
        Returns a list of panels that have at least one baloon text.
     */
    public List<Node> getPanelsToDuplicate() {
        NodeList panelNodes = this.doc.getElementsByTagName("panel");
        List<Node> panelsToDuplicate = new ArrayList<>();
        for (int i = 0; i < panelNodes.getLength(); i++) {
            Node panel = panelNodes.item(i);
            NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
            if (balloons.getLength() >= 1) {
                panelsToDuplicate.add(panel);
            }
        }
        return panelsToDuplicate;
    }

    public void printInfo() {
        System.out.println("\nXML Filepath : " + this.file.getAbsolutePath());
        NodeList panelNodes = this.doc.getElementsByTagName("panel");
        NodeList balloonNodes = this.doc.getElementsByTagName("balloon");
        NodeList sceneNodes = this.doc.getElementsByTagName("scene");
        System.out.println("Number of panels  - " + panelNodes.getLength());
        System.out.println("Number of balloons - " + balloonNodes.getLength());
        System.out.println("Number of scenes - " + sceneNodes.getLength());
    }

    public void addTranslatedPanels() {
        ensureTranslatedPanel();

        List<Node> panelsToDuplicate = getPanelsToDuplicate();

        for (Node panel : panelsToDuplicate) {
            //Create a clone of the panel get the translation of its text and set it to the translation
            //then insert that into the Document before the original panel.
            Node clone =  panel.cloneNode(true);
            NodeList balloons = ((Element) clone).getElementsByTagName("balloon");
            for (int i = 0; i < balloons.getLength(); i++) {
                Node balloon = balloons.item(i);
                String translation = t.translate(balloon.getTextContent().trim());
                balloon.setTextContent(translation);
            }


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

    /*
        Takes in Xml file, and returns true of it fits a certain criteria, false otherwise.
        Criteria;
        - Has <comic> root tag.
        - Has <figures> tag and at least one <figure> tag nested inside.
        - Has <scenes> tag and at least one <scene> tag nested inside.
     */

    /**
     * Validates the structure of a comic XML file.
     * The method checks for the following structural requirements:
     * - Has <comic> root tag.
     * - Has <figures> tag and at least one <figure> tag nested inside.
     * - Has <scenes> tag and at least one <scene> tag nested inside.
     *
     * @param xmlFile The XML file to validate.
     * @return true; if the XML file meets all structural criteria; false otherwise or if a parsing error occurs.
     */
    public static boolean validateComicXml(File xmlFile) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            //STARTING CHECKS
            Element root = document.getDocumentElement();
            if (!root.getNodeName().equals("comic")) { //Check root element is <comic>.
                return false;
            }

            NodeList figuresList = document.getElementsByTagName("figures");
            if (figuresList.getLength() == 0) { // Check <figures> exists.
                return false;
            }

            Element figuresElement = (Element) figuresList.item(0);
            NodeList figureNodes = figuresElement.getElementsByTagName("figure");
            if (figureNodes.getLength() == 0) { //Checks <figures> has at least one <figure>.
                return false;
            }

            NodeList scenesList = document.getElementsByTagName("scenes");
            if (scenesList.getLength() == 0) { //Check <scenes> exists.
                return false;
            }

            Element scenesElement = (Element) scenesList.item(0);
            NodeList sceneNodes = scenesElement.getElementsByTagName("scene");
            if (sceneNodes.getLength() == 0) { //Checks <scenes> has at least one <scene>.
                return false;
            }

            //More Checks for format can be added here.

            return true;

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static void main(String[] args) throws ParserConfigurationException {
        File root = Helper.getRootDirectory();
        String path = "Resources/XMLinput/Sprint4Verbs.xml";
        File f = new File(root, path);
        try {
            XML_Parser parser = new XML_Parser(f);
            parser.addTranslatedPanels();
            //String fileName = "Verbs_" + Helper.getTargetLanguage();
            parser.writeXML("Resources/XMLoutput/", "Verbs_" + Helper.getTargetLanguage());

        }
        catch (Exception e){
            System.out.println("Error: exception building DOM from XML");
            e.printStackTrace();
        }
        String path2 = "Resources/XMLinput/Sprint5scenes.xml";
        File file2 = new File(root, path2);
        try {
            XML_Parser parser = new XML_Parser(file2);
            parser.printInfo();
            parser.getRandomScenes(3);
            List<Node> randomScenes = parser.getRandomScenes(3);
            for (Node scene : randomScenes) {
                parser.SceneToPrompt(scene); 
            }

        } catch (Exception e){
            System.out.println("Error: exception building DOM from XML");
            e.printStackTrace();
        }
    }
}
