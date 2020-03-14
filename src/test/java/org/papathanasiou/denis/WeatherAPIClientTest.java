package org.papathanasiou.denis;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeatherAPIClientTest {
    private static final String LAT  = "40.7688";
    private static final String LONG = "-73.9898";

    private final MockWebServer server = new MockWebServer();

    @BeforeEach
    public void setup() throws IOException {
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch (RecordedRequest request) {
                String path = Objects.requireNonNull(request.getPath());
                if( path.startsWith("/points") ) {
                    return new MockResponse().setResponseCode(200).setBody(loadTestData("points.json"));
                }
                if( path.startsWith("/gridpoints") ) {
                    return new MockResponse().setResponseCode(200).setBody(loadTestData("forecast.json"));
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        server.setDispatcher(dispatcher);
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    /**
     * Read the given file in `src/test/resources` and return its contents as a String.
     */
    String loadTestData(String filename) {
        try {
            return new String(Objects.requireNonNull(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(filename))
                    .readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Test
    public void calling_lookupLatLong_producesValidURL() {
        WeatherAPIClient client = new WeatherAPIClient(WebClient.create(server.url("/").toString()));
        String result = client.lookupLatLong(LAT, LONG).block();
        assertEquals("https://api.weather.gov/gridpoints/OKX/32,37/forecast", result);
    }

    @Test
    public void calling_getForecast_producesHTMLResultTable() {
        WeatherAPIClient client = new WeatherAPIClient(WebClient.create(server.url("/").toString()));
        String result = client.getForecast(LAT, LONG).block();
        assertEquals(loadTestData("forecast.html"), result);
    }
}