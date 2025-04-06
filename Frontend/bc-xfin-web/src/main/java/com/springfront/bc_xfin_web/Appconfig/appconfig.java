package com.springfront.bc_xfin_web.Appconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.springfront.bc_xfin_web.lib.DateManager;

@Configuration
public class appconfig {
  @Bean
  RestTemplate restTemplate(){
    return new RestTemplate();
  }

  
}
