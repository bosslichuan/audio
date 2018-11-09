package com.mv;

import com.mv.service.DoirService;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.mv")
public class AudioApplication implements ApplicationContextAware {


    @Resource
    DoirService doirService;

    public static void main(String[] args) {
        SpringApplication.run(AudioApplication.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Executors.newFixedThreadPool(1).submit(() -> doirService.patching());
    }
}
