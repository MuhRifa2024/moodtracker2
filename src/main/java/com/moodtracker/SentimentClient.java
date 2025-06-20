package com.moodtracker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SentimentClient {
    public static int getSentiment(String text) throws Exception {
        String json = String.format("{\"text\": \"%s\"}", text.replace("\"", "\\\""));
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://127.0.0.1:8000/predict"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        // Ambil nilai stars dari response JSON
        int stars = Integer.parseInt(body.replaceAll("[^0-9]", ""));
        return stars;
    }

    // Mapping ke label
    public static String mapStarsToLabel(int stars) {
        switch (stars) {
            case 1: return "Very Negative";
            case 2: return "Negative";
            case 3: return "Neutral";
            case 4: return "Positive";
            case 5: return "Very Positive";
            default: return "Unknown";
        }
    }
}
