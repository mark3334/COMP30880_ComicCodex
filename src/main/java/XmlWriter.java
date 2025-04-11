
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

import java.io.File;
import java.io.IOException;
import java.util.List;

public class XmlWriter {
    private Document outDoc;
    private Document inDoc;
    private String folderPath;
    private String fileName;
    private Element comic;
    private Element scenes;
    public XmlWriter(String folderPath, String fileName, Document inDoc) {
        this.fileName = fileName;
        this.folderPath = folderPath;
        this.inDoc = inDoc;
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
        // Create and append the <comic> element to the new document
        Element comic = outDoc.createElement("comic");
        outDoc.appendChild(comic);
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
            Element scenesElement = outDoc.createElement("scenes");
            this.scenes = scenesElement;
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
}
