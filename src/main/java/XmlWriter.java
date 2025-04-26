
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
    private String outFolder = ConfigurationFile.get("XML_OUTPUT_PATH");
    private String fileName;
    private Element comic;
    private Element scenes;
    private XmlReader reader;
    private TranslationFile t = TranslationFile.getInstance();
    private AudioManager audioManager = AudioManager.getInstance();
    public XmlWriter(File sourceXML) throws ParserConfigurationException, IOException, SAXException {
        FileParser.ensureFolderExists(FileParser.getFile(outFolder));
        this.reader = new XmlReader(sourceXML);
        this.inDoc = reader.getDoc();
    }

    public void setOutFolder(String folderPath) {
        outFolder = folderPath;
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
            // String sceneCopyString = XmlReader.nodeToString(scenecopy); // working as expected
            //System.out.println(sceneCopyString);
            newScenes.add(scenecopy);
        }
        if(outDoc == null) {
            System.out.println("Creating empty comic with figures from source XML file (createNewScenes method)");
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
        File outputFile = FileParser.getFile(path);

        // The way the code is now only add audio to either the non-translated version or the translated version
        // as the translated version reads from the file of the translated version so adding audio can
        // cause issues
        addAudio(); // adds audio for scenes (not translated scenes)

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(this.outDoc), new StreamResult(outputFile));

        System.out.println("XML written to: " + outputFile.getAbsolutePath());
        this.reader = new XmlReader(outputFile);
        this.inDoc = reader.getDoc();
        this.outDoc = this.inDoc;
        addTranslatedPanels();

        // addAudio(); - doesn't work (11 duplicates?)
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
        outDoc = inDoc;
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

    public void addAudio() { addAudioToDoc(outDoc); }

    public static void splitPanels(Document doc) {
        NodeList panelNodes = doc.getElementsByTagName("panel");
        List<Element> panelsToAdd = new ArrayList<>();
        List<Element> panelsToRemove = new ArrayList<>();
        for (int i = 0; i < panelNodes.getLength(); i++) {
            List<Element> result = new ArrayList<>();
            Element panel = (Element) panelNodes.item(i);
            NodeList balloons = panel.getElementsByTagName("balloon");
            if (balloons.getLength() <= 1) continue;

            if (balloons.getLength() == 2) { //Modify the condition logic, if there are exactly two balloons, split the panel.
                Element panel1 = (Element) panel.cloneNode(true);
                Element panel2 = (Element) panel.cloneNode(true);

                // Remove second balloon from panel1
                Node toRemove1 = panel1.getElementsByTagName("balloon").item(1);
                toRemove1.getParentNode().removeChild(toRemove1);

                // Remove first balloon from panel2
                Node toRemove2 = panel2.getElementsByTagName("balloon").item(0);
                toRemove2.getParentNode().removeChild(toRemove2);

                panelsToAdd.add(panel1);
                panelsToAdd.add(panel2);
                panelsToRemove.add(panel);
            }
            else if (balloons.getLength() > 2) {
                System.out.println("Error, Unexpected input : number of balloons in panel " + balloons.getLength());
            }
        }
        for (Element originalPanel : panelsToRemove) {
            Node parent = originalPanel.getParentNode();
            Node nextSibling = originalPanel.getNextSibling();

            parent.removeChild(originalPanel);

            for (Element newPanel : panelsToAdd) {
                Node importedPanel = doc.importNode(newPanel, true);
                if (nextSibling != null) {
                    parent.insertBefore(importedPanel, nextSibling);
                } else {
                    parent.appendChild(importedPanel);
                }
            }
        }
    }
    public static void addAudioToDoc(Document doc) {
        //XmlWriter.splitPanels(doc);
        AudioManager audioManager = AudioManager.getInstance();
        NodeList panels = doc.getElementsByTagName("panel");

        for (int i = 0; i < panels.getLength(); i++) {
            Element panel = (Element) panels.item(i);

            NodeList balloonList = panel.getElementsByTagName("balloon");
            for (int j = 0; j < balloonList.getLength(); j++) {
                Element balloon = (Element) balloonList.item(j);
                String text = "";

                // If the format is <balloon><content>...</content></balloon>
                NodeList contents = balloon.getElementsByTagName("content");
                if (contents.getLength() > 0) {
                    text = contents.item(0).getTextContent().trim();
                } else {
                    // If the format is <balloon>...</balloon>
                    text = balloon.getTextContent().trim();
                }

                int index = audioManager.getOrAdd(text);
                Element audioElement = doc.createElement("audio");
                audioElement.setTextContent(index + ".mp3");
                panel.appendChild(audioElement);
                break; // only one <audio> for each panel
            }
        }
    }

    public void createComicFullLesson() throws TransformerException, ParserConfigurationException, IOException, SAXException {
        List<String> schedule = ConfigurationFile.getLessonSchedule();
        createEmptyComic();

        for(String type : schedule){
            if(type.equalsIgnoreCase("conjugation")){
                Node scene = XML_Parser.generateScene(this.outDoc, "Resources/XMLinput/Sprint4Verbs.xml",false);
                this.comic.appendChild(scene);

            } ;// add verb scene co
            if(type.equalsIgnoreCase("left")) continue; // add left vignette
            if(type.equalsIgnoreCase("whole")) continue; //
            if(type.equalsIgnoreCase("story")) {
                Node scene = XML_Parser.generateScene(this.outDoc, "Resources/XMLoutput/Sprint6_FinalAudioFile.xml",true);
                this.comic.appendChild(scene);
            }
        }
        String outputFile = "FinalSprint.xml";
        this.writeXML(outputFile);
    }

    public static void createConjugationLesson() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String fileNameVerbs = "Sprint4verbs.xml";
        String inFolder = ConfigurationFile.get("XML_INPUT_PATH");
        String inPath = inFolder + "/" + fileNameVerbs;
        File verbsFile = FileParser.getFile(inPath);
        XmlWriter writerVerbs = new XmlWriter(verbsFile);
        writerVerbs.addTranslatedPanels();
        writerVerbs.addAudio();
        writerVerbs.writeXML(fileNameVerbs);
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String fileNameVerbs = "Sprint4verbs.xml";
        String inFolder = ConfigurationFile.get("XML_INPUT_PATH");
        String inPath = inFolder + "/" + fileNameVerbs;
        File verbsFile = FileParser.getFile(inPath);
        XmlWriter writerVerbs = new XmlWriter(verbsFile);
        writerVerbs.addTranslatedPanels();
        writerVerbs.addAudio();
        writerVerbs.writeXML(fileNameVerbs);

        String fileNameScenes = "Sprint5scenes.xml";
        String fileNameScenesT = "Sprint5scenesTranslated.xml";
        String pathScenes = inFolder + "/"  + fileNameScenes;
        File scenesFile = FileParser.getFile(pathScenes);
        XmlWriter writerScenes = new XmlWriter(scenesFile);
        writerScenes.createNewScenes(1);
        writerScenes.setBalloonSpeech();
        writerScenes.writeXMLTranslated(fileNameScenes, fileNameScenesT);
    }
}
