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
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
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
        Date firstReqTime = nowWithoutSeconds();
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
        assert res1.getBody().get("user1").equals(7);

        //3.запрос без фильтров
        ResponseEntity<List> res2 = restTemplate.getForEntity("/statistics", List.class);
        assert res2.getStatusCodeValue() == 200;
        assert res2.getBody().size() == 4;
        assert ((Map) res2.getBody().get(0)).size() == 6;
        assert ((Map) res2.getBody().get(0)).get("jobId").equals(1);
        assert ((Map) res2.getBody().get(0)).get("user").equals("user1");
        assert ((Map) res2.getBody().get(0)).get("device").equals("device1");
        assert ((Map) res2.getBody().get(0)).get("type").equals("print");
        assert ((Map) res2.getBody().get(0)).get("amount").equals(10);
        assert ((Map) res2.getBody().get(1)).get("jobId").equals(2);
        assert ((Map) res2.getBody().get(2)).get("jobId").equals(3);
        assert ((Map) res2.getBody().get(3)).get("jobId").equals(4);
        Date time = DATE_FORMAT.parse(((Map) res2.getBody().get(0)).get("time").toString());
        assert time.after(firstReqTime) || time.equals(firstReqTime);
        Date now = nowWithoutSeconds();
        assert time.after(now) || time.equals(now);

        //4. один фильтр
        res2 = restTemplate.getForEntity("/statistics?user=user1", List.class);
        assert res2.getStatusCodeValue() == 200;
        assert res2.getBody().size() == 3;
        assert ((List<Map<String, Object>>) res2.getBody()).stream().map(o -> o.get("user").toString())
                .distinct().collect(Collectors.toList()).equals(singletonList("user1"));

        //5. два фильтра
        res2 = restTemplate.getForEntity("/statistics?device=device1&user=user1", List.class);
        assert res2.getStatusCodeValue() == 200;
        assert res2.getBody().size() == 2;
        assert ((List<Map<String, Object>>) res2.getBody()).stream().map(o -> o.get("user").toString())
                .distinct().collect(Collectors.toList()).equals(singletonList("user1"));
        assert ((List<Map<String, Object>>) res2.getBody()).stream().map(o -> o.get("device").toString())
                .distinct().collect(Collectors.toList()).equals(singletonList("device1"));

        //6. три фильтра
        res2 = restTemplate.getForEntity("/statistics?type=print&user=user1&device=device1", List.class);
        assert res2.getStatusCodeValue() == 200;
        assert res2.getBody().size() == 1;
        assert ((Map) res2.getBody().get(0)).get("type").equals("print");
        assert ((Map) res2.getBody().get(0)).get("device").equals("device1");
        assert ((Map) res2.getBody().get(0)).get("user").equals("user1");

        //7. фильтр по времени
        res2 = restTemplate.getForEntity("/statistics?timeFrom={timeFrom}", List.class, singletonMap("timeFrom", "01.01.1988 01:01"));
        assert res2.getStatusCodeValue() == 200;
        assert res2.getBody().size() == 4;
        res2 = restTemplate.getForEntity("/statistics?timeFrom={timeFrom}", List.class, singletonMap("timeFrom", "01.01.2099 01:01"));
        assert res2.getStatusCodeValue() == 200;
        assert res2.getBody().size() == 0;
        res2 = restTemplate.getForEntity("/statistics?timeTo={timeTo}", List.class, singletonMap("timeTo", "01.01.1988 01:01"));
        assert res2.getStatusCodeValue() == 200;
        assert res2.getBody().size() == 0;
        res2 = restTemplate.getForEntity("/statistics?timeTo={timeTo}", List.class, singletonMap("timeTo", "01.01.2099 01:01"));
        assert res2.getStatusCodeValue() == 200;
        assert res2.getBody().size() == 4;
    }


    @Test
    public void uniqueTest() throws Exception {
        //1. первый запрос
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        ResponseEntity<Map> res1 = restTemplate.exchange("/jobs", POST,
                new HttpEntity<>(content("uniqueTest1.xml"), headers),
                Map.class);
        assert res1.getStatusCodeValue() == 200;

        //2. нарушаем constraint
        res1 = restTemplate.exchange("/jobs", POST,
                new HttpEntity<>(content("uniqueTest2.xml"), headers),
                Map.class);
        assert res1.getStatusCodeValue() == 409;
        assert res1.getBody().get("messages").equals(singletonList("Job with jobId=5 and device=device1 already exists"));

        //3. проверяем что данные из uniqueTest2.xml не записались
        ResponseEntity<List> res2 = restTemplate.getForEntity("/statistics", List.class);
        List<String> ids = ((List<Map<String, Object>>) res2.getBody()).stream().map(o -> o.get("jobId").toString())
                .distinct().collect(Collectors.toList());
        assert !ids.contains("7");
    }

    @Test
    public void badRequestTest() throws Exception {
        //1. пустой запрос
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        ResponseEntity<Map> res1 = restTemplate.exchange("/jobs", POST,
                new HttpEntity<>(content("badRequestEmpty.xml"), headers),
                Map.class);
        assert res1.getStatusCodeValue() == 400;
        assert res1.getBody().get("messages").equals(singletonList("Require at least one job!"));

        //2. нету полей
        res1 = restTemplate.exchange("/jobs", POST,
                new HttpEntity<>(content("badRequestNoFields.xml"), headers),
                Map.class);
        assert res1.getStatusCodeValue() == 400;
        assert new HashSet<>((Collection<String>) res1.getBody().get("messages"))
                .equals(new HashSet<>(asList("Type is required!", "User is required!", "Id is required!")));

        //3. корневой элемент не jobs
        res1 = restTemplate.exchange("/jobs", POST,
                new HttpEntity<>(content("badRequestIncorrectRoot.xml"), headers),
                Map.class);
        assert res1.getStatusCodeValue() == 400;
        assert res1.getBody().get("messages").equals(singletonList("Bad request"));
    }

    //время без секунд
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
