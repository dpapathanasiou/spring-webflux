package org.papathanasiou.denis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WeatherRouter {
    @Bean
    public RouterFunction<ServerResponse> route(WeatherHandler weatherHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/weather/{lat}/{long}"), weatherHandler::getForecast);
    }
}
