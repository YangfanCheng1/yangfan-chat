package com.yangfan.buslocator;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusClient busClient;

    List<Api.BusDto> find(String route, String location) {
        val buses = Optional.ofNullable(busClient.getBusesByRoute(route).getBus()).orElse(Collections.emptyList());
        return buses
                .stream()
                .map(this::convert)
                .map(busDto -> busDto.withDistance(computeDistance(new BigDecimal(location), busDto.getLat())))
                .collect(Collectors.toList());
    }

    private Api.BusDto convert(Buses.Bus bus) {
        return new Api.BusDto(bus.getId(), BigDecimal.ZERO, new BigDecimal(bus.getLat()), new BigDecimal(bus.getLon()));
    }

    /**
     * @param lat1 latitude1
     * @param lat2 latitude2
     * @return formula: 69 * abs(lat1 - lat2)
     */
    private BigDecimal computeDistance(BigDecimal lat1, BigDecimal lat2) {
        return BigDecimal.valueOf(69).multiply(lat1.subtract(lat2).abs());
    }
}
