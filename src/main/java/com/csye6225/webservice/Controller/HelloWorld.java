package com.csye6225.webservice.Controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class HelloWorld {
    @RequestMapping("/health")
    public String getHelloWorld() {

        return "Hello World!";
    }

}
