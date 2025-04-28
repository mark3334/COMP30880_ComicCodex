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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

    public File getFile() { return file; }
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

    public void ensureTranslatedBalloonsWholeScenes() {
        List<String> balloonContents = new ArrayList<>();
        NodeList balloonNodes = this.doc.getElementsByTagName("balloon");

        for (int i = 2; i < balloonNodes.getLength(); i+=3) {
            Node n = balloonNodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String content = n.getTextContent().trim();
                if (!content.isEmpty()) {
                    balloonContents.add(content);
                }
            }
        }
        if (t.allTranslated(balloonContents)){
            System.out.println("All balloon text contents are already translated");
            return;
        }
        else{
            System.out.println("Now translating balloon contents : ...");
            t.translateAllPhrases(balloonContents);
            System.out.println("Balloon contents have been translated");
        }
        if (!t.allTranslated(balloonContents)) System.out.println("Error : Balloon text contents could not be translated");
    }
    public void ensureTranslatedBalloons(){
        List<String> balloonContents = getBalloons();
        if (t.allTranslated(balloonContents)){
            System.out.println("All balloon text contents are already translated");
            return;
        }
        else{
            System.out.println("Now translating balloon contents : ...");
            t.translateAllPhrases(balloonContents);
            System.out.println("Balloon contents have been translated");
        }
        if (!t.allTranslated(balloonContents)) System.out.println("Error : Balloon text contents could not be translated");
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
    public String getNarrativeArc(Node sceneNode) {
        NodeList panels = ((Element) sceneNode).getElementsByTagName("panel");

        StringBuilder sceneDescription = new StringBuilder();

        for (int i = 1; i < panels.getLength(); i++) {
            Element panel = (Element) panels.item(i);

            String setting = getText(panel, "setting", "an unknown place");
            String below = getText(panel, "below", "").trim();
            String caption;

            for (String pos : Arrays.asList("left", "middle", "right")) {
                NodeList posNode = panel.getElementsByTagName(pos);
                if (posNode.getLength() > 0) {
                    Element posElement = (Element) posNode.item(0);
                    NodeList figures = posElement.getElementsByTagName("figure");
                    NodeList balloons = posElement.getElementsByTagName("balloon");

                    String balloonContent = "doing something";
                    if (balloons.getLength() > 0) {
                        balloonContent = balloons.item(0).getTextContent().trim();
                    }

                    if (figures.getLength() > 0) {
                        Element fig = (Element) figures.item(0);
                        String name = getText(fig, "name", "someone");
                        if (figureNames.contains(name)) {
                            sceneDescription.append("(").append(setting).append(")").append(name).append(" is ").append(balloonContent).append(".\n");
                        }
                    }
                }
            }
            caption = "";
            if(!below.isEmpty()) caption = "Caption : " + below + "\n";
            sceneDescription.append(caption);
        }

        return sceneDescription.toString();
    }

    public String getText(Element parent, String tag, String defaultValue) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() > 0 && list.item(0).getTextContent() != null) {
            return list.item(0).getTextContent().trim();
        }
        return defaultValue;
    }

    private List<String> ensureValidDialogue(String prompt) {
        int maxRetries = 2;
        List<String> dialogues = new ArrayList<>();

        while (maxRetries-- > 0) {
            String response = client.getChatCompletion(prompt);
            dialogues.clear(); // Clear before reusing the list

            boolean allValid = true;

            for (String line : response.split("\n")) {
                line = line.trim();
                if (!line.isEmpty()) {
                    dialogues.add(line);

                    // Remove non-letter characters and split by space to get words
                    String[] words = line.replaceAll("[^a-zA-Z]", " ").trim().split("\\s+");

                    // If no valid words or all are one-character, mark as invalid
                    if (words.length == 0 || Arrays.stream(words).allMatch(s -> s.length() <= 1)) {
                        allValid = false;
                    }
                }
            }

            if (allValid) break;
            System.out.println("Invalid response detected, retrying...");
        }

        return dialogues;
    }


    public List<String> getDialogue(String sceneDescription) {
        StringBuilder sb = new StringBuilder();
        sb.append("For each line, generate a short dialogue that the character might say, based on the action described.");
        sb.append("Do not generate dialogue for the lines with Caption : just keep the caption in mind when generating the dialogue");
        sb.append("Each translation must contain at least one word, not just punctuation or ellipses (e.g., \"...\" is not acceptable).\n");
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

        return ensureValidDialogue(prompt);
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String fileNameVerbs = "Sprint4verbs.xml";
        String inFolder = ConfigurationFile.get("XML_INPUT_PATH");
        String path = inFolder + "/" + fileNameVerbs;
        File readFile = FileParser.getFile(path);
        XmlReader reader = new XmlReader(readFile);
        reader.printInfo();

        String fileNameScenes = "Sprint5scenes.xml";
        String pathScenes = inFolder + "/"  + fileNameScenes;
        File scenesFile = FileParser.getFile(pathScenes);
        XmlReader readerScenes = new XmlReader(scenesFile);
        readerScenes.printInfo();
    }

}
