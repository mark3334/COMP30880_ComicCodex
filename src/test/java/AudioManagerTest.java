import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


public class AudioManagerTest {

    private static Path tempIndexFile;
    private static Path tempAudioFolder;
    private static AudioManager audioManager;

    @BeforeEach
    void setupOnce() throws IOException {
        tempIndexFile = Files.createTempFile("indexesTest", ".txt");
        tempAudioFolder = Files.createTempDirectory("mp3FilesTest");
        audioManager= AudioManager.getInstance(tempIndexFile.toString(), tempAudioFolder.toString());
    }

    @AfterEach
    void cleanupOnce() throws IOException {
        System.out.println("Deleting "+tempIndexFile.getFileName());
        Files.deleteIfExists(tempIndexFile);
        for (File file : tempAudioFolder.toFile().listFiles()) {
            System.out.println("Deleting " + file.getName());
            file.delete();
        }
        tempAudioFolder.toFile().delete();
    }

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

        //Still need to test if mp3 file was created through TTS call.
    }

    @Test
    public void testGetOrAddReturnsSameIndexForSameText() {
        int firstIndex = audioManager.getOrAdd("Ciao");
        int secondIndex = audioManager.getOrAdd("Ciao");
        assertEquals(firstIndex, secondIndex);
    }

    @Test
    public void testAppendSingleEntryCreatesCorrectFormat() {

    }





}
