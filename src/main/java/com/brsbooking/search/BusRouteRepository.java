package com.brsbooking.search;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusRouteRepository extends JpaRepository<BusRoute, Integer> {

    List<BusRoute> findAllBySourceAndDestination(String source, String destination);

}