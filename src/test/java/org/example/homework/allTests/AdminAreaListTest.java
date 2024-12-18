package org.example.homework.allTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.homework.locations.regionCountryAdminAreaLists.AdminArea;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AdminAreaListTest extends AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(AdminAreaListTest.class);


    @Test
    void getAdminAreaListResponseCode200() throws IOException {
        logger.info("Тест код ответа 200 запущен");

        //given
        ObjectMapper objectMapper = new ObjectMapper();
        AdminArea adminArea = new AdminArea();
        adminArea.setLocalizedName("Andrijevica");
        adminArea.setCountryID("ME");

        logger.debug("Формирование мока для GET /locations/v1/adminareas/ME");
        stubFor(WireMock.get(urlPathEqualTo("/locations/v1/adminareas/ME"))
                .willReturn(aResponse().withStatus(200)
                        .withBody(objectMapper.writeValueAsString(adminArea))));

        //when
        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http-клиент создан");

        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/adminareas/ME");

        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/adminareas/ME")));
        Assertions.assertEquals(200, response.getStatusLine().getStatusCode());

        AdminArea resultAdminArea = objectMapper.readValue(response.getEntity().getContent(), AdminArea.class);
        Assertions.assertEquals("Andrijevica", resultAdminArea.getLocalizedName());
        Assertions.assertEquals("ME", resultAdminArea.getCountryID());
    }

    @Test
    void getAdminAreaListResponseCode401() throws IOException, URISyntaxException {
        logger.info("Тест код ответа 401 запущен");

        //given
        logger.debug("Формирование мока для GET /locations/v1/adminareas/ME");

        stubFor(get(urlPathEqualTo("/locations/v1/adminareas/ME"))
                .withQueryParam("apiKey", containing("CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK"))
                .willReturn(aResponse().withStatus(401).withBody("ERROR 401 (Unauthorized)")));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl()+"/locations/v1/adminareas/ME");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK1000")
                .build();
        request.setURI(uri);

        logger.debug("http клиент создан");

        //when
        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/adminareas/ME")));
        Assertions.assertEquals(401, response.getStatusLine().getStatusCode());
        Assertions.assertEquals("ERROR 401 (Unauthorized)", convertResponseToString(response));
    }
}




