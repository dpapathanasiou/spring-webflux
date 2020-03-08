package org.papathanasiou.denis;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class WeatherHandler {
    private WeatherAPIClient client = new WeatherAPIClient();

    public Mono<ServerResponse> getForecast(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                //.body(BodyInserters.fromValue(client.lookupLatLong(request.pathVariable("lat"), request.pathVariable("long")).bodyToMono(String.class)));
                .body(BodyInserters.fromValue(String.format("Hello, lat %s, long %s!",
                       request.pathVariable("lat"),
                       request.pathVariable("long"))));
    }
}
