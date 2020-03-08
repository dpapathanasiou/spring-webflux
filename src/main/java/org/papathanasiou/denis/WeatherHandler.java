package org.papathanasiou.denis;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class WeatherHandler {
    private WeatherAPIClient client;

    public WeatherHandler() {
        this.client = new WeatherAPIClient();
    }

    public Mono<ServerResponse> getForecast(ServerRequest request) {
        Mono<String> forecast = client.getForecast(request.pathVariable("lat"), request.pathVariable("long"));
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(forecast, String.class);
    }
}
