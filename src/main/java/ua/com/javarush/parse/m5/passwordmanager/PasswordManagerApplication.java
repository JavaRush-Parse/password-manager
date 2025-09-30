package ua.com.javarush.parse.m5.passwordmanager;

import org.springdoc.core.configuration.SpringDocHateoasConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(exclude = SpringDocHateoasConfiguration.class)
@ConfigurationPropertiesScan
public class PasswordManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(PasswordManagerApplication.class, args);
  }
}
