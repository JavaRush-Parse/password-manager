package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.javarush.parse.m5.passwordmanager.security.JwtUtil;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

@WebMvcTest(HomeController.class)
@Import(HomeControllerTest.TestConfig.class)
class HomeControllerTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public VaultItemService vaultItemService() {
      return mock(VaultItemService.class);
    }

    @Bean
    public JwtUtil jwtUtil() {
      return mock(JwtUtil.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private VaultItemService service;

  @Test
  @WithMockUser
  void whenHome_thenReturnsHomeViewWithVaultItems() throws Exception {
    when(service.findAll()).thenReturn(List.of());

    mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("home"))
        .andExpect(model().attributeExists("vaultItems"));
  }
}
