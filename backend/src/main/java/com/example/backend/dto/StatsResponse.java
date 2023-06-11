package com.example.backend.dto;

import com.example.backend.model.MoveStat;
import com.example.backend.model.Stat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsResponse {
    private Stat overall;
    private List<MoveStat> moves;
}

