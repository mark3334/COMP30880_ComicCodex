import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class textReader {
    public static void main(String[] args) {
        String filePath = "words.tsv";
        List<sourceText> texts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip the header row
                }

                String[] values = line.split("\t", -1);
                /*
                for(int i = 0; i < 5; i++){
                    System.out.print(values[i] + " | ");
                }
                System.out.println();
                */
                sourceText text = new sourceText(
                        values[0], // Left Pose
                        values[1], // Combined Text
                        values[2], // Left Text
                        values[3], // Right Pose
                        values[4]  // Backgrounds
                );
                texts.add(text);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (sourceText text : texts) {
            System.out.println(text);
        }
    }
}
