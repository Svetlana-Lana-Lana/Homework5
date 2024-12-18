package org.example.homework.allTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.homework.indices.fiveDay.FiveDay;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class DailyIndexValuesForAllIndices15DaysTest extends AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(DailyIndexValuesForAllIndices15DaysTest.class);

    @Test
    void getIndexForAllIndicesFifteenDaysResponseCode401() throws IOException {
        logger.info("Тест код ответа 401 запущен");

        //given
        ObjectMapper objectMapper = new ObjectMapper();
        FiveDay fiveDay = new FiveDay();
        fiveDay.setName("Flight Delays");

        logger.debug("Формирование мока для GET /indices/v1/daily/15day/294018");
        stubFor(get(urlPathEqualTo("/indices/v1/daily/15day/294018"))
                .willReturn(aResponse().withStatus(401)
                        .withBody(objectMapper.writeValueAsString(fiveDay))));

        //when
        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http-клиент создан");

        HttpGet request = new HttpGet(getBaseUrl() + "/indices/v1/daily/15day/294018");

        HttpResponse response = httpClient.execute(request);

        //then
        verify(getRequestedFor(urlPathEqualTo("/indices/v1/daily/15day/294018")));
        Assertions.assertEquals(401, response.getStatusLine().getStatusCode());
    }
}
