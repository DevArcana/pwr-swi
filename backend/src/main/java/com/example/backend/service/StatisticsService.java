package com.example.backend.service;

import com.example.backend.dto.StatsResponse;
import com.example.backend.model.MoveStat;
import com.example.backend.model.Stat;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class StatisticsService {
    private final TypesenseService typesenseService;

    @Autowired
    public StatisticsService(TypesenseService typesenseService) {
        this.typesenseService = typesenseService;
    }

    public StatsResponse calcStatistics(String q) throws Exception {
        val regex = Pattern.compile("[A-Za-z]+\\S+");
        val typesenseHits = typesenseService.searchStatistics(q, 1, 250);
        val results = typesenseHits
                .parallelStream()
                .map(hit -> (String) hit.getDocument().get("result"))
                .toList();
        if (results.isEmpty())
            return null;
        val coefficient = 1.0 / results.size();
        val highlits =
                typesenseHits
                        .stream()
                        .map(hit -> Pair.of(hit.getHighlights().stream(), (String) hit.getDocument().get("result")))
                        .flatMap(pair -> pair.getLeft().map(highlight -> Pair.of(highlight, pair.getRight())));
        val nextMovesOccurrences = highlits
                .map(highlight -> {
                    val snippet = highlight.getLeft().getSnippet();
                    val snippetPostMark = snippet.substring(snippet.lastIndexOf("</mark>")+7).trim();
                    val matcher = regex.matcher(snippetPostMark);
                    if(matcher.find())
                        return Pair.of(matcher.group(), highlight.getRight());
                    return null;})
                .filter(Objects::nonNull);
        val stat = new Stat();
        stat.setWhiteWin(results.parallelStream().filter(res -> res.equals("1-0")).count()*coefficient);
        stat.setBlackWin(results.parallelStream().filter(res -> res.equals("0-1")).count()*coefficient);
        stat.setDraw(results.parallelStream().filter(res -> res.equals("1/2-1/2")).count()*coefficient);
        val nextMovesDictionary = new HashMap<String, MoveStat>();
        nextMovesOccurrences.forEach(moveOccurrence -> {
            val move = moveOccurrence.getLeft();
            if (!nextMovesDictionary.containsKey(move))
                nextMovesDictionary.put(move, new MoveStat(){{setMove(move);}});
            val entry = nextMovesDictionary.get(move);
            entry.setPopularity(entry.getPopularity() + coefficient);
            switch (moveOccurrence.getRight()) {
                case "1-0" -> entry.setWhiteWin(entry.getWhiteWin() + 1);
                case "0-1" -> entry.setBlackWin(entry.getBlackWin() + 1);
                default -> entry.setDraw(entry.getDraw() + 1);
            }
        });
        for (var key: nextMovesDictionary.keySet()){
            val move = nextMovesDictionary.get(key);
            val moveCoefficient = 1 / (move.getDraw() + move.getBlackWin() + move.getWhiteWin());
            move.setDraw(move.getDraw() * moveCoefficient);
            move.setWhiteWin(move.getWhiteWin() * moveCoefficient);
            move.setBlackWin(move.getBlackWin() * moveCoefficient);
        }
        val nextMoves = nextMovesDictionary
                .values()
                .stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(move -> -move.getPopularity()))
                .limit(7)
                .toList();
        return new StatsResponse(stat, nextMoves);
    }
}
