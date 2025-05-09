package com.example.mind_laundromat.cbt.controller;

import com.example.mind_laundromat.cbt.service.GeminiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/gemini")
@RestController
public class GeminiController {
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private final GeminiService geminiService;

    @Autowired
    public GeminiController(VertexAiGeminiChatModel chatModel, GeminiService geminiService) {
        this.vertexAiGeminiChatModel = chatModel;
        this.geminiService = geminiService;
    }

    @GetMapping("/chat")
    public Map<String, String> chat(@RequestBody String message) {
        Map<String, String> responses = new HashMap<>();

        String vertexAiGeminiResponse = vertexAiGeminiChatModel.call(message);
        responses.put("gemini", vertexAiGeminiResponse);
        return responses;
    }

    @GetMapping("/chat/test")
    public Map<String, String> chat(@RequestBody String message, HttpSession session) {
        Map<String, String> responses = new HashMap<>();
        Map<String, Object> sessionData = (Map<String, Object>) session.getAttribute("chatbotSession");
        if (sessionData == null) {
            sessionData = new HashMap<>();
            session.setAttribute("chatbotSession", sessionData);
        }

        String geminiResponse = geminiService.processMessage(message, sessionData);
        responses.put("gemini", geminiResponse);
        return responses;
    }
}
