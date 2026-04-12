package com.cargo.onerecord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CargoOneRecordApplication {
    public static void main(String[] args) {
        SpringApplication.run(CargoOneRecordApplication.class, args);
    }
}