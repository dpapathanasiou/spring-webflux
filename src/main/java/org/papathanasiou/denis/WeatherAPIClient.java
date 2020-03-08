package org.papathanasiou.denis;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WeatherAPIClient {
    public Mono<ClientResponse> lookupLatLong(String latitude, String longitude) {
        return WebClient.create(String.format("https://api.weather.gov/points/%s,%s", latitude, longitude))
                .get() // TODO: add user-agent string
                .accept(MediaType.APPLICATION_JSON)
                .exchange();  // resulting `"properties": "forecast":` contains the URL with the forecast data
    }
}
