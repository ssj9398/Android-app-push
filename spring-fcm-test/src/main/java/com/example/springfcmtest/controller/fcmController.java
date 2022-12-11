package com.example.springfcmtest.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.util.Arrays;

@Slf4j
@RestController
@RequiredArgsConstructor
public class fcmController {

    @GetMapping("test")
    public void test(){
        System.out.println("Aaa");
    }

    @GetMapping("/fcmTest")
    public void fcmTest() throws Exception {

        try {
            String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
            String[] SCOPES = { MESSAGING_SCOPE };
            GoogleCredential googleCredential = GoogleCredential
                    .fromStream(new FileInputStream("src/main/resources/my-application-15d99-firebase-adminsdk-ycsvs-45de0fa5fe.json"))
                    .createScoped(Arrays.asList(SCOPES));
            googleCredential.refreshToken();

            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type" , MediaType.APPLICATION_JSON_VALUE);
            headers.add("Authorization", "Bearer " + googleCredential.getAccessToken());

            JSONObject notification = new JSONObject();
            notification.put("body", "내용 테스트");
            notification.put("title", "제목 테스트");

            JSONObject message = new JSONObject();
            message.put("token", "eqM-7-G4QC-I5vQhmOQ4e7:APA91bFL-nljKcBFfDzpTPoLuPrBLRIT5iZZCY8EedHQoUJCnyaqEW3xFPx4AzlwF5gu7OXVIMsP9saTaVXGbuHnp_L0KVb-IqSxJo8i_7XvlWkKp5f3HMYi9PVFrbYVCccVraOPaQfj");
            message.put("notification", notification);

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("message", message);

            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(jsonParams, headers);
            RestTemplate rt = new RestTemplate();

            ResponseEntity<String> res = rt.exchange("https://fcm.googleapis.com/v1/projects/my-application-15d99/messages:send"
                    , HttpMethod.POST
                    , httpEntity
                    , String.class);

            if (res.getStatusCode() != HttpStatus.OK) {
                log.debug("FCM-Exception");
                log.debug(res.getStatusCode().toString());
                log.debug(res.getHeaders().toString());
                log.debug(res.getBody());

            } else {
                log.debug(res.getStatusCode().toString());
                log.debug(res.getHeaders().toString());
                log.debug(res.getBody().toLowerCase());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}