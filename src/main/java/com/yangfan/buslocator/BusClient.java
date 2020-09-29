package com.yangfan.buslocator;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "BusClient", url="http://mybusnow.njtransit.com", configuration = BusClient.Config.class)
public interface BusClient {

    // calls http://mybusnow.njtransit.com/bustime/map/getBusesForRoute.jsp?route=159
    @GetMapping(value = "/bustime/map/getBusesForRoute.jsp", produces = MediaType.TEXT_HTML_VALUE)
    Buses getBusesByRoute(@RequestParam String route);

    class Config {}
}
