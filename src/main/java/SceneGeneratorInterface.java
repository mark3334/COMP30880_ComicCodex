import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;

public interface SceneGeneratorInterface {
    boolean matches(VignetteSchema schema);
    Document generate(VignetteSchema schema) throws ParserConfigurationException;
}
