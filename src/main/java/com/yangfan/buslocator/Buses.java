package com.yangfan.buslocator;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Value;

import java.util.List;

@Value
public class Buses {

    @JacksonXmlElementWrapper(useWrapping = false)
    List<Bus> bus;

    @Value
    public static class Bus {
        String id;
        String fs;
        String rt;
        String dd;
        String lat;
        String lon;
    }
}
