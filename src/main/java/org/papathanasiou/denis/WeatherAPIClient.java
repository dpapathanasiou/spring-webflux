package org.papathanasiou.denis;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WeatherAPIClient {
    public Mono<ClientResponse> lookupLatLong(Float latitude, Float longitude) {
        return WebClient.create(String.format("https://api.weather.gov/points/%f,%f", latitude, longitude))
                .get() // TODO: add user-agent string   
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }
}
