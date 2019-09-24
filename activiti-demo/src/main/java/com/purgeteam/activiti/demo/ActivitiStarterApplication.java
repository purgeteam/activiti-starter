package com.purgeteam.activiti.demo;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ActivitiStarterApplication {

  public static void main(String[] args) {
    SpringApplication.run(ActivitiStarterApplication.class, args);
  }

}
