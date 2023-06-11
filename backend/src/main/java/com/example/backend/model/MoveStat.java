package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveStat {
    private String move;
    private double popularity;
    private double whiteWin;
    private double blackWin;
    private double draw;
}
