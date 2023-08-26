package com.brsbooking.search;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {


    private final BusRouteRepository busRouteRepository;

    SearchService(BusRouteRepository busRouteRepository){
        this.busRouteRepository = busRouteRepository;
    }
    public List<BusRoute> searchBus(String source, String destination) {
        return busRouteRepository.findAllBySourceAndDestination(source,destination);
    }
}
