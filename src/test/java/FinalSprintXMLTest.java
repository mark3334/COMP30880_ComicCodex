import org.junit.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FinalSprintXMLTest {

    //List of required XML files
    private final String[] requiredXMLFiles = {
            "Sprint6_FinalAudioFile.xml",
            "Sprint4verbs.xml",
            "left_scenes.xml",
            "whole_scenes.xml"
    };

    @Test
    public void testAllXMLFilesExist() {
        for (String fileName : requiredXMLFiles) {
            File file = new File(FileParser.getRootDirectory() + "\\Resources\\XMLoutput\\" + fileName);
            System.out.println(file.getAbsolutePath());
            assertTrue(file.exists(), "File " + fileName + " is missing!");
        }
    }
}
