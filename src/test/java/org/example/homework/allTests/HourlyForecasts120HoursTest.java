package org.example.homework.allTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.homework.forecast.hourly.Forecast;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.apache.http.impl.client.HttpClients.createDefault;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HourlyForecasts120HoursTest extends AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(HourlyForecasts120HoursTest.class);

    @Test
    void getHourlyForecastsOneHundredTwentyHoursResponseCode401() throws IOException {
        logger.info("Тест код ответа 401 запущен");

        //given
        ObjectMapper objectMapper = new ObjectMapper();
        Forecast forecast = new Forecast();
        forecast.setIconPhrase("Intermittent clouds");

        logger.debug("Формирование мока для GET /forecasts/v1/hourly/120hour/294018");
        stubFor(get(urlPathEqualTo("/forecasts/v1/hourly/120hour/294018"))
                .willReturn(aResponse().withStatus(401)
                        .withBody(objectMapper.writeValueAsString(forecast))));

        //when
        CloseableHttpClient httpClient = createDefault();
        logger.debug("http-клиент создан");

        HttpGet request = new HttpGet(getBaseUrl() + "/forecasts/v1/hourly/120hour/294018");

        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/forecasts/v1/hourly/120hour/294018")));
        Assertions.assertEquals(401, response.getStatusLine().getStatusCode());
    }
}
