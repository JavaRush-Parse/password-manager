package ua.com.javarush.parse.m5.passwordmanager.config;

import java.security.SecureRandom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

  @Bean
  public SecureRandom secureRandom() {
    return new SecureRandom();
  }
}
