package com.example.openbanking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class OpenbankingService {
    private final static String O_CLIENT_ID = "58df4e1c-f669-4e83-8e9f-367b8f2b2a85";
    private final static String O_CLIENT_SECRET= "c7aa6868-89a7-452d-8fb7-1ce875bac509";
    private final static String O_REDIRECT_URI = "http://localhost:8282/openbanking/apiTest";
    private final static String O_ORGANIZATION_CODE = "T991650480";

    public String getUrl() {
        String openUrl = "https://testapi.openbanking.or.kr/oauth/2.0/authorize?"
                +"response_type=code"
                +"&client_id=" + O_CLIENT_ID
                +"&redirect_uri=" + O_REDIRECT_URI
                +"&scope=login inquiry"
                +"&state=b80BLsfigm9OokPTjy03elbJqRHOfGSY"
                +"&auth_type=0";

        log.info(openUrl);

        return openUrl;
    }

    public JsonNode getAccessToken(String code) throws IOException {
        JsonNode returnNode = null;

        final String requestUrl = "https://testapi.openbanking.or.kr/oauth/2.0/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", O_CLIENT_ID);
        params.add("client_secret", O_CLIENT_SECRET);
        params.add("redirect_uri", O_REDIRECT_URI);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.postForEntity(requestUrl, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        returnNode = mapper.readTree(response.getBody());

        String access_token = returnNode.get("access_token").asText();
        String refresh_token = returnNode.get("refresh_token").asText();
        String user_seq_no = returnNode.get("user_seq_no").asText();

        log.info("access_token : {}, refresh_token : {}, user_seq_no : {}", access_token, refresh_token, user_seq_no);

        getUserInfo(access_token, user_seq_no);

        return returnNode;
    }

    public JsonNode getUserInfo(String access_token, String user_seq_no) throws JsonProcessingException {
        final String userInfoUrl = "https://testapi.openbanking.or.kr/v2.0/user/me?user_seq_no=" + user_seq_no;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+access_token);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode returnNode = null;
        returnNode = mapper.readTree(response.getBody());

        log.info("getUserInfo return.... {}", returnNode);

        String finTechUseNum = returnNode.get("res_list").get(0).get("fintech_use_num").toString();
        log.info("getUserInfo fintechUseNum.... {}", finTechUseNum);

        return returnNode;
    }
}
