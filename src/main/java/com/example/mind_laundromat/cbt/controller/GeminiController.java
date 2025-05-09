package com.example.mind_laundromat.cbt.controller;

import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/gemini")
@RestController
public class GeminiController {
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;

    @Autowired
    public GeminiController(VertexAiGeminiChatModel chatModel) {
        this.vertexAiGeminiChatModel = chatModel;
    }

    @GetMapping("/chat")
    public Map<String, String> chat(@RequestBody String message) {
        Map<String, String> responses = new HashMap<>();

        String vertexAiGeminiResponse = vertexAiGeminiChatModel.call(message);
        responses.put("gemini", vertexAiGeminiResponse);
        return responses;
    }
}
