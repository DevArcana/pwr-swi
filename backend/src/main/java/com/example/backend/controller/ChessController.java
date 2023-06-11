package com.example.backend.controller;

import com.example.backend.dto.SearchResponse;
import com.example.backend.dto.StatsResponse;
import com.example.backend.service.StatisticsService;
import com.example.backend.service.TypesenseService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChessController {
    private final TypesenseService typesenseService;
    private final StatisticsService statisticsService;

    @Autowired
    public ChessController(TypesenseService typesenseService,
                           StatisticsService statisticsService) {
        this.typesenseService = typesenseService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/games")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable content - malformed query", content = @Content(schema = @Schema()))
    })
    public SearchResponse searchForGames(@RequestParam("q") String query, @RequestParam(defaultValue = "1") int page) throws Exception {
        return new SearchResponse(typesenseService.search(query, page));
    }

    @GetMapping("/openings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    public List<String> searchForOpenings(@RequestParam("q") String query) throws Exception {
        return typesenseService.searchOpenings(query);
    }

    @GetMapping("/stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable content - malformed query", content = @Content(schema = @Schema()))
    })
    public StatsResponse getStats(@RequestParam("q") String query) throws Exception {
        return statisticsService.calcStatistics(query);
    }
}
