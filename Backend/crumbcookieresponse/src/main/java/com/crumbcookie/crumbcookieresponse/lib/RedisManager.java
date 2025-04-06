package com.crumbcookie.crumbcookieresponse.lib;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import com.crumbcookie.crumbcookieresponse.Appcofig.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.val;

@Component
public class RedisManager {



  private RedisTemplate<String, String> redisTemplate;
  private ObjectMapper objectMapper;

  public RedisManager(RedisConnectionFactory factory,
    ObjectMapper objectMapper){
      RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(factory);
      redisTemplate.setKeySerializer(RedisSerializer.string());
      redisTemplate.setValueSerializer(RedisSerializer.json());
      redisTemplate.afterPropertiesSet();
      this.redisTemplate = redisTemplate;
      this.objectMapper = objectMapper;
    }

    public void set(String key, Object obj, Duration duration)
      throws JsonProcessingException {
        String json = this.objectMapper.writeValueAsString(obj);
        this.redisTemplate.opsForValue().set(key, json, duration);
      }
    
      public void set(String key, Object obj)
      throws JsonProcessingException {
        String json = this.objectMapper.writeValueAsString(obj);
        this.redisTemplate.opsForValue().set(key, json);
      }
    
    public <T> T get(String key, Class<T> clazz) throws JsonProcessingException{
      String json = this.redisTemplate.opsForValue().get(key);
      return json == null ? null : this.objectMapper.readValue(json, clazz);
    }

    public <T> T get(String key, TypeReference<T> typeReference) throws JsonProcessingException {
      String json = this.redisTemplate.opsForValue().get(key);
      return json == null ? null : this.objectMapper.readValue(json, typeReference);
  }

    public Map<String, Object> getAll() throws JsonProcessingException{
      Map<String, Object> redisAllData = new HashMap<>();
      Set<String> keys = this.redisTemplate.keys("*");

      if (keys != null) {
        for (String key : keys) {
          String jsonkey = this.redisTemplate.opsForValue().get(key);
          Object jsonvalue = this.objectMapper.readValue(jsonkey, Object.class);

          redisAllData.put(jsonkey, jsonvalue);
          
        }
        
      } else{
        System.out.println("Redis No Data !");
        return redisAllData;
      }
      return redisAllData;
    }

    public void deleteAll() {
      Set<String> keys = this.redisTemplate.keys("*");
      if (keys != null && !(keys.isEmpty())) {
        this.redisTemplate.delete(keys);
      }
    }
  
}
