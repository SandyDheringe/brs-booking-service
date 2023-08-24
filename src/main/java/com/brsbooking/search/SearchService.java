package com.brsbooking.search;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {


    private final RouteRepository routeRepository;

    SearchService(RouteRepository routeRepository){
        this.routeRepository = routeRepository;
    }
    public List<Route> searchBus(String source, String destination) {
        return routeRepository.findAllBySourceAndDestination(source,destination);
    }
}
