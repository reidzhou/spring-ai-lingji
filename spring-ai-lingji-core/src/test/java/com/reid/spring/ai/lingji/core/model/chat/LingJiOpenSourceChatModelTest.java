package com.reid.spring.ai.lingji.core.model.chat;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

    public static class ObjectHolder<T> {

        private T data;

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    @Test
    void testStreamChatWithIncrementalOutputFalse() throws InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new AssistantMessage("You are a helpful assistant."));

        messageList.add(new UserMessage("How to write a paper?"));

        LingJiOpenSourceChatOptions lingJiOpenSourceChatOptions = new LingJiOpenSourceChatOptions();

        final ObjectHolder<String> holder = new ObjectHolder<>();

        Prompt prompt = new Prompt(messageList, lingJiOpenSourceChatOptions);
        Flux<ChatResponse> chatResponseFlux = lingJiOpenSourceChatModel.stream(prompt);
        Disposable disposable = chatResponseFlux.subscribe(chatResponse -> {
            if (!chatResponse.getResult().getMetadata().getFinishReason().equals(Constants.FINISH_REASON_NULL)) {
                holder.setData(chatResponse.getResult().getOutput().getContent());
                countDownLatch.countDown();
            }

        });

        countDownLatch.await();
        disposable.dispose();
        System.out.println(holder.getData());
        // for example:
        //        Writing a paper, whether it's for academic purposes or for publication, can be a complex process. Here's a general guide to help you through the steps:
        //
        //        ### 1. **Define Your Purpose**
        //                - Understand the assignment or the need for the paper.
        //                - Determine the purpose of your paper (to inform, argue, persuade, etc.).
        //
        //        ### 2. **Choose a Topic**
        //                - Select a topic that interests you and aligns with the purpose.
        //                - Make sure the topic is neither too broad nor too narrow.
        //
        //        ### 3. **Research**
        //                - Gather information from reliable sources such as books, academic journals, and reputable websites.
        //                - Take notes and keep track of your sources for later citation.
        //
        //        ### 4. **Develop a Thesis Statement**
        //                - Your thesis statement should clearly express the main point or argument of your paper.
        //                        - It should be specific, manageable, and provable.
        //
        //        ### 5. **Create an Outline**
        //                - Organize your ideas into an outline to structure your paper.
        //                        - Typically, this includes an introduction, body paragraphs, and a conclusion.
        //
        //        ### 6. **Write the First Draft**
        //                - Start with the introduction, then write the body paragraphs, and finally, the conclusion.
        //                - Don’t worry about perfection; just get your ideas down.
        //
        //        ### 7. **Revise and Edit**
        //                - Review your draft for content, structure, and clarity.
        //                        - Check for grammar, spelling, and punctuation errors.
        //                - Make sure your paper flows logically and coherently.
        //
        //        ### 8. **Cite Your Sources**
        //                - Use the appropriate citation style (APA, MLA, Chicago, etc.) to cite all the sources you used in your research.
        //                - This prevents plagiarism and gives credit to original authors.
        //
        //        ### 9. **Seek Feedback**
        //                - Have someone else read your paper and provide feedback.
        //                - This could be a peer, a teacher, or a mentor.
        //
        //        ### 10. **Finalize Your Paper**
        //                - Incorporate the feedback you received.
        //                - Make any final adjustments to your paper, including formatting.
        //
        //        ### 11. **Submit or Publish**
        //                - If it’s for a class, submit your paper according to the instructor’s guidelines.
        //                - If it’s for publication, follow the submission guidelines of the journal or publisher.
        //
        //        ### Tips:
        //                - Manage your time effectively.
        //                        - Stay organized throughout the process.
        //                - Read widely to improve your writing skills.
        //                - Write regularly to maintain your momentum.
        //
        //        Remember, writing is a process that often involves multiple revisions. Don't be afraid to make changes and improve your work as you go.
    }

    @Test
    void testStreamChatWithIncrementalOutputTrue() throws InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new AssistantMessage("You are a helpful assistant."));

        messageList.add(new UserMessage("How to write a paper?"));

        LingJiOpenSourceChatOptions lingJiOpenSourceChatOptions = new LingJiOpenSourceChatOptions();
        lingJiOpenSourceChatOptions.setIncrementalOutput(true);

        StringBuilder sb = new StringBuilder();


        Prompt prompt = new Prompt(messageList, lingJiOpenSourceChatOptions);
        Flux<ChatResponse> chatResponseFlux = lingJiOpenSourceChatModel.stream(prompt);
        Disposable disposable = chatResponseFlux.subscribe(chatResponse -> {

            sb.append(chatResponse.getResult().getOutput().getContent());
            if (!chatResponse.getResult().getMetadata().getFinishReason().equals(Constants.FINISH_REASON_NULL)) {
                countDownLatch.countDown();
            }

        });

        countDownLatch.await();
        disposable.dispose();
        System.out.println(sb);

        // for example:
        //        Writing a paper, whether it's for academic purposes or for publication, can be a complex process. Here's a general guide to help you through the steps:
        //
        //        ### 1. **Define Your Purpose**
        //                - Understand the assignment or the need for the paper.
        //                - Determine the purpose of your paper: Are you arguing a point, informing, persuading, or analyzing?
        //                        - Identify your audience. This will influence your tone, style, and level of detail.
        //
        //        ### 2. **Choose a Topic**
        //                - Select a topic that interests you and fits the assignment criteria.
        //                - Narrow down broad topics to something more specific and manageable.
        //
        //        ### 3. **Conduct Research**
        //                - Gather information from reliable sources.
        //                - Take detailed notes, making sure to cite sources to avoid plagiarism.
        //                        - Organize your notes and sources to make the writing process smoother.
        //
        //        ### 4. **Create an Outline**
        //                - Organize your ideas and information into a logical structure.
        //                - Typically includes an introduction, body paragraphs, and a conclusion.
        //                - Decide on your main points and how they will be developed.
        //
        //        ### 5. **Write a Draft**
        //                - Start with the introduction, where you introduce the topic and state your thesis.
        //                - Each body paragraph should focus on a single idea that supports your thesis.
        //                - Use transitions to connect ideas and paragraphs.
        //                - Conclude by summarizing your main points and restating your thesis in a new way.
        //
        //        ### 6. **Revise**
        //                - Review your draft for clarity, flow, and coherence.
        //                        - Check for any logical gaps or areas that need more development.
        //                        - Revise for style and tone to ensure it matches your intended audience.
        //
        //        ### 7. **Edit and Proofread**
        //                - Correct any grammar, punctuation, and spelling errors.
        //                - Read your paper aloud to catch errors and awkward phrasing.
        //                - It might be helpful to have someone else read your paper as well.
        //
        //        ### 8. **Cite Your Sources**
        //                - Use the appropriate citation style (APA, MLA, Chicago, etc.) as required by your institution or publication.
        //                        - Include in-text citations and a bibliography or works cited page.
        //
        //        ### 9. **Finalize and Submit**
        //                - Once you are satisfied with your paper, prepare it for submission.
        //                        - Follow any specific formatting guidelines.
        //                - Submit your paper on time.
        //
        //        ### 10. **Reflect**
        //                - After receiving feedback, take time to reflect on what you've learned.
        //                        - Consider how you can improve in your next writing project.
        //
        //                Writing a paper is an iterative process. Don't be afraid to go back and revise earlier sections as you continue to develop your ideas. Happy writing!
    }

}
