package org.example.homework.allTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.homework.indices.fiveDay.FiveDay;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class DailyIndexValuesForAGroupOfIndices5DaysTest extends AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(DailyIndexValuesForAGroupOfIndices5DaysTest.class);


    @Test
    void getIndexForAGroupFiveDaysResponseCode200() throws IOException {
        logger.info("Тест код ответа 200 запущен");

        //given
        ObjectMapper objectMapper = new ObjectMapper();
        FiveDay fiveDay = new FiveDay();
        fiveDay.setName("Hunting Forecast");

        logger.debug("Формирование мока для GET /indices/v1/daily/5day/52/groups/8");
        stubFor(get(urlPathEqualTo("/indices/v1/daily/5day/52/groups/8"))
                .willReturn(aResponse().withStatus(200)
                        .withBody(objectMapper.writeValueAsString(fiveDay))));

        //when
        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http-клиент создан");

        HttpGet request = new HttpGet(getBaseUrl() + "/indices/v1/daily/5day/52/groups/8");

        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/indices/v1/daily/5day/52/groups/8")));
        Assertions.assertEquals(200, response.getStatusLine().getStatusCode());

        FiveDay resultFiveDay = objectMapper.readValue(response.getEntity().getContent(), FiveDay.class);
        Assertions.assertEquals("Hunting Forecast", resultFiveDay.getName());
    }

    @Test
    void getIndexForAGroupFiveDaysResponseCode401() throws IOException, URISyntaxException {
        logger.info("Тест код ответа 401 запущен");

        //given
        logger.debug("Формируем мок GET /indices/v1/daily/5day/52/groups/8");

        stubFor(get(urlPathEqualTo("/indices/v1/daily/5day/52/groups/8"))
                .withQueryParam("apiKey", containing("CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK"))
                .willReturn(aResponse()
                        .withStatus(401).withBody("ERROR 401 (Unauthorized)")));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl()+"/indices/v1/daily/5day/52/groups/8");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "CSGPpnAFXrAGJDzTE5uGqOa1KtBZzrAK1000")
                .build();
        request.setURI(uri);

        logger.debug("http клиент создан");

        //when
        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/indices/v1/daily/5day/52/groups/8")));
        Assertions.assertEquals(401, response.getStatusLine().getStatusCode());
        Assertions.assertEquals("ERROR 401 (Unauthorized)", convertResponseToString(response));
    }
}
