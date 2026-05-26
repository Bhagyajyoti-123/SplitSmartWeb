package com.splitsmart.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.*;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String getSpendingInsight(Map<String, Double> breakdown, double total) {
        if (apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
            return "Add your Gemini API key in application.properties to enable AI insights.";
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
                    prompt.replace("\"", "\\\"").replace("\n", "\\n"));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String resp = response.body();
            int start = resp.indexOf("\"text\": \"") + 9;
            int end = resp.indexOf("\"", start);
            if (start > 9 && end > start) {
                return resp.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
            }
            return "Could not parse AI response.";
        } catch (Exception e) {
            return "AI unavailable: " + e.getMessage();
        }
    }
}
