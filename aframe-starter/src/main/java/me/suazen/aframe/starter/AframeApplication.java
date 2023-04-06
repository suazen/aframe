package me.suazen.aframe.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import me.suazen.aframe.framework.core.restful.annotation.RestfulScan;

@SpringBootApplication
@ComponentScan({"me.suazen.aframe"})
@RestfulScan({"me.suazen.aframe"})
public class AframeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AframeApplication.class, args);
    }
}
