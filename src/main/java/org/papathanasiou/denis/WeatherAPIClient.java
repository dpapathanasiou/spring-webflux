package org.papathanasiou.denis;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WeatherAPIClient {
    private WebClient client = WebClient.create("https://api.weather.gov/");

    public Mono<ClientResponse> lookupLatLong(Float latitude, Float longitude) {
        return client.get()
                .uri(String.format("points/%f,%f", latitude, longitude))
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }
}
