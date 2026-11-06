package com.tarot.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class McpService {

    private final ChatClient chatClient;

    public McpService(ChatClient.Builder chatClientBuilder,
                      Advisor[] advisors,
                      @Value("${app.chat.default-system-prompt:}") String systemPrompt,
                      ToolCallbackProvider[] toolCallbackProviders) {

        // 1. ToolCallback 추출 및 로깅
        List<ToolCallback> callbackList = new ArrayList<>(); // 툴 콜백들을 임시로 담아둘 리스트 생성

        for (ToolCallbackProvider provider : toolCallbackProviders) {
            for (ToolCallback callback : provider.getToolCallbacks()) {

                log.info("Tool loaded - name: {}",
                        callback.getToolDefinition().name());

                callbackList.add(new ToolCallback() {

                    @Override
                    public ToolDefinition getToolDefinition() {
                        return callback.getToolDefinition();
                    }

                    @Override
                    public ToolMetadata getToolMetadata() {
                        return callback.getToolMetadata();
                    }

                    @Override
                    public String call(String toolInput) {
                        log.info("Tool Calling 실행: {}", callback.getToolDefinition().name());
                        log.info("Tool Input: {}", toolInput);

                        String result = callback.call(toolInput);

                        log.info("Tool Result: {}", result);
                        return result;
                    }
                });
            }
        }

        // 리스트에 모인 콜백들을 ChatClient 설정에 넣기 위해 배열 형태로 변환
        ToolCallback[] toolCallbacks = callbackList.toArray(new ToolCallback[0]);

        // 2. ChatClient 설정
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .defaultOptions(ToolCallingChatOptions.builder().temperature(0.2).build())
                .defaultAdvisors(advisors)
                .defaultToolCallbacks(toolCallbacks)
                .build();
    }

    private ChatClient.ChatClientRequestSpec createRequest(String conversationId, Prompt prompt) {
        return chatClient.prompt(prompt)
                .advisors(advisors -> advisors.param(ChatMemory.CONVERSATION_ID, conversationId));
    }

    public Flux<String> stream(String conversationId, Prompt prompt) {
        return createRequest(conversationId, prompt).stream().content();
    }

    public ChatResponse call(String conversationId, Prompt prompt) {
        return createRequest(conversationId, prompt).call().chatResponse();
    }
}