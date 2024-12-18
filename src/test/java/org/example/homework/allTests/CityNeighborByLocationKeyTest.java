package org.example.homework.allTests;

import org.example.homework.locations.locationKey.LocationKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CityNeighborByLocationKeyTest extends AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(CityNeighborByLocationKeyTest.class);

    @Test
    void getCityNeighborByLocationKeyResponseCode200() throws IOException {
        logger.info("Тест код ответа 200 запущен");

        //then
        ObjectMapper objectMapper = new ObjectMapper();
        LocationKey locationKey = new LocationKey();
        locationKey.setLocalizedName("Samara");
        locationKey.setKey("576286");

        logger.debug("Формирование мока для GET /locations/v1/cities/neighbors/290396");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/neighbors/290396"))
                .willReturn(aResponse().withStatus(200)
                        .withBody(objectMapper.writeValueAsString(locationKey))));

        //when
        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http-клиент создан");

        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/cities/neighbors/290396");

        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/neighbors/290396")));
        Assertions.assertEquals(200, response.getStatusLine().getStatusCode());

        LocationKey resultLocationKey = objectMapper.readValue(response.getEntity().getContent(), LocationKey.class);
        Assertions.assertEquals("Samara", resultLocationKey.getLocalizedName());
        Assertions.assertEquals("576286", resultLocationKey.getKey());
    }


    @Test
    void getCityNeighborByLocationKeyResponseCode401() throws IOException, URISyntaxException {
        logger.info("Тест код ответа 401 запущен");

        //given
        logger.debug("Формирование мока для GET /locations/v1/cities/neighbors/290396");

        stubFor(get(urlPathEqualTo("/locations/v1/cities/neighbors/290396"))
                .withQueryParam("apiKey", containing("CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK"))
                .willReturn(aResponse().withStatus(401).withBody("ERROR 401 (Unauthorized)")));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl()+"/locations/v1/cities/neighbors/290396");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK1000")
                .build();
        request.setURI(uri);

        logger.debug("http клиент создан");

        //when
        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/neighbors/290396")));
        Assertions.assertEquals(401, response.getStatusLine().getStatusCode());
        Assertions.assertEquals("ERROR 401 (Unauthorized)", convertResponseToString(response));
    }
}
