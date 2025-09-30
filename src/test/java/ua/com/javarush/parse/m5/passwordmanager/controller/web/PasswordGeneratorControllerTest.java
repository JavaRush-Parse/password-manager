package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.javarush.parse.m5.passwordmanager.service.PasswordGeneratorService;

@ExtendWith(MockitoExtension.class)
class PasswordGeneratorControllerTest {

  private MockMvc mockMvc;

  @Mock private PasswordGeneratorService passwordGeneratorService;

  @BeforeEach
  void setUp() {
    PasswordGeneratorController controller =
        new PasswordGeneratorController(passwordGeneratorService);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void generatePassword_ShouldReturnCorrectViewAndModel() throws Exception {
    String testPassword = "Test1Password!@#";
    when(passwordGeneratorService.generateStrongPassword()).thenReturn(testPassword);

    mockMvc
        .perform(get("/password/generate"))
        .andExpect(status().isOk())
        .andExpect(view().name("fragments/password-input :: password-input"))
        .andExpect(model().attributeExists("generatedPassword"))
        .andExpect(model().attribute("generatedPassword", testPassword));
  }

  @Test
  void generatePassword_ShouldHandleSpecialCharacters() throws Exception {
    String specialPassword = "A1b!@#$%^&*()_+=";
    when(passwordGeneratorService.generateStrongPassword()).thenReturn(specialPassword);

    mockMvc
        .perform(get("/password/generate"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("generatedPassword", specialPassword))
        .andExpect(
            model()
                .attribute(
                    "generatedPassword", matchesPattern("^[a-zA-Z0-9!@#$%^&*()\\-_=+<>?]+$")));
  }

  @Test
  void generatePassword_ShouldReturnCorrectViewName() throws Exception {
    String testPassword = "TestPass123!";
    when(passwordGeneratorService.generateStrongPassword()).thenReturn(testPassword);

    mockMvc
        .perform(get("/password/generate"))
        .andExpect(status().isOk())
        .andExpect(view().name("fragments/password-input :: password-input"))
        .andExpect(model().size(1));
  }
}
