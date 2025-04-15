import java.nio.file.Path;
import java.util.Map;

public class AudioIndex implements AudioIndexProperties {
    private Map<String, String> indexes;
    private int nextIndex = 0;
    private Path AUDIO_INDEX_PATH = Path.of("./");
    private Path AUDIO_MP3_PATH = Path.of("./");

    public AudioIndex (Map<String, String> index, Path indexPath) {

    }

    @Override
    public void load() {
    }

    @Override
    public void save() {
    }

    @Override
    public String getOrAdd(String text, String language) {
        return "";
    }

    @Override
    public boolean contains(String text, String language) {
        return false;
    }
}
