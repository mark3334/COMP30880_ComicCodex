import java.util.List;
import java.util.Arrays;

public class sourceText {
    private final String leftPose;
    private final List<String> combinedText;
    private final List<String> leftText;
    private final List<String> rightPose;
    private final List<String> backgrounds;

    public sourceText(String leftPose, String combinedText, String leftText, String rightPose, String backgrounds) {
        this.leftPose = leftPose;
        this.combinedText = Arrays.asList(combinedText.split(", "));
        this.leftText = Arrays.asList(leftText.split(", "));
        this.rightPose = Arrays.asList(rightPose.split(", "));
        this.backgrounds = Arrays.asList(backgrounds.split(", "));
    }

    public String getLeftPose() { return leftPose; }
    public List<String> getCombinedText() { return combinedText; }
    public List<String> getLeftText() { return leftText; }
    public List<String> getRightPose() { return rightPose; }
    public List<String> getBackgrounds() { return backgrounds; }

    @Override
    public String toString() {
        return "VignetteData{" +
                "leftPose='" + leftPose + '\'' +
                ", combinedText=" + combinedText +
                ", leftText=" + leftText  +
                ", rightPose=" + rightPose +
                ", backgrounds=" + backgrounds +
                '}';
    }
}
