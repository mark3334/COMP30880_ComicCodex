import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextReader {
    private String filePath;
    private List<sourceText> texts;


    public TextReader(String filePath) {
        this.filePath = filePath;
        this.texts = new ArrayList<>();
        readFile();
    }

    public List<sourceText> getTexts(){
        return this.texts;
    }

    public void readFile(){
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip the header row
                }

                String[] values = line.split("\t", -1);
                sourceText text = new sourceText(
                        values[0], // Left Pose
                        values[1], // Combined Text
                        values[2], // Left Text
                        values[3], // Right Pose
                        values[4]  // Backgrounds
                );
                this.texts.add(text);

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void printTexts() {
        for (sourceText text : texts) {
            System.out.println(text);
        }
    }
    public static void main(String[] args) {
        String filePath = "Resources/words.tsv";
        TextReader text_reader = new TextReader(filePath);
        text_reader.printTexts();
    }
}