package com.cti;

import org.assertj.core.util.Files;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PrintControllerTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void baseTest() throws ParseException {
        //1. первый POST
        Date before = nowWithoutSeconds();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        ResponseEntity<Map> res1 = restTemplate.exchange("/jobs", POST,
                new HttpEntity<>(content("baseTest1.xml"), headers),
                Map.class);
        assert res1.getStatusCodeValue() == 200;
        assert res1.getBody().size() == 2;
        assert res1.getBody().get("user1").equals(22);
        assert res1.getBody().get("user2").equals(5);

        //2. второй POST
        res1 = restTemplate.exchange("/jobs", POST,
                new HttpEntity<>(content("baseTest2.xml"), headers),
                Map.class);
        assert res1.getStatusCodeValue() == 200;
        assert res1.getBody().size() == 1;
        assert res1.getBody().get("user3").equals(7);

        //3. без фильтров
        ResponseEntity<List> res2 = restTemplate.getForEntity("/statistics", List.class);
        assert res2.getStatusCodeValue() == 200;
        assert res2.getBody().size() == 4;
        assert ((Map) res2.getBody().get(0)).get("jobId").equals(1);
        assert ((Map) res2.getBody().get(0)).get("user").equals("user1");
        assert ((Map) res2.getBody().get(0)).get("device").equals("device1");
        assert ((Map) res2.getBody().get(0)).get("type").equals("print");
        assert ((Map) res2.getBody().get(0)).get("amount").equals(10);
        Date time = DATE_FORMAT.parse(((Map) res2.getBody().get(0)).get("time").toString());
        assert time.after(before) || time.equals(before);
        Date now = nowWithoutSeconds();
        assert time.after(now) || time.equals(now);

    }

    //удаляем секунды
    private static Date nowWithoutSeconds() throws ParseException {
        return DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));
    }


    private static String content(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        try {
            return Files.contentOf(resource.getFile(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
