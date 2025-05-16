package com.example.mind_laundromat.cbt.service;

import com.example.mind_laundromat.cbt.dto.CreateCbtRequest;
import com.example.mind_laundromat.cbt.entity.DistortionType;
import com.example.mind_laundromat.cbt.entity.EmotionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private final CbtService cbtService;

    private static final String INITIAL_PROMPT_TEMPLATE = "You are a CBT-based chatbot that helps users with their mental health. " +
            "Please empathize with the user's input, ask questions about what emotions they are feeling, " +
            "and what thoughts they might be having.";

    private static final String IDENTIFY_DISTORTION_PROMPT_TEMPLATE = "You are a CBT-based chatbot that supports users with their mental well-being. " +
            "The following is a summary of the previous conversation: {사용자가 기록한 생각}. This summary is just for reference. " +
            "Focus on the following last message: {마지막 메시지}. If you find any cognitive distortions in this message, " +
            "please identify the distortion type and ask a question to help guide the user toward a healthier way of thinking. " +
            "Here is the list of possible cognitive distortions: {cognitiveDistortionList}. " +
            "If you don’t find any cognitive distortions, show empathy for the user's emotions and continue the conversation naturally. " +
            "※ If you feel the conversation has progressed enough and the user's emotions and thoughts are organized, " +
            "suggest ending the session naturally by saying something like, \"Shall we wrap up for today?\" " +
            "If not, continue the conversation with empathy and follow-up questions.";

    public String processMessage(String userMessage, Map<String, Object> sessionData, boolean isEnd, String email, EmotionType emotion) throws JsonProcessingException {
        String prompt;

        if (sessionData.isEmpty() || !sessionData.containsKey("conversationHistory")) {
            prompt = INITIAL_PROMPT_TEMPLATE;
        } else {
            List<String> history = (List<String>) sessionData.get("conversationHistory");

            StringBuilder fullConversation = new StringBuilder();
            for (String message : history) {
                fullConversation.append(message).append("\n");
            }

            String summaryPrompt = "Below is a conversation between a user and a counselor.\n" +
                    "When summarizing this conversation, do not simply summarize the mood, but be sure to include:\n" +
                    "- Repeated cognitive distortions the user has shown (e.g., black-and-white thinking, overgeneralization)\n" +
                    "- Key emotions expressed by the user (e.g., anxiety, helplessness, self-blame)\n" +
                    "- Repeated thought patterns observed in the user\n" +
                    "- Main feedback or types of questions provided by the counselor\n" +
                    "Please summarize in 3 to 10 sentences without missing key content.\n" +
                    "Conversation:" + fullConversation;

            String summary = vertexAiGeminiChatModel.call(summaryPrompt);

            if (isEnd) {
                history.add("User: " + userMessage);
                sessionData.put("conversationHistory", history);
                CreateCbtRequest request = saveCbtFromSummary(summary, email, emotion);
                if (request != null) {
                    Long diaryId = cbtService.createCbt(request);
                    return String.valueOf(diaryId);
                }
                return "Session ended.";
            }

            history.add("Summary: " + summary);

            if (true) { // Replace with actual distortion detection logic
                String cognitiveDistortionList = String.join(", ", DistortionType.getAllDistortions());
                prompt = IDENTIFY_DISTORTION_PROMPT_TEMPLATE.replace("{사용자가 기록한 생각}", summary)
                        .replace("{마지막 메시지}", userMessage)
                        .replace("{cognitiveDistortionList}", cognitiveDistortionList);
            } else {
                prompt = "I understand how you're feeling. Could you share more details with me?";
            }
        }

        String finalPrompt = prompt + "\nUser message: " + userMessage;

        String response = vertexAiGeminiChatModel.call(finalPrompt);

        List<String> history = (List<String>) sessionData.getOrDefault("conversationHistory", new ArrayList<>());
        history.add("User: " + userMessage);
        history.add("Counselor: " + response);
        sessionData.put("conversationHistory", history);

        return response;
    }

    public CreateCbtRequest saveCbtFromSummary(String summary, String email, EmotionType emotion) throws JsonProcessingException {
        try {
            String diaryPrompt = String.format("""
                    Based on the following summary, please write a CBT diary entry in JSON format using the structure below.
                    - 'summation' should be a **concise one-sentence summary** of the user’s experience. Example: "Got angry at a teammate and regretted it."
                    For the 'distortions' field, please only use the following Enum values (comma-separated):
                    [BLACK_AND_WHITE_THINKING, OVERGENERALIZATION, MENTAL_FILTER, DISQUALIFYING_THE_POSITIVE, EMOTIONAL_REASONING, JUMPING_TO_CONCLUSIONS, MIND_READING, FORTUNE_TELLING, MAGNIFICATION, MINIMIZATION, PERSONALIZATION, SHOULD_STATEMENTS, LABELING, CONTROL_FALLACY, FALLACY_OF_FAIRNESS, BLAMING]

                    Summary:
                    %s

                    {
                      "before_content": "...",
                      "after_content": "...",
                      "summation": "...",
                      "solution": "...",
                      "distortions": ["..."]
                    }
                    """, summary);

            String json = vertexAiGeminiChatModel.call(diaryPrompt);

            json = json.replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```", "")
                    .trim();

            System.out.println("✅ Gemini Response (Cleaned):\n" + json);

            ObjectMapper mapper = new ObjectMapper();
            CreateCbtRequest request = mapper.readValue(json, CreateCbtRequest.class);
            request.setEmail(email);
            request.setEmotion_type(emotion);
            return request;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
