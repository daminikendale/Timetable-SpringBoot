package com.rspc.timetable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.rspc.timetable")
public class TimeTableApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimeTableApplication.class, args);
    }
}
