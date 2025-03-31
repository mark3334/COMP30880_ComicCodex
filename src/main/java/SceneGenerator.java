class FullSceneGenerator implements SceneGeneratorInterface {
    @Override
    public boolean matches(VignetteSchema schema) {
        return isNonEmpty(schema.getLeftPose()) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getLeftText())) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getRightPose())) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getCombinedText())) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getBackgrounds()));
    }

    @Override
    public String generate(VignetteSchema schema) {
        VignetteManager vignetteManager = new VignetteManager();
        String leftPose = schema.getLeftPose();
        String leftText = VignetteSchema.getRandomElement(schema.getLeftText());
        String rightPose = VignetteSchema.getRandomElement(schema.getRightPose());
        String background = VignetteSchema.getRandomElement(schema.getBackgrounds());
        String combinedText = VignetteSchema.getRandomElement(schema.getCombinedText());
        String combinedTextTranslated = vignetteManager.translateToTarget(combinedText);

        Figure leftFigure = Figure.generateRandomFigure("right");
        Figure rightFigure = Figure.generateRandomFigure("left");
        String leftName = leftFigure.getName();
        String rightName = rightFigure.getName();

        StringBuilder sb = new StringBuilder();
        sb.append("  <scene>\n");
        sb.append(String.format("    <background>%s</background>\n", background));

        // Panel 1
        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", leftText));
        sb.append("      </balloon>\n");
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        // Panel 2
        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append(String.format("      <right>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>left</facing>\n        </figure>\n      </right>\n", rightName, rightPose));
        sb.append(String.format("      <below>%s</below>\n", combinedText));
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        // Panel 3
        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", combinedText));
        sb.append("      </balloon>\n");
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        // Panel 4
        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append(String.format("      <right>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>left</facing>\n        </figure>\n      </right>\n", rightName, rightPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", combinedTextTranslated));
        sb.append("      </balloon>\n");
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        sb.append("  </scene>\n");
        return sb.toString();
    }

    private boolean isNonEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

class NoLeftTextSceneGenerator implements SceneGeneratorInterface {
    @Override
    public boolean matches(VignetteSchema schema) {
        return isNonEmpty(schema.getLeftPose()) &&
                isEmpty(VignetteSchema.getRandomElement(schema.getLeftText())) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getRightPose())) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getCombinedText())) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getBackgrounds()));
    }

    @Override
    public String generate(VignetteSchema schema) {
        VignetteManager vignetteManager = new VignetteManager();
        String leftPose = schema.getLeftPose();
        String rightPose = VignetteSchema.getRandomElement(schema.getRightPose());
        String combinedText = VignetteSchema.getRandomElement(schema.getCombinedText());
        String background = VignetteSchema.getRandomElement(schema.getBackgrounds());
        String combinedTextTranslated = vignetteManager.translateToTarget(combinedText);

        Figure leftFigure = Figure.generateRandomFigure("right");
        Figure rightFigure = Figure.generateRandomFigure("left");
        String leftName = leftFigure.getName();
        String rightName = rightFigure.getName();

        StringBuilder sb = new StringBuilder();
        sb.append("  <scene>\n");
        sb.append(String.format("    <background>%s</background>\n", background));

        // Panel 1
        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append(String.format("      <right>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>left</facing>\n        </figure>\n      </right>\n", rightName, rightPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", combinedText));
        sb.append("      </balloon>\n");
        sb.append(String.format("      <below>%s</below>\n", combinedText));
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        // Panel 2
        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", combinedText));
        sb.append("      </balloon>\n");
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        // Panel 3
        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append(String.format("      <right>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>left</facing>\n        </figure>\n      </right>\n", rightName, rightPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", combinedTextTranslated));
        sb.append("      </balloon>\n");
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        sb.append("  </scene>\n");
        return sb.toString();
    }

    private boolean isNonEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

class OnlyCombinedTextSceneGenerator implements SceneGeneratorInterface {
    @Override
    public boolean matches(VignetteSchema schema) {
        return isNonEmpty(schema.getLeftPose()) &&
                isEmpty(VignetteSchema.getRandomElement(schema.getLeftText())) &&
                isEmpty(VignetteSchema.getRandomElement(schema.getRightPose())) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getCombinedText())) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getBackgrounds()));
    }

    @Override
    public String generate(VignetteSchema schema) {
        VignetteManager vignetteManager = new VignetteManager();
        String leftPose = schema.getLeftPose();
        String combinedText = VignetteSchema.getRandomElement(schema.getCombinedText());
        String background = VignetteSchema.getRandomElement(schema.getBackgrounds());
        String combinedTextTranslated = vignetteManager.translateToTarget(combinedText);

        Figure leftFigure = Figure.generateRandomFigure("right");
        Figure rightFigure = Figure.generateRandomFigure("left");
        String leftName = leftFigure.getName();
        String rightName = rightFigure.getName();

        StringBuilder sb = new StringBuilder();
        sb.append("  <scene>\n");
        sb.append(String.format("    <background>%s</background>\n", background));

        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append(String.format("      <right>\n        <figure>\n          <id>%s</id>\n          <pose>neutral</pose>\n          <facing>left</facing>\n        </figure>\n      </right>\n", rightName));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", combinedText));
        sb.append("      </balloon>\n");
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append(String.format("      <right>\n        <figure>\n          <id>%s</id>\n          <pose>neutral</pose>\n          <facing>left</facing>\n        </figure>\n      </right>\n", rightName));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", combinedTextTranslated));
        sb.append("      </balloon>\n");
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        sb.append("  </scene>\n");
        return sb.toString();
    }

    private boolean isNonEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

class LeftTextOnlySceneGenerator implements SceneGeneratorInterface {
    @Override
    public boolean matches(VignetteSchema schema) {
        return isNonEmpty(schema.getLeftPose()) &&
                isNonEmpty(VignetteSchema.getRandomElement(schema.getLeftText())) &&
                isEmpty(VignetteSchema.getRandomElement(schema.getRightPose())) &&
                isEmpty(VignetteSchema.getRandomElement(schema.getCombinedText())) &&
                isEmpty(VignetteSchema.getRandomElement(schema.getBackgrounds()));
    }

    @Override
    public String generate(VignetteSchema schema) {
        VignetteManager vignetteManager = new VignetteManager();
        String leftPose = schema.getLeftPose();
        String leftText = VignetteSchema.getRandomElement(schema.getLeftText());
        String leftTextTranslated = vignetteManager.translateToTarget(leftText);

        Figure leftFigure = Figure.generateRandomFigure("right");
        String leftName = leftFigure.getName();

        StringBuilder sb = new StringBuilder();
        sb.append("  <scene>\n");

        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", leftText));
        sb.append("      </balloon>\n");
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        sb.append("    <panel>\n");
        sb.append(String.format("      <left>\n        <figure>\n          <id>%s</id>\n          <pose>%s</pose>\n          <facing>right</facing>\n        </figure>\n      </left>\n", leftName, leftPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", leftTextTranslated));
        sb.append("      </balloon>\n");
        sb.append("      <duration>5000</duration>\n");
        sb.append("    </panel>\n");

        sb.append("  </scene>\n");
        return sb.toString();
    }

    private boolean isNonEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
