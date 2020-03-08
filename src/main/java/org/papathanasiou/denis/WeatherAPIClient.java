package org.papathanasiou.denis;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WeatherAPIClient {
    private static final String baseURL = "https://api.weather.gov/";
    private static final String userAgent = "spring-webflux/0.0.1 +http://github.com/dpapathanasiou/spring-webflux";

    private WebClient client = WebClient.builder()
            .baseUrl(baseURL)
            .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
            .build();

    public Mono<ClientResponse> lookupLatLong(String latitude, String longitude) {
        return client
                .get()
                .uri(String.format("/points/%s,%s", latitude, longitude))
                .accept(MediaType.APPLICATION_JSON)
                .exchange();  // resulting `"properties": "forecast":` contains the URL with the forecast data
    }
}
