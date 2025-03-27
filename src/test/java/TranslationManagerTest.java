import org.junit.jupiter.api.Test;


public class TranslationManagerTest {


    @Test
    void testTranslationManager(){
        String filePath = "Resources/test.tsv";
        TextReader text_reader = new TextReader(filePath);
        TranslationManager t = new TranslationManager("Resources/Translations_Test.txt");
        t.processAll(text_reader.getTexts());
    }

}
