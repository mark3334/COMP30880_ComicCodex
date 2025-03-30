
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

        String combinedTextTranslated = vignetteManager.translateToSpanish(VignetteSchema.getRandomElement(schema.getCombinedText()));

        return String.format("""
            <scene>
              <background>%s</background>

              <panel>
                <left pose="%s">
                  <balloon status="speech">
                    <content>%s</content>
                  </balloon>
                </left>
                <duration>5000</duration>
              </panel>

              <panel>
                <left pose="%s"/>
                <right pose="%s"/>
                <below>%s</below>
                <duration>5000</duration>
              </panel>

              <panel>
                <left pose="%s">
                  <balloon status="speech">
                    <content>%s</content>
                  </balloon>
                </left>
                <duration>5000</duration>
              </panel>

              <panel>
                <left pose="%s"/>
                <right pose="%s">
                  <balloon status="speech">
                    <content>%s</content>
                  </balloon>
                </right>
                <duration>5000</duration>
              </panel>
            </scene>
            """,
                background,
                leftPose, leftText,
                leftPose, rightPose, combinedText,
                leftPose, combinedText,
                leftPose, rightPose, combinedTextTranslated
        );
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

        String combinedTextTranslated = vignetteManager.translateToSpanish(VignetteSchema.getRandomElement(schema.getCombinedText()));

        return String.format("""
            <scene>
              <background>%s</background>

              <panel>
                <left pose="%s"/>
                <right pose="%s">
                  <balloon status="speech">
                    <content>%s</content>
                  </balloon>
                </right>
                <below>%s</below>
                <duration>5000</duration>
              </panel>

              <!-- Panel 2: -->
              <panel>
                <left pose="%s">
                  <balloon status="speech">
                    <content>%s</content>
                  </balloon>
                </left>
                <duration>5000</duration>
              </panel>

              <!-- Panel 3: -->
              <panel>
                <left pose="%s"/>
                <right pose="%s">
                  <balloon status="speech">
                    <content>%s</content>
                  </balloon>
                </right>
                <duration>5000</duration>
              </panel>

            </scene>
            """,
                background,
                leftPose, rightPose, combinedText, combinedText,
                leftPose, combinedText,
                leftPose, rightPose, combinedTextTranslated
        );
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

        String defaultRightPose = "neutral";
        String combinedTextTranslated = vignetteManager.translateToSpanish(VignetteSchema.getRandomElement(schema.getCombinedText()));

        StringBuilder sb = new StringBuilder();
        sb.append("<scene>\n");
        sb.append(String.format("  <background>%s</background>\n", background));

        // Panel 1:
        sb.append("  <panel>\n");
        sb.append(String.format("    <left pose=\"%s\">\n", leftPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", combinedText));
        sb.append("      </balloon>\n");
        sb.append("    </left>\n");
        sb.append(String.format("    <right pose=\"%s\"/>\n", defaultRightPose));
        sb.append("    <duration>5000</duration>\n");
        sb.append("  </panel>\n");

        // Panel 2:
        sb.append("  <panel>\n");
        sb.append(String.format("    <left pose=\"%s\"/>\n", leftPose));
        sb.append(String.format("    <right pose=\"%s\">\n", defaultRightPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", combinedTextTranslated));
        sb.append("      </balloon>\n");
        sb.append("    </right>\n");
        sb.append("    <duration>5000</duration>\n");
        sb.append("  </panel>\n");

        sb.append("</scene>\n");
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
        String leftTextTranslated = vignetteManager.translateToSpanish(VignetteSchema.getRandomElement(schema.getLeftText()));

        StringBuilder sb = new StringBuilder();
        sb.append("<scene>\n");

        // Panel 1:
        sb.append("  <panel>\n");
        sb.append(String.format("    <left pose=\"%s\">\n", leftPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", leftText));
        sb.append("      </balloon>\n");
        sb.append("    </left>\n");
        sb.append("    <duration>5000</duration>\n");
        sb.append("  </panel>\n");

        // Panel 2:
        sb.append("  <panel>\n");
        sb.append(String.format("    <left pose=\"%s\">\n", leftPose));
        sb.append("      <balloon status=\"speech\">\n");
        sb.append(String.format("        <content>%s</content>\n", leftTextTranslated));
        sb.append("      </balloon>\n");
        sb.append("    </left>\n");
        sb.append("    <duration>5000</duration>\n");
        sb.append("  </panel>\n");

        sb.append("</scene>\n");
        return sb.toString();
    }

    private boolean isNonEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}