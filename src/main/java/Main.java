import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args)  {
        File root = Helper.getRootDirectory();
        String outputFolder = "Resources/XMLoutput/";

        String path = "Resources/XMLinput/Sprint5scenes.xml";
        File file2 = new File(root, path);
        try {
            XML_Parser parser = new XML_Parser(file2);
            parser.printInfo();
            List<String> figureNames = parser.getFigureNames();
            System.out.println("Figure Names: " + figureNames);
            List<Node> newScenes = new ArrayList<>();
            List<Node> randomScenes = parser.getRandomScenes(1);
            for (Node scene : randomScenes) {
                Node scenecopy = scene.cloneNode(true);
                String sceneDescription = parser.getNarrativeArc(scene);
                List<String> sceneDialogue = parser.getDialogue(sceneDescription);

                String fullDialogue = String.join("\n", sceneDialogue);

                System.out.println(fullDialogue);
                parser.addDialogue(scenecopy, fullDialogue);

                newScenes.add(scenecopy);
            }

            Document newDoc1 = parser.scenesToDoc(newScenes);

            String outpath = "Resources/XMLoutput/Sprint5_DialogueOutput.xml";
            parser.docToXml(newDoc1, outpath);

            String fname = "Sprint5_InterwovenOutput.xml";
            XML_Parser parser2 = new XML_Parser(new File(root, outpath));
            parser2.addTranslatedPanels();
            parser2.writeXML(outputFolder, fname);


        } catch (Exception e){
            System.out.println("Error: exception building DOM from XML");
            e.printStackTrace();
        }
    }

}