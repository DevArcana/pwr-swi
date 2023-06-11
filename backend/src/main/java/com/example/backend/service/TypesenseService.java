package com.example.backend.service;

import com.example.backend.dto.TypesenseResponse;
import com.example.backend.exception.MalformedQueryException;
import com.example.backend.model.Game;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.typesense.api.Client;
import org.typesense.api.Configuration;
import org.typesense.api.exceptions.RequestMalformed;
import org.typesense.model.SearchParameters;
import org.typesense.model.SearchResultHit;
import org.typesense.resources.Node;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class TypesenseService {
    private final Client client;
    private final ObjectMapper objectMapper;
    private final Comparator<SearchResultHit> sortOpenings =
            Comparator.comparingInt((SearchResultHit hit) -> {
                val highlights = hit.getHighlights().stream().map(searchHighlight -> searchHighlight.getSnippet() == null ? "" : searchHighlight.getSnippet());
                val positions = highlights.map(searchHighlight -> searchHighlight.indexOf("<mark>")).map(index -> index == -1 ? Integer.MAX_VALUE : index);
                val minPosition = positions.min(Integer::compare);
                return minPosition.orElse(Integer.MAX_VALUE);
            });

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

    private static <T> Predicate<T> distinctByField(Function<? super T, ?> fieldExtractor) {
        Set<String> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add((String) fieldExtractor.apply(t));
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
        try {
            val response = client.collections("chess").documents().search(searchParameters);
            val documents = response.getHits();
            val count = response.getFound();
            val pages = (int) Math.ceil(response.getFound() / (double) perPage);
            val games = documents
                    .stream()
                    .map(document -> objectMapper.convertValue(document.getDocument(), Game.class));
            return new TypesenseResponse(count, page, pages, games.toList());
        } catch (RequestMalformed ex) {
            throw new MalformedQueryException();
        }
    }

    public List<String> searchOpenings(String q) throws Exception {
        val searchParameters = new SearchParameters()
                .q(q)
                .queryBy("opening")
                .includeFields("opening")
                .groupLimit(1)
                .prioritizeTokenPosition(true)
                .perPage(250);
        try {
            val response = client.collections("chess").documents().search(searchParameters);
            return response
                    .getHits()
                    .stream()
                    .filter(distinctByField(hit -> hit.getDocument().get("opening")))
                    .sorted(Comparator.comparing(document -> document.getDocument().get("opening").toString()))
                    .sorted(sortOpenings)
                    .map(hit -> (String) hit.getDocument().get("opening"))
                    .toList();
        } catch (RequestMalformed ex) {
            throw new MalformedQueryException();
        }
    }

    public Pair<String, String> parseQuery(String q) {
        val splitted = Arrays.stream(q.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)")).filter(token -> token.trim().length()>0).toList();
        val filters = new StringBuilder();
        val freeTokens = new StringBuilder();
        var operator = "";
        for (var el : splitted){
            if (el.equals("NOT")) {
                operator=("NOT");
            }
            else if (el.contains(":")) {
                if (!filters.isEmpty())
                    filters.append(" && ");
                if (operator.equals("NOT")){
                    el = el.replace(":", ":!=");
                    operator = "";
                }
                filters.append(el);
            }
            else {
                if (!freeTokens.isEmpty())
                    freeTokens.append(" ");
                if (operator.equals("NOT")){
                    el = "-" + el;
                    operator = "";
                }
                freeTokens.append(el);
            }
        }
        return Pair.of(freeTokens.toString(), filters.toString());
    }


}