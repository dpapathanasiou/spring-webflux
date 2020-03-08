package org.papathanasiou.denis;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WeatherAPIClient {
    private static final String baseURL = "https://api.weather.gov/";
    private static final String userAgent = "spring-webflux/0.0.1 +http://github.com/dpapathanasiou/spring-webflux";

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
        return "";
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

    Function<String, List<JSONObject>> parseForecastResponse = json -> {
        // within the json returned by the call to `/forecast`,
        // `"properties": "periods":` contains a list of forecasts,
        // which are the collection of objects we want.
        JSONObject obj = new JSONObject(json);
        if( obj.has("properties") ) {
            JSONObject properties = obj.getJSONObject("properties");
            if( properties.has("periods") ) {
                return StreamSupport.stream(properties.getJSONArray("periods").spliterator(), false)
                        .map(JSONObject.class::cast)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    };

    Function<List<JSONObject>, String> generateResultsTable = periods ->
            // Format the list of time period objects into a single
            // html table with headers, or a `not found` message.
            periods.isEmpty() ? "Forecast information is unavailable"
                    : String.join("", Arrays.asList(
                    "<table><tr><th>Time Period</th><th>Forecast</th></tr>",
                    periods.stream()
                            .map(x -> String.format("<tr><td>%s</td><td>%s</td></tr>",
                                    x.getString("name"),
                                    x.getString("detailedForecast")))
                            .collect(Collectors.joining()),
                    "</table>"));

    /**
     * Use the string path returned and parsed from the call to `/points/lat,long`
     * to fetch the actual forecast information.
     */
    Mono<String> lookupForecast(String path) {
        return client
                .get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> parseForecastResponse.apply(json))
                .map(generateResultsTable);
    }

    Function<String, String> parseForecastURLPath = url -> {
        // split the string `https://api.weather.gov/gridpoints/OKX/32,37/forecast`
        // and remove the base url, returning just the relevant path
        try {
            return new URL(url).toURI().getPath();
        } catch (MalformedURLException | URISyntaxException e) {
            System.err.println(e.getMessage());
        }
        return "";
    };

    /**
     * Take the `latitude` and `longitude` of the external request,
     * and make two client calls:
     * 1 - to `/points/lat,long` which provides the forecast url
     * 2 - to the resulting forecast url, which contains the actual data
     * And finally parses the result of the second to the desired value.
     */
    public Mono<String> getForecast(String latitude, String longitude) {
        return lookupLatLong(latitude, longitude)
                .map(forecastURL -> parseForecastURLPath.apply(forecastURL))
                .filter(forecastPath -> ! forecastPath.isEmpty())
                .flatMap(this::lookupForecast);
    }
}
