
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

    /**
     * Sets the output folder path where the generated XML files will be saved.
     *
     * @param folderPath the path to the desired output folder
     */
    public void setOutFolder(String folderPath) {
        outFolder = folderPath;
    }

    /**
     * Initializes an empty comic structure in the output XML document by creating the root <comic> element.
     */
    public void createEmptyComic() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.outDoc = builder.newDocument();
        Element comic = outDoc.createElement("comic");
        outDoc.appendChild(comic);
        this.comic = comic;
    }

    /**
     * Adds the <figures> element from the input XML document to the output document.
     * If the <figures> element is not found in the input, an error message is printed.
     */
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

    /**
     * Adds a list of scene nodes to the output XML document under the <scenes> element.
     * If the <scenes> element does not exist, it is created and appended to the <comic> element.
     *
     * @param newScenes a list of scene nodes to be added to the output document
     */
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

    /**
     * Creates new scenes by selecting random scenes from the input document, adding dialogue to them,
     * and appending them to the output document. If the output document is not initialized,
     * it creates an empty comic and adds figures before adding the scenes.
     *
     * @param k the number of random scenes to create and add
     */
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

    /**
     * Adds dialogue to the specified scene node by mapping speaker names to their respective lines.
     * The dialogue is distributed across the <balloon> elements within the scene's panels.
     *
     * @param scene         the scene node to which dialogue will be added
     * @param sceneDialogue the dialogue text containing speaker names and their lines
     */
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

    /**
     * Writes the output XML document to a file with the specified filename, adds audio elements,
     * translates the panels, and writes the translated document to another specified file.
     */
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

    /**
     * Writes the current output XML document to a file with the specified filename.
     *
     * @param fName the filename for the output XML
     */
    public void writeXML(String fName) throws TransformerException, IOException {
        String path = this.outFolder + "/" + fName;
        File outputFile =  FileParser.getFile(path);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(this.outDoc), new StreamResult(outputFile));
        System.out.println("XML written to: " + outputFile.getAbsolutePath());
    }

    /**
     * Adds translated versions of panels to the output XML document by duplicating existing panels
     * and replacing their text content with translated text.
     */
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

    /**
     * Sets the "status" attribute of all <balloon> elements in the output XML document to "speech".
     */
    public void setBalloonSpeech() {
        NodeList balloons = outDoc.getElementsByTagName("balloon");
        for (int i = 0; i < balloons.getLength(); i++) {
            Element balloon = (Element) balloons.item(i);
            balloon.setAttribute("status", "speech");
        }
    }

    /**
     * Adds audio elements to the output XML document by associating audio files with the text content
     * of <balloon> elements within each panel.
     */
    public void addAudio() {
        if(outDoc == null) {
            outDoc = inDoc;
        }
        addAudioToDoc(outDoc);
    }

    /**
     * Splits panels in the provided XML document that contain multiple <balloon> elements into separate panels,
     * each containing a single <balloon>.
     *
     * @param doc the XML document whose panels are to be split
     */
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

    /**
     * Adds audio elements to the specified XML document by associating audio files with the text content
     * of <balloon> elements within each panel.
     *
     * @param doc the XML document to which audio elements will be added
     */
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

    /**
     * Creates a full comic lesson by assembling scenes based on a predefined schedule,
     * adding audio elements, and writing the final output to an XML file.
     */
    public void createComicFullLesson() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        List<String> schedule = ConfigurationFile.getLessonSchedule();
        createEmptyComic();
        addFigures();

        Map<String, Integer> typeCounts = new HashMap<>();
        for(String type : schedule) {
            String key = type.toLowerCase();
            typeCounts.put(key, typeCounts.getOrDefault(key, 0) + 1);
        }

        Map<String, Stack<Node>> sceneStacks = new HashMap<>();
        sceneStacks.put("conjugation", new Stack<>());
        sceneStacks.put("story", new Stack<>());
        sceneStacks.put("left", new Stack<>());
        sceneStacks.put("whole", new Stack<>());

        String verbFileName = "Sprint4verbs.xml";
        File verbsFile = FileParser.getFile(outFolder + "/" + verbFileName);
        XmlReader verbReader = new XmlReader(verbsFile);
        String storyFileName = "Sprint6_FinalAudioFile.xml";
        File storyFile = FileParser.getFile(outFolder + "/" + storyFileName);
        XmlReader storyReader = new XmlReader(storyFile);
        String leftFileName = "left_scenes.xml";
        leftFileName = "left_scenes_translated_" + ConfigurationFile.getTargetLanguage() + ".xml"; // NEW
        File leftFile = FileParser.getFile(outFolder + "/" + leftFileName);
        XmlReader leftReader = new XmlReader(leftFile);
        String wholeFileName = "whole_scenes.xml";
        wholeFileName = "whole_scenes_translated_" + ConfigurationFile.getTargetLanguage() + ".xml";
        File wholeFile = FileParser.getFile(outFolder + "/" + wholeFileName);
        XmlReader wholeReader = new XmlReader(wholeFile);

        for (Node scene : verbReader.getRandomScenes(typeCounts.get("conjugation"))) {
            sceneStacks.get("conjugation").push(scene);
        }
        for (Node scene : storyReader.getRandomScenes(typeCounts.get("story"))){
            sceneStacks.get("story").push(scene);
        }
        for (Node scene : leftReader.getRandomScenes(typeCounts.get("left"))) {
            sceneStacks.get("left").push(scene);
        }
        for (Node scene : wholeReader.getRandomScenes(typeCounts.get("whole"))) {
            sceneStacks.get("whole").push(scene);
        }

        for (String type : schedule) {
            String key = type.toLowerCase();
            if (sceneStacks.containsKey(key) && !sceneStacks.get(key).isEmpty()) {
                Node raw = sceneStacks.get(key).pop();
                Node imported = this.outDoc.importNode(raw, true);
                this.comic.appendChild(imported);
            }
        }

        String outputFile = "FinalSprint.xml";
        this.writeXML(outputFile);
    }

    /**
     * Creates a conjugation lesson by reading verb scenes from an input XML file,
     * adding translated panels and audio elements, and writing the final output to an XML file.
     */
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



    public static void removeTranslatedPanel(Document doc) {
        NodeList panelNodes = doc.getElementsByTagName("panel");
        for (int i = 1; i < panelNodes.getLength(); i++) {
            Node prevPanel = panelNodes.item(i - 1);
            Node currPanel = panelNodes.item(i);

            Node prevClone = prevPanel.cloneNode(true);
            Node currClone = currPanel.cloneNode(true);

            resetBalloon(prevClone);
            resetBalloon(currClone);

            if (prevClone.isEqualNode(currClone)) {
                currPanel.getParentNode().removeChild(currPanel);
                i--;
            }
        }
    }

    private static void resetBalloon(Node panel) {
        NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
        for (int i = 0; i < balloons.getLength(); i++) {
            balloons.item(i).setTextContent("");
        }
    }

    public static void writeTranslatedVignettes() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String leftScenesFilename = "left_scenes_untranslated.xml";
        String inFolder = ConfigurationFile.get("XML_OUTPUT_PATH");
        String inPathLeft = inFolder + "/" + leftScenesFilename;
        File leftScenesFile = FileParser.getFile(inPathLeft);
        if (!leftScenesFile.exists()) {
            System.err.println("Left scenes file not found: " + inPathLeft);
            return;
        }
        String leftOutputName = "left_scenes_translated_" + ConfigurationFile.getTargetLanguage() + ".xml";
        String leftScenesTranslatedPath = inFolder + "/"  + leftOutputName;
        File leftScenesTranslatedFile = FileParser.getFile(leftScenesTranslatedPath);
        if(leftScenesTranslatedFile.exists()) {
            System.out.println("Translated scenes file already exists for language - " + ConfigurationFile.getTargetLanguage());
        }
        else{
            XmlWriter leftWriter = new XmlWriter(leftScenesFile);
            leftWriter.addTranslatedPanels();
            leftWriter.addAudio();
            leftWriter.writeXML(leftOutputName);
        }


        TranslationFile t = TranslationFile.getInstance();
        // handle whole scenes separately as its translation structure is different.

        System.out.println("Now translating whole scenes: ");
        String outFolder = ConfigurationFile.get("XML_OUTPUT_PATH");
        String pathwholeFileOriginal = outFolder + "/" + "whole_scenes.xml";
        File wholeFileOriginal = FileParser.getFile(pathwholeFileOriginal);
        XmlReader wholeReader = new XmlReader(wholeFileOriginal);
        wholeReader.ensureTranslatedBalloonsWholeScenes();
        NodeList balloonNodes = wholeReader.getDoc().getElementsByTagName("balloon");

        for (int i = 2; i < balloonNodes.getLength(); i += 3) {
            Node balloonNode = balloonNodes.item(i);
            String toTranslate = balloonNodes.item(i - 1).getTextContent().trim();
            balloonNode.setTextContent(t.translate(toTranslate).trim());
        }
        String wholeOutputName = "whole_scenes_translated_" + ConfigurationFile.getTargetLanguage() + ".xml";
        String path = inFolder + "/" + wholeOutputName;
        XmlWriter.addAudioToDoc(wholeReader.getDoc());
        XML_Parser.docToXml(wholeReader.getDoc(), path);

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
