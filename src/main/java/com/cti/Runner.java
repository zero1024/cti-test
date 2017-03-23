package com.cti;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.TimeZone;


@SpringBootApplication(scanBasePackages = "com.cti")
public class Runner {

    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
    }

    //таймозона по умолчанию jackson не совпадает с таймозоной по умолчанию виртуальной машины
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter res = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getDefault());
        res.setObjectMapper(objectMapper);
        return res;
    }

}
