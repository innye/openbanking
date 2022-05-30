package com.example.openbanking.controller;

import com.example.openbanking.service.OpenbankingService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.MalformedURLException;

@Controller
@Slf4j
public class OpenbankingTestController {

    @Autowired
    OpenbankingService open;

    @GetMapping("/apiTest")
    String testApi(){
        return "redirect:"+open.getUrl();
    }

    @RequestMapping("/openbanking/apiTest")
    String apiTest(@RequestParam("code") String code) throws IOException {
    JsonNode node = open.getAccessToken(code);

        return "apitest";

    }
}
