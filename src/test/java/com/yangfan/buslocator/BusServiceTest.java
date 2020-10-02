package com.yangfan.buslocator;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    BusClient busClient;

    @InjectMocks
    BusService busService;

    @Test
    void find() {
        given(busClient.getBusesByRoute("1")).willReturn(new Buses(Arrays.asList(
                        new Buses.Bus("233", "SOME DESCRIPTION", "1", "Destination",  "1", "1")
                )));

        val buses = busService.find("1", "1");

        assertThat(buses).hasSize(1);
        assertThat(buses.get(0).getId()).isEqualTo("233");
        assertThat(buses.get(0).getLat()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(buses.get(0).getLng()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(buses.get(0).getDistance()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}