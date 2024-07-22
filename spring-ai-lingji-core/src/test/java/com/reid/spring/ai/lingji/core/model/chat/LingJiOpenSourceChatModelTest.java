package com.reid.spring.ai.lingji.core.model.chat;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class LingJiOpenSourceChatModelTest {

    @Resource
    LingJiOpenSourceChatModel lingJiOpenSourceChatModel;

    @Test
    void testSingleQuestionChat() {

        String answer = lingJiOpenSourceChatModel.call("who are you?");
        System.out.println(answer);

    }

    @Test
    void testMultiQuestionChat() {

        List<Message> messageList = new ArrayList<>();
        messageList.add(new AssistantMessage("You are a helpful assistant."));


        // first question
        messageList.add(new UserMessage("who are you?"));
        Prompt prompt = new Prompt(messageList);
        ChatResponse chatResponse = lingJiOpenSourceChatModel.call(prompt);

        String answer = chatResponse.getResult().getOutput().getContent();
        System.out.println(answer);

        // second question
        messageList.add(new AssistantMessage(answer));
        messageList.add(new UserMessage("What can you do for me?"));
        prompt = new Prompt(messageList);
        chatResponse = lingJiOpenSourceChatModel.call(prompt);
        answer = chatResponse.getResult().getOutput().getContent();
        System.out.println(answer);
    }

}
