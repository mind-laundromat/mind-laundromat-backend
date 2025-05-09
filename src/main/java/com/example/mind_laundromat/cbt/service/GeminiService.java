package com.example.mind_laundromat.cbt.service;

import com.example.mind_laundromat.cbt.entity.DistortionType;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;

    private static final String INITIAL_PROMPT_TEMPLATE = "당신은 사용자의 마음 건강을 돕는 CBT 기반 챗봇입니다. " +
            "사용자가 기록한 내용에 대해 공감하며, 어떤 감정을 느끼고 있는지, 어떤 생각을 하고 있는지 질문해주세요.";
    private static final String IDENTIFY_DISTORTION_PROMPT_TEMPLATE = "사용자의 기록을 분석하여 '{사용자가 기록한 생각}'에서 나타나는 인지적 오류를 지적하고, " +
            "더 건강한 생각을 할 수 있도록 질문해주세요. 가능한 인지적 오류 목록: {cognitiveDistortionList}.";

    public GeminiService(VertexAiGeminiChatModel vertexAiGeminiChatModel){
        this.vertexAiGeminiChatModel = vertexAiGeminiChatModel;
    }

    public String processMessage(String userMessage, Map<String, Object> sessionData) {
        String prompt;

        // 초기 대화인 경우 초기 프롬프트 사용
        if (sessionData.isEmpty() || !sessionData.containsKey("conversationHistory")) {
            prompt = INITIAL_PROMPT_TEMPLATE;
        } else {
            List<String> history = (List<String>) sessionData.get("conversationHistory");
            String lastUserMessage = history.get(history.size() - 1);

            if (/* 감정 왜곡 감지 */ true) {
                String cognitiveDistortionList = String.join(", ", DistortionType.getAllDistortions());
                prompt = IDENTIFY_DISTORTION_PROMPT_TEMPLATE.replace("{사용자가 기록한 생각}", lastUserMessage)
                        .replace("{cognitiveDistortionList}", cognitiveDistortionList);
            } else {
                prompt = "사용자님의 이야기에 공감하며, 더 자세히 이야기해 주시겠어요?";
            }
        }

        String finalPrompt = prompt + "\n사용자 메시지: " + userMessage;

        // 대화 내용을 sessionData에 저장
        List<String> history = (List<String>) sessionData.getOrDefault("conversationHistory", new ArrayList<>());
        history.add(userMessage);
        sessionData.put("conversationHistory", history);

        return vertexAiGeminiChatModel.call(finalPrompt);
    }

}
