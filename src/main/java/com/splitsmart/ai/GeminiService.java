package com.splitsmart.ai;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.*;
import java.util.Map;

@Service
public class GeminiService {

    private String apiKey = "YOUR_GEMINI_API_KEY_HERE";

    public String getSpendingInsight(Map<String, Double> breakdown, double total) {
        if (apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
            return "AI insights disabled. Add a valid Gemini API key to enable.";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> e : breakdown.entrySet()) {
            double pct = (e.getValue() / total) * 100;
            sb.append(String.format("%s: %.2f (%.1f%%), ", e.getKey(), e.getValue(), pct));
        }
        String prompt = String.format(
                "Analyze this group expense breakdown and give 3 short practical money-saving tips. " +
                "Total: %.2f. Breakdown: %s. Under 80 words, direct and actionable.", total, sb);
        return callGemini(prompt);
    }

    private String callGemini(String prompt) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" +
                    "gemini-2.0-flash:generateContent?key=" + apiKey;
            String body = String.format(
                    "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
                    prompt.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String resp = response.body();

            int textIdx = resp.indexOf("\"text\":");
            if (textIdx == -1) return "AI unavailable. Response: " + resp.substring(0, Math.min(200, resp.length()));

            int start = resp.indexOf("\"", textIdx + 7) + 1;
            int end = start;
            while (end < resp.length()) {
                if (resp.charAt(end) == '"' && resp.charAt(end - 1) != '\\') break;
                end++;
            }

            return resp.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

        } catch (Exception e) {
            return "AI unavailable: " + e.getMessage();
        }
    }
}