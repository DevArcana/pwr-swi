package com.example.backend.service;

import com.example.backend.dto.StatsResponse;
import com.example.backend.model.MoveStat;
import com.example.backend.model.Stat;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.typesense.model.SearchResultHit;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class StatisticsService {
    private final TypesenseService typesenseService;

    @Autowired
    public StatisticsService(TypesenseService typesenseService) {
        this.typesenseService = typesenseService;
    }

    public StatsResponse calcStatistics(String q) throws Exception {
        val regex = Pattern.compile("[A-Za-z]+\\S+");
        List<SearchResultHit> typesenseHits = new ArrayList<>();
        for(int i = 1; i <= 10 ; i++) {
            val page = typesenseService.searchStatistics(q, i, 250);
            typesenseHits.addAll(page);
            if (page.size() < 250)
                break;
        }

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
                        .map(hit -> Triple.of(hit.getHighlights().stream(), hit.getDocument(),(String) hit.getDocument().get("result")))
                        .flatMap(triple -> triple.getLeft().map(highlight -> Triple.of(highlight, triple.getMiddle(), triple.getRight())));
        val nextMovesOccurrences = highlits
                .flatMap(highlight -> {
                    if (highlight.getLeft().getField().equals("mainlineMoves")) {
                        val snippet = highlight.getLeft().getSnippet();
                        val snippetPostMark = snippet.substring(snippet.lastIndexOf("</mark>") + 7).trim();
                        val matcher = regex.matcher(snippetPostMark);
                        if (matcher.find())
                            return Stream.of(Pair.of(matcher.group(), highlight.getRight()));
                        return null;
                    } else if (highlight.getLeft().getField().equals("positions")) {
                        val snippets = highlight
                                .getLeft()
                                .getSnippets()
                                .stream()
                                .filter(snippet -> snippet.split(" ")[0].contains("<mark>"))
                                .map(snippet ->
                                        snippet
                                                .replaceAll("</mark>", "")
                                                .replaceAll("<mark>", ""));
                        return snippets.map(snippet -> {
                            val temp = snippet.split(" ");
                            val color = temp[1];
                            val fullMoveNumber = Integer.parseInt(temp[temp.length - 1]);
                            val movesStream = Arrays.stream(((String) highlight.getMiddle().get("mainlineMoves")).split(" "));
                            val next = movesStream.skip(fullMoveNumber * 3L + (color.equals("w") ? -2 : -1)).findFirst().orElse(null);
                            if (next == null) return null;
                            return Pair.of(next, highlight.getRight());
                        });
                    }
                    else
                        return null;
                })
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
