package org.example.homework.allTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.homework.locations.autocomplete.AutocompleteSearch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AutocompleteSearchTest extends AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(AutocompleteSearchTest.class);


    @Test
    void getAutocompleteSearchResponseCode200() throws IOException {
        logger.info("Тест код ответа 200 запущен");

        //given
        ObjectMapper objectMapper = new ObjectMapper();
        AutocompleteSearch autocompleteSearch = new AutocompleteSearch();
        autocompleteSearch.setLocalizedName("Zelenograd");
        autocompleteSearch.setKey("294018");
        autocompleteSearch.setType("City");

        logger.debug("Формирование мока для GET /locations/v1/cities/autocomplete");
        stubFor(WireMock.get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .willReturn(aResponse().withStatus(200)
                        .withBody(objectMapper.writeValueAsString(autocompleteSearch))));

        //when
        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http-клиент создан");

        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/cities/autocomplete");

        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete")));
        Assertions.assertEquals(200, response.getStatusLine().getStatusCode());

        AutocompleteSearch resultAutocompleteSearch = objectMapper.readValue(response.getEntity().getContent(), AutocompleteSearch.class);
        Assertions.assertEquals("Zelenograd", resultAutocompleteSearch.getLocalizedName());
        Assertions.assertEquals("294018", resultAutocompleteSearch.getKey());
        Assertions.assertEquals("City", resultAutocompleteSearch.getType());
    }


    @Test
    void getAutocompleteSearchResponseCode401() throws IOException, URISyntaxException {
        logger.info("Тест код ответа 401 запущен");

        //given
        logger.debug("Формирование мока для GET /locations/v1/cities/autocomplete");

        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("apiKey", containing("CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK"))
                .willReturn(aResponse().withStatus(401).withBody("ERROR 401 (Unauthorized)")));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl()+"/locations/v1/cities/autocomplete");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK1000")
                .build();
        request.setURI(uri);

        logger.debug("http клиент создан");

        //when
        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete")));
        Assertions.assertEquals(401, response.getStatusLine().getStatusCode());
        Assertions.assertEquals("ERROR 401 (Unauthorized)", convertResponseToString(response));
    }
}
