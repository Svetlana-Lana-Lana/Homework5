package org.example.homework.allTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.homework.currentConditons.currentCondition.CurrentCondition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class CurrentConditionTest extends  AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(CurrentConditionTest.class);

    @Test
    void getCurrentConditionResponseCode200() throws IOException {
        logger.info("Тест код ответа 200 запущен");

        //given
        ObjectMapper objectMapper = new ObjectMapper();
        CurrentCondition currentCondition = new CurrentCondition();
        currentCondition.setWeatherText("Light snow");
        currentCondition.setLink("http://www.accuweather.com/en/ru/zelenograd/294018/current-weather/294018?lang=en-us");

        logger.debug("Формирование мока для GET /currentconditions/v1/294018");
        stubFor(get(urlPathEqualTo("/currentconditions/v1/294018"))
                .willReturn(aResponse().withStatus(200)
                        .withBody(objectMapper.writeValueAsString(currentCondition))));

        //when
        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http-клиент создан");

        HttpGet request = new HttpGet(getBaseUrl() + "/currentconditions/v1/294018");

        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/currentconditions/v1/294018")));
        Assertions.assertEquals(200, response.getStatusLine().getStatusCode());

        CurrentCondition resultCurrentCondition = objectMapper.readValue(response.getEntity().getContent(), CurrentCondition.class);
        Assertions.assertEquals("Light snow", resultCurrentCondition.getWeatherText());
        Assertions.assertEquals("http://www.accuweather.com/en/ru/zelenograd/294018/current-weather/294018?lang=en-us", resultCurrentCondition.getLink());
    }


    @Test
    void getCurrentConditionResponseCode401() throws IOException, URISyntaxException {
        logger.info("Тест код ответа 401 запущен");

        //given
        logger.debug("Формирование мока для GET /currentconditions/v1/294018");

        stubFor(get(urlPathEqualTo("/currentconditions/v1/294018"))
                .withQueryParam("apiKey", containing("CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK"))
                .willReturn(aResponse().withStatus(401).withBody("ERROR 401 (Unauthorized)")));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl()+"/currentconditions/v1/294018");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK1000")
                .build();
        request.setURI(uri);

        logger.debug("http клиент создан");

        //when
        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/currentconditions/v1/294018")));
        Assertions.assertEquals(401, response.getStatusLine().getStatusCode());
        Assertions.assertEquals("ERROR 401 (Unauthorized)", convertResponseToString(response));
    }
}
