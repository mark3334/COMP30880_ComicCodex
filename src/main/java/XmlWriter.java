
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
import java.util.List;

public class XmlWriter {
    private Document outDoc;
    private Document inDoc;
    private String outFolder;
    private String fileName;
    private Element comic;
    private Element scenes;
    private XmlReader reader;
    private TranslationFile t = TranslationFile.getInstance();

    private final File root = Helper.getRootDirectory();
    public XmlWriter(String outFolder, String fileName, File sourceXML) throws ParserConfigurationException, IOException, SAXException {
        this.fileName = fileName;
        this.outFolder = outFolder;
        FileParser.ensureFolderExists(new File(root, outFolder));
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
            Node figuresNode = figuresList.item(0);
            Node figuresCopy = figuresNode.cloneNode(true);
            comic.appendChild(figuresCopy);
        }
    }

    public void addScenes(List<Node> newScenes) {
        if(this.scenes == null){
            this.scenes = outDoc.createElement("scenes");
            comic.appendChild(scenes);
        }
        for (Node scene : newScenes) {
            Node sceneCopy = scene.cloneNode(true);
            this.scenes.appendChild(sceneCopy);
        }
    }


    public void writeXML(String folderPath, String filename) throws TransformerException, IOException {
        File root = Helper.getRootDirectory();

        File folder = new File(root, folderPath);
        FileParser.ensureFolderExists(folder);

        String path = folder.getAbsolutePath() + fileName;
        File outputFile =  new File(root, path);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(this.outDoc), new StreamResult(outputFile));
        System.out.println("XML written to: " + outputFile.getAbsolutePath());
    }
    public void addTranslatedPanels() {
        if(outDoc != null) {
            System.out.println("Error: adding translated panels is for when indoc = outdoc");
            System.out.println("This function should only be called when outdoc is null");
        }
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
}
