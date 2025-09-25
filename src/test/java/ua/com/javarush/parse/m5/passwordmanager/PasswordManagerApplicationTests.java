package ua.com.javarush.parse.m5.passwordmanager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Password Manager Application Tests")
class PasswordManagerApplicationTests {

  @Test
  @DisplayName("Context loads successfully")
  void contextLoads() {}
}
