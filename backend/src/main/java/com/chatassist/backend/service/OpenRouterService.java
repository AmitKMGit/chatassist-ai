package com.chatassist.backend.service;

import okhttp3.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenRouterService implements AiService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private static final String URL =
            "https://openrouter.ai/api/v1/chat/completions";

    @Override
    public String generateResponse(String prompt) {
        try {
            OkHttpClient client = new OkHttpClient();

            JSONObject body = new JSONObject()
                    .put("model", "mistralai/mistral-7b-instruct:free")
                    .put("messages", new JSONArray()
                            .put(new JSONObject()
                                    .put("role", "user")
                                    .put("content", prompt)));

            Request request = new Request.Builder()
                    .url(URL)
                    .post(RequestBody.create(
                            body.toString(),
                            MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("HTTP-Referer", "http://localhost")
                    .addHeader("X-Title", "ChatAssist-AI")
                    .build();

            Response response = client.newCall(request).execute();
            String res = response.body().string();

            JSONObject json = new JSONObject(res);

            if (json.has("error")) {
                return "OpenRouter error: " +
                        json.getJSONObject("error").getString("message");
            }

            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            return "OpenRouter error: " + e.getMessage();
        }
    }
    
    public List<Double> generateEmbeddingViaChat(String text) {
        try {
            OkHttpClient client = new OkHttpClient();

            String prompt = String.format(
                    "Convert the following text into a numeric embedding vector.\n" +
                    "Output ONLY a JSON array of numbers. No explanation.\n\n" +
                    "Text:\n%s",
                    text
            );

            JSONObject body = new JSONObject()
                    .put("model", "nousresearch/nous-capybara-7b:free")
                    .put("messages", new JSONArray()
                            .put(new JSONObject()
                                    .put("role", "user")
                                    .put("content", prompt)));

            Request request = new Request.Builder()
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .post(RequestBody.create(
                            body.toString(),
                            MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("HTTP-Referer", "http://localhost")
                    .addHeader("X-Title", "ChatAssist-AI")
                    .build();

            Response response = client.newCall(request).execute();
            String res = response.body().string();

            JSONObject json = new JSONObject(res);

            String content = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            // Parse JSON array returned by model
            JSONArray arr = extractJsonArray(content);            List<Double> embedding = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                embedding.add(arr.getDouble(i));
            }

            return embedding;

        } catch (Exception e) {
            throw new RuntimeException("Embedding generation failed", e);
        }
    }
    
    private JSONArray extractJsonArray(String content) {
        int start = content.indexOf('[');
        int end = content.lastIndexOf(']');

        if (start == -1 || end == -1 || end <= start) {
            throw new RuntimeException("No JSON array found in LLM output: " + content);
        }

        return new JSONArray(content.substring(start, end + 1));
    }
    @Override
    public Iterable<String> streamResponse(String prompt) {
        // simple fake streaming for now
        String full = generateResponse(prompt);
        return java.util.List.of(full.split(" "));

    }

}
