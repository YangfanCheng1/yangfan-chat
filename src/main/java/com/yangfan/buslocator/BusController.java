package com.yangfan.buslocator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BusController {

    @GetMapping("/bus-locator")
    public String greetingForm(Model model) {
        // http://mybusnow.njtransit.com/bustime/map/getBusesForRoute.jsp?route=159c
        return "bus-locator";
    }
}
