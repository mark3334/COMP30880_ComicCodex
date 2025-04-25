import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;


public class VignetteSchema {
    private final String leftPose;
    private final List<String> combinedText;
    private final List<String> leftText;
    private final List<String> rightPose;
    private final List<String> backgrounds;

    public VignetteSchema(String leftPose, String combinedText, String leftText, String rightPose, String backgrounds) {
        this.leftPose = leftPose;
        this.combinedText = Arrays.asList(combinedText.split(", "));
        this.leftText = Arrays.asList(leftText.split(", "));
        this.rightPose = Arrays.asList(rightPose.split(", "));
        this.backgrounds = Arrays.asList(backgrounds.split(", "));
    }

    public static String getRandomElement(List<String> list) {
        Random rand = new Random();
        if (list.isEmpty()) return null;
        return list.get(rand.nextInt(list.size()));
    }

    private List<String> cleanQuotes(List<String> input) {
        List<String> result = new ArrayList<>();
        for (String s : input) {
            if (s != null) {
                result.add(s.replace("\"", "").trim());
            }
        }
        return result;
    }

    public List<String> getPhrasesToTranslate(){
        return this.combinedText;
    }
    public String getLeftPose() { return leftPose; }
    public List<String> getCombinedText() {
        return cleanQuotes(combinedText);
    }

    public List<String> getLeftText() {
        return cleanQuotes(leftText);
    }

    public List<String> getRightPose() {
        return cleanQuotes(rightPose);
    }

    public List<String> getBackgrounds() {
        return cleanQuotes(backgrounds);
    }


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
