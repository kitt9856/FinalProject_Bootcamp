package com.crumbcookie.crumbcookieresponse.lib;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Component
@Getter
@NoArgsConstructor
public class CrumbManager {
  private static final String fcYaho = "https://fc.yahoo.com";
  private String yahooCookie;
  private String strFromgGetCrum;


  @Autowired
  RestTemplate restTemplate;
 



  /* public String getStrCrum(){
    
  } */

  public  void getKey() throws Exception {
   // RestTemplate restTemplate = new RestTemplate();

    restTemplate = new RestTemplateBuilder() //
        .connectTimeout(Duration.ofSeconds(20)) //
        .readTimeout(Duration.ofSeconds(20)) //
        .build();

    //CrumbManager crumbManager = new CrumbManager();
    //crumbManager.setYahCookie();
    if (this.strFromgGetCrum != null && this.yahooCookie != null) {
        return;
    }
    setYahCookie();
    String urlStr = "https://query1.finance.yahoo.com/v1/test/getcrumb";
    HttpHeaders headers =new HttpHeaders();
    headers.set("Cookie", null);
    headers.set("User-Agent", null);
    headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0");
    headers.set(HttpHeaders.COOKIE, getYahooCookie());
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response = restTemplate.exchange(urlStr, HttpMethod.GET, entity, String.class);
    this.strFromgGetCrum = response.getBody().toString();
    System.out.println("getKey Crumb: " + this.strFromgGetCrum); // 打印 Crumb
    System.out.println("getKey Cookie: " + getYahooCookie()); // 打印 Cookie
  }


  public  void  setYahCookie() throws IOException  {
    
    URL url = new URL(CrumbManager.fcYaho);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    int responCode = connection.getResponseCode();
    System.out.println("URL Respon: "+responCode);
    
    Map<String,List<String>>  urlHeader = new  HashMap<>();
    urlHeader = connection.getHeaderFields();
    List<String> cookies = new  ArrayList<>();
    cookies = urlHeader.get("Set-Cookie");

    String targetA3 = "";

    
    
    if (cookies != null) {
        for (String cookie: cookies){
           // System.err.println("cookies: " + cookie);
            if (cookie.contains("A3=")) {
                targetA3 = cookie.substring(0, cookie.indexOf(";"));
               // System.out.println("target: " + targetA3);
                
            }
        } 
       
    }else{
        System.out.println("Cannot get cookie");
    }
    this.yahooCookie = targetA3;
}
  
  

  public static void main(String[] args) {
  /*  try {
        URL url = new URL("https://fc.yahoo.com/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responCode = connection.getResponseCode();
        System.out.println("URL Respon: "+responCode);
        
        Map<String,List<String>>  urlHeader = new  HashMap<>();
        urlHeader = connection.getHeaderFields();
        List<String> cookies = new  ArrayList<>();
        cookies = urlHeader.get("Set-Cookie");

        String targetA3 ;

        if (cookies != null) {
            for (String cookie: cookies){
                System.err.println("cookies: " + cookie);
                if (cookie.contains("A3=")) {
                    targetA3 = cookie.substring(0, cookie.indexOf(";"));
                    System.out.println("target: " + targetA3);
                }
            } 
           
        }else{
            System.out.println("Cannot get cookie");
        }
        
    } catch (Exception e) {
        System.out.println("???");
    } */
    
    /* CrumbManager cManager = new CrumbManager();
    try {
        cManager.setYahCookie();
        System.out.println("" + cManager.yahooCookie);
        System.out.println(cManager.yahooCookie.charAt(0));
    } catch (Exception e) {
        System.err.println("An exception occurred: " + e.getMessage());
    } */

    //CrumbManager cManager = new CrumbManager();

    //debug getkey
   /*  RestTemplate restTemplate = new RestTemplate();
    CrumbManager crumbManager = new CrumbManager();
    try {
    crumbManager.setYahCookie();
    String urlStr = "https://query1.finance.yahoo.com/v1/test/getcrumb";
    //URL url = new URL("https://query1.finance.yahoo.com/v1/test/getcrumb");
    //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    //connection.getContent(); 
    HttpHeaders headers =new HttpHeaders();
    headers.set("Cookie", null);
    headers.set("User-Agent", null);
    //headers.clearContentHeaders();
    headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0");
    headers.set(HttpHeaders.COOKIE, crumbManager.getYahooCookie());
    //just for debug checking
    HttpEntity<String> entity = new HttpEntity<>(headers);
    System.out.println(entity.getHeaders());
    ResponseEntity<String> response = restTemplate.exchange(urlStr, HttpMethod.GET, entity, String.class);

    
    
   // System.out.println(connection); //check whelter 429
    System.out.println(response.getBody());
  //  System.out.println(restTemplate.getForObject(urlStr.toString(), String.class));

    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
 */

   /*  CrumbManager crumb = new CrumbManager();
    try {
        crumb.getKey();
    } catch (Exception e) {
        // TODO: handle exception
    }
    String x = crumb.getStrFromgGetCrum();
    System.out.println(x); */
    



    



  }
}
