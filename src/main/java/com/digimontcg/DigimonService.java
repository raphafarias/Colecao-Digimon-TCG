package com.digimontcg;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

public class DigimonService {

    // Método que busca TODOS os cards da API oficial para alimentar o nosso catálogo
    public static Carta[] sincronizarCatalogoCompleto() {
        try {
            // Endpoint público para trazer a lista completa de cartas do bloco clássico/atual
            String url = "https://digimoncard.io/api-public/getAllCards.php?series=Digimon%20Card%20Game&sort=name&sortdirection=asc";

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(Redirect.NORMAL)
                    .build();
                    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return new Gson().fromJson(response.body(), Carta[].class);
            } else {
                System.out.println("Erro na sincronização global. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Erro ao sincronizar catálogo completo: " + e.getMessage());
        }
        return null;
    }

    // Método para buscar pacotes específicos por correspondência de nome
    public static Carta[] buscarCartaPorNome(String nome) {
        try {
            String nomeFormatado = URLEncoder.encode(nome.trim(), StandardCharsets.UTF_8);
            String url = "https://digimoncard.io/api-public/search.php?n=" + nomeFormatado;

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(Redirect.NORMAL)
                    .build();
                    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return new Gson().fromJson(response.body(), Carta[].class);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar carta por nome: " + e.getMessage());
        }
        return null;
    }
}