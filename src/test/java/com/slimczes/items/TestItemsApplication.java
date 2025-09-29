package com.slimczes.items;

import org.springframework.boot.SpringApplication;

public class TestItemsApplication {

    public static void main(String[] args) {
        SpringApplication.from(ItemsApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
