package com.example.backend.service;

import com.example.backend.dto.StatsResponse;
import com.example.backend.model.MoveStat;
import com.example.backend.model.Stat;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class StatisticsService {
    private final TypesenseService typesenseService;

    @Autowired
    public StatisticsService(TypesenseService typesenseService) {
        this.typesenseService = typesenseService;
    }

    public StatsResponse calcStatistics(String q) throws Exception {
        val typesenseResponse = typesenseService.search(q, 1, 250);
        return new StatsResponse(new Stat(0.4, 0.3, 0.3), new ArrayList<>() {{
            add(new MoveStat("Nf3", 0.5, 0.4, 0.2, 0.4));
        }});
    }
}
