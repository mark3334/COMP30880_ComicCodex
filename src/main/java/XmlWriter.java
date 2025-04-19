
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
import java.util.*;

public class XmlWriter {
    private Document outDoc;
    private Document inDoc;
    private String outFolder;
    private String fileName;
    private Element comic;
    private Element scenes;
    private XmlReader reader;
    private TranslationFile t = TranslationFile.getInstance();

    public XmlWriter(String outFolder, String fileName, File sourceXML) throws ParserConfigurationException, IOException, SAXException {
        //this.fileName = fileName;
        this.outFolder = outFolder;
        FileParser.ensureFolderExists(FileParser.getFile(outFolder));
        this.reader = new XmlReader(sourceXML);
        this.inDoc = reader.getDoc();
    }
    public void createEmptyComic() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.outDoc = builder.newDocument();
        Element comic = outDoc.createElement("comic");
        outDoc.appendChild(comic);
        this.comic = comic;
    }



    public void addFigures() {
        NodeList figuresList = this.inDoc.getElementsByTagName("figures");
        // there should be one <figures> tag in input xml / this.inDoc
        if (figuresList.getLength() > 0) {
            // must use import Node instead of clone when reading from one doc to another
            Node figuresNode = figuresList.item(0);
            Node importedFigures = this.outDoc.importNode(figuresNode, true); // not cloneNode
            comic.appendChild(importedFigures);
        }
        else{
            System.out.println("Error : no <figures> tag in input XML file");
        }
    }

    public void addScenes(List<Node> newScenes) {
        if(comic == null) System.out.println("Error no comic node in output doc ");
        if(this.scenes == null){
            this.scenes = outDoc.createElement("scenes");
            comic.appendChild(scenes);
        }
        for (Node scene : newScenes) {
            Node sceneCopy = this.outDoc.importNode(scene, true);
            this.scenes.appendChild(sceneCopy);
        }
    }
    public void createNewScenes(int k) throws ParserConfigurationException {
        List<Node> newScenes = new ArrayList<>();
        List<Node> randomScenes = reader.getRandomScenes(k);
        for (Node scene : randomScenes) {
            Node scenecopy = scene.cloneNode(true);
            String sceneDescription = reader.getNarrativeArc(scene);
            for(String line : sceneDescription.split("\n")) System.out.println(line);

            List<String> sceneDialogue = reader.getDialogue(sceneDescription);

            String fullDialogue = String.join("\n", sceneDialogue);

            System.out.println(fullDialogue);
            addDialogue(scenecopy, fullDialogue);

            newScenes.add(scenecopy);
        }
        if(outDoc == null) {
            createEmptyComic();
            addFigures();
        }
        addScenes(newScenes);
        //return newScenes;
    }
    public void addDialogue(Node scene, String sceneDialogue) {
        //Stores each character's lines in a FIFO queue
        Map<String, Queue<String>> dialogueMap = new HashMap<>();

        for (String line : sceneDialogue.split("\n")) {
            line = line.trim();
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                String speaker = parts[0].trim();
                String speech = parts[1].trim();
                if (!speech.isEmpty()) {
                    if (speech.endsWith(".")) {
                        speech = speech.substring(0, speech.length() - 1);
                    }
                    dialogueMap.putIfAbsent(speaker, new LinkedList<>());
                    dialogueMap.get(speaker).add(speech);
                }
            }
        }

        // Iterate through all panels in this scene
        NodeList panels = ((Element) scene).getElementsByTagName("panel");
        for (int i = 0; i < panels.getLength(); i++) {
            Element panel = (Element) panels.item(i);

            //Check each position in the panel
            for (String pos : Arrays.asList("left", "middle", "right")) {
                NodeList posNodeList = panel.getElementsByTagName(pos);
                if (posNodeList.getLength() > 0) {
                    Element posElement = (Element) posNodeList.item(0);

                    // Get the <figure> from this position
                    NodeList figures = posElement.getElementsByTagName("figure");
                    if (figures.getLength() > 0) {
                        Element fig = (Element) figures.item(0);
                        String name = reader.getText(fig, "name", null);

                        if (name != null && dialogueMap.containsKey(name)) {
                            Queue<String> lines = dialogueMap.get(name);
                            NodeList balloons = posElement.getElementsByTagName("balloon");

                            for (int b = 0; b < balloons.getLength(); b++) {
                                if (lines.isEmpty()) break;
                                Element balloon = (Element) balloons.item(b);
                                NodeList contents = balloon.getElementsByTagName("content");
                                if (contents.getLength() > 0) {
                                    Element content = (Element) contents.item(0);
                                    content.setTextContent(lines.poll());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void writeXMLTranslated(String fName, String fNameTranslated) throws TransformerException, IOException, ParserConfigurationException, SAXException {
        String path = this.outFolder + "/" + fName;
        File outputFile =  FileParser.getFile(path);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(this.outDoc), new StreamResult(outputFile));

        System.out.println("XML written to: " + outputFile.getAbsolutePath());
        this.reader = new XmlReader(outputFile);
        this.inDoc = reader.getDoc();
        this.outDoc = this.inDoc;
        addTranslatedPanels();
        writeXML(fNameTranslated);

    }
    public void writeXML(String fName) throws TransformerException, IOException {
        String path = this.outFolder + "/" + fName;
        File outputFile =  FileParser.getFile(path);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(this.outDoc), new StreamResult(outputFile));
        System.out.println("XML written to: " + outputFile.getAbsolutePath());
    }
    public void addTranslatedPanels() {
//        if(outDoc != null && outDoc != inDoc) {
//            System.out.println("Error: adding translated panels is for when indoc = outdoc");
//            System.out.println("This function should only be called when outdoc is null");
//        }
//      outDoc = inDoc;
        reader.ensureTranslatedBalloons();

        List<Node> panelsToDuplicate = reader.getPanelsToDuplicate();

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


    public void setBalloonSpeech() {
        NodeList balloons = outDoc.getElementsByTagName("balloon");
        for (int i = 0; i < balloons.getLength(); i++) {
            Element balloon = (Element) balloons.item(i);
            balloon.setAttribute("status", "speech");
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String fileNameVerbs = "Sprint4verbs.xml";
        String inFolder = ConfigurationFile.get("XML_INPUT_PATH");
        String inPath = inFolder + "/" + fileNameVerbs;
        File verbsFile = FileParser.getFile(inPath);
        String outputFolder = ConfigurationFile.get("XML_OUTPUT_PATH");
        XmlWriter writerVerbs = new XmlWriter(outputFolder, fileNameVerbs, verbsFile);
        writerVerbs.addTranslatedPanels();
        writerVerbs.writeXML(fileNameVerbs);

        String fileNameScenes = "Sprint5scenes.xml";
        String fileNameScenesT = "Sprint5scenesTranslated.xml";
        String pathScenes = inFolder + "/"  + fileNameScenes;
        File scenesFile = FileParser.getFile(pathScenes);
        XmlWriter writerScenes = new XmlWriter(outputFolder, fileNameScenes, scenesFile);
        writerScenes.createNewScenes(1);
        writerScenes.addTranslatedPanels();
        writerScenes.writeXMLTranslated(fileNameScenes, fileNameScenesT);


    }
}
