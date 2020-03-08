package org.papathanasiou.denis;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class WeatherAPIClient {
    private static final String baseURL = "https://api.weather.gov/";
    private static final String userAgent = "spring-webflux/0.0.1 +http://github.com/dpapathanasiou/spring-webflux";
    private static final String EMPTY = "";

    private WebClient client;

    public WeatherAPIClient() {
        this.client = WebClient.builder()
                .baseUrl(baseURL)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .build();
    }

    Function<String, String> parseForecastURL = json -> {
        // within the json returned by the call to `/points/lat,long`,
        // `"properties": "forecast":` contains the URL with the forecast data
        JSONObject obj = new JSONObject(json);
        if( obj.has("properties") ) {
            JSONObject properties = obj.getJSONObject("properties");
            if( properties.has("forecast") ) {
                return properties.getString("forecast");
            }
        }
        return EMPTY;
    };

    /**
     * Use the `latitude` and `longitude` values to fetch the corresponding forecast URL as a string.
     */
    Mono<String> lookupLatLong(String latitude, String longitude) {
        return client
                .get()
                .uri(String.format("/points/%s,%s", latitude, longitude))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> parseForecastURL.apply(json));
    }

    Function<String, String> parseForecastResponse = json -> {
        // within the json returned by the call to `/forecast`,
        // `"properties": "periods":` contains a list of forecasts,
        // starting with `"name": "Today"` as the current one we want
        JSONObject obj = new JSONObject(json);
        if( obj.has("properties") ) {
            JSONObject properties = obj.getJSONObject("properties");
            if( properties.has("periods") ) {
                return StreamSupport.stream(properties.getJSONArray("periods").spliterator(), false)
                        .map(JSONObject.class::cast)
                        .filter(x -> x.has("name"))
                        .filter(x -> x.getString("name").equals("Today"))
                        .filter(x -> x.has("detailedForecast"))
                        .findFirst()
                        .map(x -> x.getString("detailedForecast"))
                        .orElse(EMPTY);
            }
        }
        return EMPTY;
    };

    Function<String, String> parseForecastURLPath = url -> {
        // split the string `https://api.weather.gov/gridpoints/OKX/32,37/forecast`
        // and remove the base url, returning just the relevant path
        try {
            return new URL(url).toURI().getPath();
        } catch (MalformedURLException | URISyntaxException e) {
            System.err.println(e.getMessage());
        }
        return EMPTY;
    };

    Mono<String> lookupForecast(String path) {
        return client
                .get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> parseForecastResponse.apply(json));
    }

    public Mono<String> getForecast(String latitude, String longitude) {
        return lookupLatLong(latitude, longitude)
                .map(forecastURL -> parseForecastURLPath.apply(forecastURL))
                .filter(forecastPath -> ! forecastPath.equals(EMPTY))
                .flatMap(this::lookupForecast);
    }
}
