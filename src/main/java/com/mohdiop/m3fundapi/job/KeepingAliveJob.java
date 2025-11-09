package com.mohdiop.m3fundapi.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KeepingAliveJob {

    @Value("${keep.alive}")
    private String keepAliveBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(cron = "0 */5 * * * *")
    public void keepAlive() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                keepAliveBaseUrl+"/keep-alive",
                String.class
        );
        System.out.println(response.getBody());
    }
}