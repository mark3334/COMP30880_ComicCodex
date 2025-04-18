import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            File root = Helper.getRootDirectory();
            File inputFile = new File(root, "Resources/XMLoutput/Sprint5_InterwovenOutput.xml");
            XML_Parser parser1 = new XML_Parser(inputFile);

            // Create a list to store all new scenes
            List<Node> newScenes = new ArrayList<>();

            //Use splitPanel to process the file.
            NodeList sceneNodes = parser1.getDoc().getElementsByTagName("scene");
            for (int i = 0; i < sceneNodes.getLength(); i++) {
                Element oldScene = (Element) sceneNodes.item(i);

                Document doc = parser1.getDoc();
                Element newScene = doc.createElement("scene");

                NodeList panelNodes = oldScene.getElementsByTagName("panel");
                for (int j = 0; j < panelNodes.getLength(); j++) {
                    Element panel = (Element) panelNodes.item(j);

                    List<Element> split = parser1.splitPanel(panel);

                    for (Element p : split) {
                        Node imported = doc.importNode(p, true);
                        newScene.appendChild(imported);
                    }
                }

                newScenes.add(newScene);
            }

            // Write the split panels into the file.
            Document newDoc = parser1.scenesToDoc(newScenes);
            String splitPanelsFilePath = "Resources/XMLoutput/Sprint5_SplitPanels.xml";
            parser1.docToXml(newDoc, splitPanelsFilePath);

            //Read the splitpanels file and use the getOrAdd method to create the indexes and audio files.
            File inputXml = new File(root, splitPanelsFilePath);
            XML_Parser parser2 = new XML_Parser(inputXml);

            AudioManager audioManager = AudioManager.getInstance();
            List<String> balloonTexts = parser2.getBalloons();

            for (String text : balloonTexts) {
                if (text.isEmpty()) continue;

                audioManager.getOrAdd(text);
            }

            System.out.println("All audio and indexing processing is complete.");

            //Use the addAudioToPanel method to write the <audio> tags into the file and generate the final output file.
            Document docWithAudio = parser2.getDoc();
            XML_Parser.addAudioToPanel(docWithAudio);

            parser2.docToXml(docWithAudio, "Resources/XMLoutput/Sprint6_FinalAudioTagged.xml");

            System.out.println("The final version has been generated.");

        } catch (Exception e) {
            System.out.println(" Error during split panel processing:");
            e.printStackTrace();
        }
    }
}