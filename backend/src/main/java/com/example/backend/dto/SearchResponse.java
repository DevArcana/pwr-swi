package com.example.backend.dto;

import com.example.backend.model.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private int count;
    private int page;
    private int totalPages;
    private List<Game> hits;

    public SearchResponse(TypesenseResponse typesenseResponse) {
        count = typesenseResponse.getCount();
        page = typesenseResponse.getPage();
        totalPages = typesenseResponse.getTotalPages();
        hits = typesenseResponse.getHits();
    }
}
