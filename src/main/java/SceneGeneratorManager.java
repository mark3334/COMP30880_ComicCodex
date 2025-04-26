import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
public class SceneGeneratorManager {
    /**
     * checks whether the input string is empty or contains only meaningless characters
     */
    private static boolean isNonEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * generates a boolean array indicating whether each element in a VignetteSchema is present
     */
    public static boolean[] getFeatureVector(VignetteSchema schema) {
        return new boolean[] {
                isNonEmpty(schema.getLeftPose()),
                isNonEmpty(VignetteSchema.getRandomElement(schema.getLeftText())),
                isNonEmpty(VignetteSchema.getRandomElement(schema.getRightPose())),
                isNonEmpty(VignetteSchema.getRandomElement(schema.getCombinedText())),
                isNonEmpty(VignetteSchema.getRandomElement(schema.getBackgrounds()))
        };
    }

    /**
     * checks whether two input boolean arrays match
     */
    public static boolean matchesPattern(boolean[] actual, boolean[] pattern) {
        if (actual.length != pattern.length) return false;
        for (int i = 0; i < actual.length; i++) {
            if (actual[i] != pattern[i]) return false;
        }
        return true;
    }

    /**
     * Defined all the variables needed for generating scenes,
     * set up the basic structure of the DOM parser, and used a switch-case to control different generation templates.
     */
    public static Document generateDom(VignetteSchema schema, String model) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element scene = doc.createElement("scene");
        doc.appendChild(scene);

        Element backgroundEl = doc.createElement("background");
        backgroundEl.setTextContent(VignetteSchema.getRandomElement(schema.getBackgrounds()));
        scene.appendChild(backgroundEl);

        String leftPose = schema.getLeftPose();
        String leftText = VignetteSchema.getRandomElement(schema.getLeftText());
        String rightPose = VignetteSchema.getRandomElement(schema.getRightPose());
        String combinedText = VignetteSchema.getRandomElement(schema.getCombinedText());
        String combinedTextTranslated = TranslationFile.getInstance().translate(combinedText);

        Figure leftFigure = new Figure("A", "male", "light", "black", "none", "short", "straight", "default", "running", "right");
        Figure rightFigure = new Figure("B", "female", "brown", "brown", "none", "long", "curly", "red", "jumping", "left");
        String leftName = leftFigure.getName();
        String rightName = rightFigure.getName();

        switch (model.toLowerCase()) {
            case "full":
                // Panel 1
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", leftText, null, false));

                // Panel 2
                scene.appendChild(createPanelWithBelow(doc, leftName, leftPose, "right", rightName, rightPose, "left", combinedText));

                // Panel 3
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", combinedText, null, false));

                // Panel 4
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", combinedTextTranslated, rightName, true));
            case "no_left_text":
                // Panel 1
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", combinedText, rightName, true));

                // Panel 2
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", combinedText, null, false));

                // Panel 3
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", combinedTextTranslated, rightName, true));
                break;
            case "only_combined_text":
                // Panel 1
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", combinedText, rightName, true));

                // Panel 2
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", combinedTextTranslated, rightName, true));
                break;
            case "left_text_only":
                // Panel 1
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", leftText, null, false));

                // Panel 2
                scene.appendChild(createPanelWithBalloon(doc, leftName, leftPose, "right", TranslationFile.getInstance().translate(leftText), null, false));
                break;
            default:
                throw new IllegalArgumentException("Unknown model type: " + model);
        }
        return doc;
    }

    private static Element createPanelWithBalloon(Document doc, String leftName, String leftPose, String leftFacing,
                                                  String balloonText, String rightName, boolean withRight) {
        Element panel = doc.createElement("panel");

        panel.appendChild(createFigureElement(doc, "left", leftName, leftPose, leftFacing));
        if (withRight && rightName != null) {
            panel.appendChild(createFigureElement(doc, "right", rightName, "jumping", "left")); // RightPose hardcoded for now
        }

        Element balloon = doc.createElement("balloon");
        balloon.setAttribute("status", "speech");
        Element content = doc.createElement("content");
        content.setTextContent(balloonText);
        balloon.appendChild(content);
        panel.appendChild(balloon);

        return panel;
    }

    private static Element createPanelWithBelow(Document doc, String leftName, String leftPose, String leftFacing,
                                                String rightName, String rightPose, String rightFacing,
                                                String belowText) {
        Element panel = doc.createElement("panel");

        panel.appendChild(createFigureElement(doc, "left", leftName, leftPose, leftFacing));
        panel.appendChild(createFigureElement(doc, "right", rightName, rightPose, rightFacing));

        Element below = doc.createElement("below");
        below.setTextContent(belowText);
        panel.appendChild(below);

        return panel;
    }

    private static Element createFigureElement(Document doc, String side, String name, String pose, String facing) {
        Element sideEl = doc.createElement(side);
        Element figure = doc.createElement("figure");

        Element id = doc.createElement("id");
        id.setTextContent(name);
        figure.appendChild(id);

        Element poseEl = doc.createElement("pose");
        poseEl.setTextContent(pose);
        figure.appendChild(poseEl);

        Element facingEl = doc.createElement("facing");
        facingEl.setTextContent(facing);
        figure.appendChild(facingEl);

        sideEl.appendChild(figure);
        return sideEl;
    }


    private static final List<SceneGeneratorInterface> generators = List.of(
            new FullSceneGenerator(),
            new NoLeftTextSceneGenerator(),
            new OnlyCombinedTextSceneGenerator(),
            new LeftTextOnlySceneGenerator()
    );

    public static Document generateScene(VignetteSchema schema) throws ParserConfigurationException {
        for (SceneGeneratorInterface gen : generators) {
            if (gen.matches(schema)) {
                return gen.generate(schema);
            }
        }
        return null;
    }
}
