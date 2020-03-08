# About

This is a simple experiment, to wrap my mind around how [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/web-reactive.html#spring-webflux) works, loosely based on [this guide](https://spring.io/guides/gs/reactive-rest-service/) and [corresponding repo](https://github.com/spring-guides/gs-reactive-rest-service).

## How's the weather?

Rather than just a simple `"Hello, Spring!"` echo, this project uses the [weather.gov API](https://www.weather.gov/documentation/services-web-api) to respond to [latitude and longitude](https://en.wikipedia.org/wiki/Geographic_coordinate_system#Latitude_and_longitude) pairs with the corresponding forecast.

The [WebClient](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html) is used to translate the `(latitude,longitude)` pair into a grid address from which the actual forecast can be found.

# Prerequisites

* [Gradle](https://gradle.org/) via [SDKMAN!](https://sdkman.io)

```sh
sdk install gradle 6.2.2
```

* [IntelliJ IDEA](https://www.jetbrains.com/idea/)
