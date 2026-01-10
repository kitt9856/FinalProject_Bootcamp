package com.crumbcookie.crumbcookieresponse.Appcofig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
  //new RestTemplate to bean, no need to create resttemplate object every time
  @Bean
  RestTemplate restTemplate(){
    return new RestTemplate();
  }
}
