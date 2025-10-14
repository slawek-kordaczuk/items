package com.slimczes.items;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.slimczes.items.api.proxy")
public class ItemsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemsApplication.class, args);
    }

}
