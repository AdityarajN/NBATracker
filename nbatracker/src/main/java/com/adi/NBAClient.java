package com.adi;


import java.net.URI;
import java.net.http.*;

public class NBAClient {
    private static final String KEY = "b38fc4fa07msh7548568b2581dadp132c71jsn0754f3be2580";
    private static final String HOST = "nba-results-pro.p.rapidapi.com";
    private static final String BASE = "https://nba-results-pro.p.rapidapi.com/nba";

    private static final HttpClient client =
            HttpClient.newHttpClient();

    public static String fetch(String endpoint) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .header("x-rapidapi-key", KEY)
                .header("x-rapidapi-host", HOST)
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request,
                        HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}