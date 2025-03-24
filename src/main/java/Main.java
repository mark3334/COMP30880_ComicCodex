import java.util.*;

public class Main {

    public static void main(String[] args) {
        /*
        OpenAIClient client = new OpenAIClient();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("--------------------------------");
            System.out.println("| Welcome to ComicCodex project|");
            System.out.println("| Press 'exit' to exit.        |");
            System.out.println("--------------------------------");

            runUserInteraction(client, scanner);
        }
        */

        String englishWord = "hello";
        String spanish = OpenAIClient.translate(englishWord);

        System.out.println("English: " + englishWord);
        System.out.println("Spanish: " + spanish);

    }


}