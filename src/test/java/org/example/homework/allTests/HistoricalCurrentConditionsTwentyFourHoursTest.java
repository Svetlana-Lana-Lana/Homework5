package org.example.homework.allTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.homework.currentConditons.historical.Historical;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoricalCurrentConditionsTwentyFourHoursTest extends AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(HistoricalCurrentConditionsTwentyFourHoursTest.class);

    @Test
    void getHistoricalCurrentConditionsTwentyFourHoursResponseCode200() throws IOException {
        logger.info("Тест код ответа 200 запущен");

        //given
        ObjectMapper objectMapper = new ObjectMapper();
        Historical historical = new Historical();
        historical.setWeatherText("Light snow");

        logger.debug("Формирование мока для GET /currentconditions/v1/294018/historical/24");
        stubFor(get(urlPathEqualTo("/currentconditions/v1/294018/historical/24"))
                .willReturn(aResponse().withStatus(200)
                        .withBody(objectMapper.writeValueAsString(historical))));

        //when
        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http-клиент создан");

        HttpGet request = new HttpGet(getBaseUrl() + "/currentconditions/v1/294018/historical/24");

        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/currentconditions/v1/294018/historical/24")));
        Assertions.assertEquals(200, response.getStatusLine().getStatusCode());

        Historical resultHistorical = objectMapper.readValue(response.getEntity().getContent(), Historical.class);
        Assertions.assertEquals("Light snow", resultHistorical.getWeatherText());
    }


    @Test
    void getHistoricalCurrentConditionsTwentyFourHoursResponseCode401() throws IOException, URISyntaxException {
        logger.info("Тест код ответа 401 запущен");

        //given
        logger.debug("Формирование мока для GET /currentconditions/v1/294018/historical/24");

        stubFor(get(urlPathEqualTo("/currentconditions/v1/294018/historical/24"))
                .withQueryParam("apiKey", containing("CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK"))
                .willReturn(aResponse()
                        .withStatus(401).withBody("ERROR 401 (Unauthorized)")));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl()+"/currentconditions/v1/294018/historical/24");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK1000")
                .build();
        request.setURI(uri);

        logger.debug("http клиент создан");

        //when
        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/currentconditions/v1/294018/historical/24")));
        Assertions.assertEquals(401, response.getStatusLine().getStatusCode());
        Assertions.assertEquals("ERROR 401 (Unauthorized)", convertResponseToString(response));
    }
}


