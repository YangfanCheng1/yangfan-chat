package com.yangfan.buslocator;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/bus")
@RequiredArgsConstructor
public class Api {

    private final BusService busService;

    @GetMapping(value = "/{route}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BusDto> getBuses(@PathVariable String route, @RequestParam String location) {
        return busService.find(route, location);
    }

    @Value
    @With
    public static class BusDto {
        String id;
        BigDecimal distance;
        BigDecimal lat;
        BigDecimal lng;
    }
}
