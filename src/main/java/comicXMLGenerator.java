import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class comicXMLGenerator {
    public static String generateSceneXML(VignetteSchema schema) {
        return SceneGeneratorRegistry.generateScene(schema);
    }

    public static void SceneExporter(){
        VignetteSchema schema = new VignetteManager().getRandomSchema();
        String xml = generateSceneXML(schema);
        String fullContent = "<comic>\n" + xml + "</comic>";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String filePath = "Resources/scene_" + timestamp + ".xml";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(fullContent);
            System.out.println("XML written to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
