import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class helper {
    public static String getTargetLanguage() {
        ConfigurationFile config = new ConfigurationFile();
        return config.getValueByKey("language");
    }

    public static List<sourceText> readTexts(String filePath) {
        List<sourceText> texts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] values = line.split("\t", -1);
                if (values.length < 5) continue;

                sourceText text = new sourceText(
                        values[0],
                        values[1],
                        values[2],
                        values[3],
                        values[4]
                );
                texts.add(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return texts;
    }
}
