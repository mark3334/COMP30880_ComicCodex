
public class comicXMLGenerator {

    public static String generateFiguresXML() {
        Figure figure1 = Figure.generateRandomFigure("left");
        Figure figure2 = Figure.generateRandomFigure("right");

        return "  <figures>\n" +
                figure1.toXML() +
                figure2.toXML() +
                "  </figures>\n";
    }

    public static String generateSceneXML(VignetteSchema schema) {
        return SceneGeneratorRegistry.generateScene(schema);
    }


    //TODO
    //process lesson 3 specification to get figures
    //figure.toString should produce the same XML that's in example comiXML at the <figure tag>
    //figure class? - name, hair... etc
    //List<figure> goes into comicXMLGenerator
    //comicXMLGenerator.printFigures
    //each row is one scene.
    // Example usage
    public static void main(String[] args) {
        VignetteSchema schema = new VignetteManager().getRandomSchema();
        String xml = generateSceneXML(schema);
        System.out.println(schema);

        System.out.println("<comic>");
        //System.out.println(generateFiguresXML());
        System.out.println(xml);
        System.out.println("</comic>");
    }
}
