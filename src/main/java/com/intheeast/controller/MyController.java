package com.intheeast.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
    	Map<String, String> response = new HashMap<>();
    	response.put("message", "Hello, Spring with Embedded Tomcat!");
        return response;
    }
}
