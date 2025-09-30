package ua.com.javarush.parse.m5.passwordmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "password-generator")
public class PasswordGeneratorProperties {

  private int length = 16;
  private String lowercaseChars = "abcdefghijklmnopqrstuvwxyz";
  private String uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private String numbers = "0123456789";
  private String symbols = "!@#$%^&*()-_=+<>?";
}
