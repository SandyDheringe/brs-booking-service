package com.brsbooking.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SearchController {


    private final SearchService searchService;

    @Autowired
    SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/bus/search")
    ResponseEntity<List<Route>> searchBus(
            @RequestParam String source,
            @RequestParam String destination
    )
    {
        List<Route> routes = searchService.searchBus(source, destination);
        return ResponseEntity.status(HttpStatus.CREATED).body(routes);
    }
}
