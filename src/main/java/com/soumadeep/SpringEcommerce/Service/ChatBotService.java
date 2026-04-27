package com.soumadeep.SpringEcommerce.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatBotService {

    @Autowired
    private ResourceLoader loader;
    @Autowired
    private PgVectorStore vectorStore;
    @Autowired
    final private ChatClient chatClient;

    ChatMemory chatMemory= MessageWindowChatMemory.builder().build();

    private ChatBotService(ChatClient.Builder builder){
        this.chatClient=builder
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build())
                .build();
    }

    public String getBotResponse(String message) {

        try {
            String promptStringTemplate= Files.readString(
                    loader.getResource("classpath:prompts/chatbot-rag-prompt.st")
                            .getFile()
                            .toPath()
            );

            String context=fetchSemanticContext(message);

            Map<String, Object> variables=new HashMap<>();
            variables.put("context",context);
            variables.put("userQuery",message);

            PromptTemplate promptTemplate= PromptTemplate.builder()
                    .template(promptStringTemplate)
                    .variables(variables)
                    .build();

            return chatClient.prompt(promptTemplate.create()).call().content();

        } catch (IOException e) {
            return "Bot Failed "+e.getMessage();
        }
    }

    private String fetchSemanticContext(String message) {
        List<Document> documents=vectorStore.doSimilaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(5)
                        .similarityThreshold(0.7)
                        .build()
        );

        StringBuilder stringBuilder=new StringBuilder();

        for(Document document:documents){
            stringBuilder.append(document.getFormattedContent()).append("\n");
        }
        return stringBuilder.toString();
    }
}
