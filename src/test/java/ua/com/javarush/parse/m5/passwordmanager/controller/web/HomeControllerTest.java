package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.javarush.parse.m5.passwordmanager.security.JwtUtil;

@WebMvcTest(HomeController.class)
@Import(HomeControllerTest.TestConfig.class)
@DisplayName("HomeController Tests")
class HomeControllerTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public JwtUtil jwtUtil() {
      return mock(JwtUtil.class);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
          .build();
    }
  }

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Get public homepage")
  void whenHome_thenReturnsIndexView() throws Exception {
    mockMvc
        .perform(get("/").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("index"));
  }
}
