package com.yangfan.chat.xml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.yangfan.buslocator.Api;
import com.yangfan.buslocator.Buses;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonXmlTest {

    @SneakyThrows
    @Test
    void validateJacksonXml() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Buses buses = xmlMapper.readValue(payload, Buses.class);

        assertThat(buses.getBus()).hasSize(1);
        assertThat(buses.getBus().get(0)).extracting("id", "fs").contains("5407", "108 NEWARK");
    }

    
    static String payload = "<?xml version=\"1.0\"?>" +
            "<buses rt=\"108\" rtRtpiFeedName=\"\" rtdd=\"108\"> <!-- @11 @16 -->" +
            "   <time>9:28 PM</time>" +
            "   <bus>" +
            "       <id>5407</id>" +
            "       <consist></consist>" +
            "       <cars></cars>" +
            "       <rtpiFeedName></rtpiFeedName>" +
            "       <m>1</m>" +
            "       <rt>108</rt>" +
            "       <rtRtpiFeedName></rtRtpiFeedName>" +
            "       <rtdd>108</rtdd>  " +
            "       <d>South West Bound</d>" +
            "       <dd>Newark</dd> " +
            "       <dn>WSW</dn> " +
            "       <lat>40.730411529541016</lat>" +
            "       <lon>-74.12667083740234</lon>" +
            "       <pid>1706</pid>" +
            "       <pd>Newark</pd> " +
            "       <pdRtpiFeedName></pdRtpiFeedName>" +
            "       <run>4</run>" +
            "       <fs>108 NEWARK</fs>" +
            "       <op>483250</op>" +
            "       <dip>55249</dip>" +
            "       <bid>112136307</bid>" +
            "       <wid1>0004</wid1>" +
            "       <wid2>0108</wid2>" +
            "   </bus>" +
            "</buses>";
}
