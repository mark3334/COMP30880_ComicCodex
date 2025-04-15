import java.nio.file.Path;
import java.util.Map;

public interface AudioIndexProperties {

    void load(); //Load from disk to HashMap, updating the nextIndex at the same time.

    void save(); //Save From Hashmap to Disk, updating the nextIndex at the same time.


    /*
     * Returns the index name (e.g., "003") of the .mp3 file for the given text in the target language.
     * If not already present, generates a new index and returns it.
     */
    String getOrAdd(String text, String language);


    /*
     * Checks if a certain text-language pair already has an assigned index.
     */
    boolean contains(String text, String language);

}
