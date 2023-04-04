package org.TouchAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SentimentAnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SentimentAnalyzerApplication.class, args);
        System.out.println("Your application is now up " +
                "and running.");
    }
    }
