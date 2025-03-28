public class comicXMLGenerator {

    private TranslationFile t;
    public static String generateSceneXML(VignetteSchema schema, int sceneId) {
        String leftPose = schema.getLeftPose();
        String leftText = VignetteSchema.getRandomElement(schema.getLeftText());
        String rightPose = VignetteSchema.getRandomElement(schema.getRightPose());
        String background = VignetteSchema.getRandomElement(schema.getBackgrounds());
        String combinedText = VignetteSchema.getRandomElement(schema.getCombinedText());

        TranslationFile t = TranslationFile.getInstance();
        String leftTextTranslated = t.translate(leftText);
        String combinedTextTranslated = t.translate(combinedText);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<scene id=\"%d\">\n", sceneId));
        sb.append(String.format("    <left pose=\"%s\">%s</left>\n",
                leftPose, leftText));
        sb.append(String.format("    <right pose=\"%s\"></right>\n",
                rightPose));
        sb.append(String.format("    <background>%s</background>\n",
                background));
        sb.append(String.format("    <combinedText>%s</combinedText>\n",
                combinedText));
        sb.append("</scene>");
        return sb.toString();
    }

    // Example usage
    public static void main(String[] args) {
        VignetteManager vignetteManager = new VignetteManager();
        VignetteSchema schema = vignetteManager.getRandomSchema();
        System.out.println(schema);

        String xml = generateSceneXML(schema, 1);
        System.out.println(xml);
    }
}
