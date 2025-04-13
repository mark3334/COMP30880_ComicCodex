import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmlReader {
    private Document doc;
    private TranslationFile t;
    private File file;
    private List<String> figureNames;
    private OpenAIClient client;

    public XmlReader(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();
        this.doc = document;
        this.t = TranslationFile.getInstance();
        this.file = file;
        this.figureNames = getFigureNames();
        this.client = OpenAIClient.getInstance();
    }

    public Document getDoc() { return doc; }
    public void printInfo() {
        System.out.println("\nXML Filepath : " + this.file.getAbsolutePath());
        NodeList panelNodes = this.doc.getElementsByTagName("panel");
        NodeList balloonNodes = this.doc.getElementsByTagName("balloon");
        NodeList sceneNodes = this.doc.getElementsByTagName("scene");
        System.out.println("Number of panels  - " + panelNodes.getLength());
        System.out.println("Number of balloons - " + balloonNodes.getLength());
        System.out.println("Number of scenes - " + sceneNodes.getLength());
    }

    public List<String> getFigureNames() {
        List<String> names = new ArrayList<>();

        NodeList figuresList = this.doc.getElementsByTagName("figures");
        if (figuresList.getLength() == 0) return names;

        Node figuresNode = figuresList.item(0);
        NodeList figureNodes = figuresNode.getChildNodes();

        for (int i = 0; i < figureNodes.getLength(); i++) {
            Node node = figureNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("figure")) {
                Element figureElement = (Element) node;
                NodeList nameNodes = figureElement.getElementsByTagName("name");
                if (nameNodes.getLength() > 0) {
                    String name = nameNodes.item(0).getTextContent().trim();
                    if (!name.isEmpty()) {
                        names.add(name);
                    }
                }
            }
        }

        return names;
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

    public void ensureTranslatedBalloons(){
        List<String> balloonContents = getBalloons();
        if (t.allTranslated(balloonContents)){
            System.out.println("All balloon text contents are translated");
            return;
        }
        else t.translateAllPhrases(balloonContents);
        if (!t.allTranslated(balloonContents)) System.out.println("Error : Balloon text contents could not be translated");
    }

    public List<String> getDialogue(String sceneDescription) {
        StringBuilder sb = new StringBuilder();
        sb.append("For each line, generate a short dialogue that the character might say, based on the action described.");
        sb.append("The character names are : ").append(figureNames.toString());
        sb.append("Only generate dialogue for lines that start with a character name. The response should be in this format:\n");
        sb.append("Alfie: <dialogue>\nBetty: <dialogue>\n");
        sb.append("Example:\n");
        sb.append("Alfie is eating.\nBetty is watching.\n\n");
        sb.append("Should produce:\nAlfie: Yum! This is good.\nBetty: I wonder if there’s any left for me.");

        sb.append("\n\nNow here is the list:\n");

        String[] lines = sceneDescription.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                sb.append(trimmed).append("\n");
            }
        }

        String prompt = sb.toString();
        String response = client.getChatCompletion(prompt);

        List<String> dialogues = new ArrayList<>();
        for (String line : response.split("\n")) {
            line = line.trim();
            if (!line.isEmpty()) {
                dialogues.add(line);
            }
        }

        return dialogues;
    }

}
