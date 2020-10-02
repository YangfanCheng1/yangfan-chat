package com.yangfan.chat.xml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.yangfan.buslocator.Buses;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonXmlTest {

    @SneakyThrows
    @Test
    void validateJacksonXml() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Buses buses = xmlMapper.readValue(payload, Buses.class);

        assertThat(buses.getBus()).hasSize(1);
        assertThat(buses.getBus().get(0)).extracting("id", "fs").contains("5229", "159 NEW YORK");
    }
    
    static String payload = "<buses rt=\"159\" rtRtpiFeedName=\"\" rtdd=\"159\">\n" +
            "<!--  @11 @16  -->\n" +
            "<time>11:09 AM</time>" +
            "<bus>" +
            "<id>5229</id>" +
            "<consist/>" +
            "<cars/>" +
            "<rtpiFeedName/>" +
            "<m>1</m>" +
            "<rt>159</rt>" +
            "<rtRtpiFeedName/>" +
            "<rtdd>159</rtdd>" +
            "<d>South West Bound</d>" +
            "<dd>New York</dd>" +
            "<dn>SSW</dn>" +
            "<lat>40.78640653266282</lat>" +
            "<lon>-74.0182119901063</lon>" +
            "<pid>1805</pid>" +
            "<pd>New York</pd>" +
            "<pdRtpiFeedName/>" +
            "<run>MAN</run>" +
            "<fs>159 NEW YORK</fs>" +
            "<op>519910</op>" +
            "<dip>22122</dip>" +
            "<bid>MAN</bid>" +
            "<wid1>0374</wid1>" +
            "<wid2>0159</wid2>" +
            "</bus>" +
            "</buses>";
}
