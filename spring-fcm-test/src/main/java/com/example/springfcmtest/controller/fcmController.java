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
    @Value("${fcm.key.path}")
    private String path;

    @GetMapping("test")
    public void test(){
        System.out.println("Aaa");
    }

    @GetMapping("/fcmTest")
    public void fcmTest() throws Exception {

        try {
            String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
            String[] SCOPES = { MESSAGING_SCOPE };
            System.out.println(path);
            GoogleCredential googleCredential = GoogleCredential
                    .fromStream(new FileInputStream("src/main/resources/fcm.json"))
                    .createScoped(Arrays.asList(SCOPES));
            googleCredential.refreshToken();

            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type" , MediaType.APPLICATION_JSON_VALUE);
            headers.add("Authorization", "Bearer " + googleCredential.getAccessToken());

            JSONObject notification = new JSONObject();
            notification.put("body", "TEST");
            notification.put("title", "TEST");

            JSONObject message = new JSONObject();
            message.put("token", "fa_qIyte8d4:APA91bHOGnZulT059PyK3z_sb1dIkDXTiZUIuRksmS7TdK6XgXAS5kopeGIwUfyhad3X3iXMNknCUOZaF6_mgoj1ohG10CanRyJ_EW1d3xN2E-1DPiLdbMK4pdOgdhB1ztZClqB-25rC");
            message.put("notification", notification);

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("message", message);

            HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(jsonParams, headers);
            RestTemplate rt = new RestTemplate();

            ResponseEntity<String> res = rt.exchange("https://fcm.googleapis.com/v1/projects/arton-316d0/messages:send"
                    , HttpMethod.POST
                    , httpEntity
                    , String.class);

            if (res.getStatusCode() != HttpStatus.OK) {
                log.debug("FCM-Exception");
                log.debug(res.getStatusCode().toString());
                log.debug(res.getHeaders().toString());
                log.debug(res.getBody().toString());

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