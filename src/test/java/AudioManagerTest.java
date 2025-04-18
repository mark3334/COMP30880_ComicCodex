import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class AudioManagerTest {

    private static Path tempIndexFile;
    private static Path tempAudioFolder;
    private static AudioManager audioManager;

    @BeforeEach
    void setupOnce() throws IOException {
        tempIndexFile = Path.of("Resources/Audio/Tests", "indexeTest.txt");
        tempAudioFolder = Path.of("Resources/Audio/Tests", "mp3FileTest");

        // Ensure folders exist
        Files.createDirectories(tempAudioFolder.getParent());
        Files.createDirectories(tempAudioFolder);

        if (Files.exists(tempIndexFile)) {
            Files.write(tempIndexFile, new byte[0]); // clear file
        } else {
            Files.createFile(tempIndexFile);
        }

        audioManager = AudioManager.getInstance(tempIndexFile.toString(), tempAudioFolder.toString());
    }

//    @AfterEach
//    void cleanupOnce() throws IOException {
//        System.out.println("Deleting "+tempIndexFile.getFileName());
//        Files.deleteIfExists(tempIndexFile);
//        for (File file : tempAudioFolder.toFile().listFiles()) {
//            System.out.println("Deleting " + file.getName());
//            file.delete();
//        }
//        tempAudioFolder.toFile().delete();
//    }

    @Test
    void testTextNotPresentInitially() {
        assertFalse(audioManager.contains("Hello"));
        assertFalse(audioManager.contains("Hej"));
    }

    @Test
    public void testGetOrAddGeneratesNewEntry() {
        int index1 = audioManager.getOrAdd("Bonjour");
        int index2 = audioManager.getOrAdd("Hola");

        assertNotEquals(index1, index2);
        assertTrue(audioManager.contains("Bonjour"));
        assertTrue(audioManager.contains("Hola"));


        File audioFolder = new File(FileParser.getRootDirectory(), tempAudioFolder.toString());
        File outputFile = new File(audioFolder,  1 + ".mp3");
        File outputFile2 = new File(audioFolder,  1 + ".mp3");
        assertTrue(outputFile.exists());
        assertTrue(outputFile2.exists());
    }

    @Test
    public void testGetOrAddReturnsSameIndexForSameText() {
        int firstIndex = audioManager.getOrAdd("Ciao");
        int secondIndex = audioManager.getOrAdd("Ciao");
        assertEquals(firstIndex, secondIndex);
    }

    @Test
    public void testAppendSingleEntryCreatesCorrectFormat() throws IOException {
        audioManager.appendSingleEntry("Testing", 123);

        List<String> lines = Files.readAllLines(tempIndexFile);
        assertEquals(1, lines.size());
        //System.out.println("Line 0; "+lines.get(0));
        assertTrue(lines.get(0).contains("Testing "+FileParser.getDelimiterLiteral()+" 123"));
    }
}
