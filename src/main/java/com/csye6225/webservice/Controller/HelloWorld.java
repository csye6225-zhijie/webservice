package com.csye6225.webservice.Controller;

import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class HelloWorld {
    @Autowired
    private StatsDClient statsDClient;

    @RequestMapping("/healthz")
    public String getHelloWorld() {
        statsDClient.incrementCounter("_healthCheck_API_");

        return "Hello World, CICD!";
    }

}
