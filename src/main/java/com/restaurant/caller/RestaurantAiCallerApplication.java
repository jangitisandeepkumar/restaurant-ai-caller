package com.restaurant.caller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestaurantAiCallerApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                RestaurantAiCallerApplication.class,
                args
        );

        System.out.println("Restaurant AI Caller Started!");
    }
}
