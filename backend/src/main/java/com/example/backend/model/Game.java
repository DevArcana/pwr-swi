package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"id", "avgElo", "player"})
public class Game {
    private String black;
    private int blackElo;
    private String event;
    private String link;
    private String mainlineMoves;
    private String opening;
    private List<String> positions;
    private String termination;
    private String time;
    private Long timestamp;
    private String result;
    private String white;
    private int whiteElo;
}
