package com.zzyl.nursing.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HelloJob {

    public void hello(){
        System.out.println("hello world"+ LocalDateTime.now());
    }
}
