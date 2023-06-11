package com.example.backend.service;

import com.example.backend.dto.TypesenseResponse;
import com.example.backend.model.Game;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.typesense.api.Client;
import org.typesense.api.Configuration;
import org.typesense.model.SearchParameters;
import org.typesense.resources.Node;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;

@Service
public class TypesenseService {
    private final Client client;
    private final ObjectMapper objectMapper;

    public TypesenseService(@Autowired Environment env) throws URISyntaxException, MalformedURLException {
        val url = new URI(env.getProperty("typesense_url", "http://localhost:8108")).toURL();
        val apiKey = env.getProperty("typesense_api_key");
        val nodes = new ArrayList<Node>() {{
            add(new Node(url.getProtocol(), url.getHost(), String.valueOf(url.getPort())));
        }};
        val configuration = new Configuration(nodes, Duration.ofSeconds(30), apiKey);
        client = new Client(configuration);
        objectMapper = new ObjectMapper();
    }

    public TypesenseResponse search(String q, int page) throws Exception {
        return search(q, page, 10);
    }

    public TypesenseResponse search(String q, int page, int perPage) throws Exception {
//        throw new MalformedQueryException();
        val query = parseQuery(q);
        val searchParameters = new SearchParameters()
                .q(query.getLeft())
                .queryBy("opening, mainlineMoves, positions")
                .filterBy(query.getRight())
                .page(page)
                .perPage(perPage);
        val response = client.collections("chess").documents().search(searchParameters);
        val documents = response.getHits();
        val count = response.getFound();
        val pages = (int) Math.ceil(response.getFound() / (double) perPage);
        val games = documents
                .stream()
                .map(document -> objectMapper.convertValue(document.getDocument(), Game.class));
        return new TypesenseResponse(count, page, pages, games.toList());
    }

    public Pair<String, String> parseQuery(String q) {
        return Pair.of(q, "");
    }
}