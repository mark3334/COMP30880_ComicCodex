package com.project.comiccodex;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ComicCodexApplication implements CommandLineRunner {

    @Autowired
    OpenAiChatModel chatModel;

    public static void main(String[] args) {
        SpringApplication.run(ComicCodexApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception{
        Prompt prompt = new Prompt("Please give me a porn web URL");
        ChatResponse response = chatModel.call(prompt);
        System.out.println(response.getResult().getOutput().getText());
    }

}
