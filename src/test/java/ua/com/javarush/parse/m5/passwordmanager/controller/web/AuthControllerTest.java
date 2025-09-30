package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserRegistrationRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserResponseDto;
import ua.com.javarush.parse.m5.passwordmanager.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

  @Mock private UserService userService;
  @Mock private Model model;
  @Mock private BindingResult bindingResult;

  private AuthController authController;

  @BeforeEach
  void setUp() {
    authController = new AuthController(userService);
  }

  @Nested
  @DisplayName("Registration Form Display")
  class RegistrationFormDisplay {

    @Test
    @DisplayName("Should display registration form with empty user object")
    void shouldDisplayRegistrationFormWithEmptyUserObject() {
      String viewName = authController.showRegistrationForm(model);

      assertThat(viewName).isEqualTo("register");
      verify(model).addAttribute("user", new UserRegistrationRequestDto());
    }

    @Test
    @DisplayName("Should return correct view name for registration form")
    void shouldReturnCorrectViewNameForRegistrationForm() {
      String viewName = authController.showRegistrationForm(model);

      assertThat(viewName).isEqualTo("register");
    }

    @Test
    @DisplayName("Should add user attribute to model")
    void shouldAddUserAttributeToModel() {
      authController.showRegistrationForm(model);

      verify(model).addAttribute("user", new UserRegistrationRequestDto());
    }
  }

  @Nested
  @DisplayName("User Registration Submission")
  class UserRegistrationSubmission {

    @Test
    @DisplayName("Should register user successfully and redirect to login")
    void shouldRegisterUserSuccessfullyAndRedirectToLogin() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      requestDto.setUserName("John Doe");
      requestDto.setEmail("john@example.com");
      requestDto.setPassword("password123");
      requestDto.setRepeatPassword("password123");

      UserResponseDto responseDto = new UserResponseDto();
      responseDto.setId(1L);
      responseDto.setUsername("John Doe");
      responseDto.setEmail("john@example.com");

      when(bindingResult.hasErrors()).thenReturn(false);
      when(userService.register(requestDto)).thenReturn(responseDto);

      String result = authController.registerUser(requestDto, bindingResult);

      assertThat(result).isEqualTo("redirect:/login?registration_success");
      verify(userService).register(requestDto);
    }

    @Test
    @DisplayName("Should return to registration form when validation errors exist")
    void shouldReturnToRegistrationFormWhenValidationErrorsExist() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      when(bindingResult.hasErrors()).thenReturn(true);

      String result = authController.registerUser(requestDto, bindingResult);

      assertThat(result).isEqualTo("register");
      verify(userService, never()).register(any(UserRegistrationRequestDto.class));
    }

    @Test
    @DisplayName("Should handle service exception and return to registration form")
    void shouldHandleServiceExceptionAndReturnToRegistrationForm() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      requestDto.setUserName("John Doe");
      requestDto.setEmail("john@example.com");
      requestDto.setPassword("password123");
      requestDto.setRepeatPassword("password123");

      when(bindingResult.hasErrors()).thenReturn(false);
      doThrow(new RuntimeException("Email already exists")).when(userService).register(requestDto);

      String result = authController.registerUser(requestDto, bindingResult);

      assertThat(result).isEqualTo("register");
      verify(bindingResult).rejectValue("email", "email.exists", "Email already exists");
      verify(userService).register(requestDto);
    }

    @Test
    @DisplayName("Should delegate registration to UserService when validation passes")
    void shouldDelegateRegistrationToUserServiceWhenValidationPasses() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      requestDto.setUserName("Jane Smith");
      requestDto.setEmail("jane@example.com");
      requestDto.setPassword("securepass");
      requestDto.setRepeatPassword("securepass");

      UserResponseDto responseDto = new UserResponseDto();
      responseDto.setId(2L);
      responseDto.setUsername("Jane Smith");
      responseDto.setEmail("jane@example.com");

      when(bindingResult.hasErrors()).thenReturn(false);
      when(userService.register(requestDto)).thenReturn(responseDto);

      String result = authController.registerUser(requestDto, bindingResult);

      assertThat(result).isEqualTo("redirect:/login?registration_success");
      verify(userService).register(requestDto);
    }
  }

  @Nested
  @DisplayName("Login Form Display")
  class LoginFormDisplay {

    @Test
    @DisplayName("Should display login form")
    void shouldDisplayLoginForm() {
      String viewName = authController.showLoginForm();

      assertThat(viewName).isEqualTo("login");
    }

    @Test
    @DisplayName("Should return correct view name for login form")
    void shouldReturnCorrectViewNameForLoginForm() {
      String result = authController.showLoginForm();

      assertThat(result).isEqualTo("login");
    }
  }

  @Nested
  @DisplayName("Error Handling")
  class ErrorHandling {

    @Test
    @DisplayName("Should handle runtime exceptions from UserService")
    void shouldHandleRuntimeExceptionsFromUserService() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      requestDto.setUserName("Test User");
      requestDto.setEmail("test@example.com");
      requestDto.setPassword("password");
      requestDto.setRepeatPassword("password");

      when(bindingResult.hasErrors()).thenReturn(false);
      doThrow(new RuntimeException("Database error")).when(userService).register(requestDto);

      String result = authController.registerUser(requestDto, bindingResult);

      assertThat(result).isEqualTo("register");
      verify(bindingResult).rejectValue("email", "email.exists", "Database error");
    }

    @Test
    @DisplayName("Should handle specific error messages from exceptions")
    void shouldHandleSpecificErrorMessagesFromExceptions() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      when(bindingResult.hasErrors()).thenReturn(false);

      RuntimeException customException = new RuntimeException("Custom error message");
      doThrow(customException).when(userService).register(requestDto);

      authController.registerUser(requestDto, bindingResult);

      verify(bindingResult).rejectValue("email", "email.exists", "Custom error message");
    }
  }

  @Nested
  @DisplayName("Controller Logic")
  class ControllerLogic {

    @Test
    @DisplayName("Should not call UserService when binding has errors")
    void shouldNotCallUserServiceWhenBindingHasErrors() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      when(bindingResult.hasErrors()).thenReturn(true);

      authController.registerUser(requestDto, bindingResult);

      verify(userService, never()).register(any(UserRegistrationRequestDto.class));
    }

    @Test
    @DisplayName("Should process registration in correct order")
    void shouldProcessRegistrationInCorrectOrder() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      UserResponseDto responseDto = new UserResponseDto();

      when(bindingResult.hasErrors()).thenReturn(false);
      when(userService.register(requestDto)).thenReturn(responseDto);

      String result = authController.registerUser(requestDto, bindingResult);

      // Verify order: validation check first, then service call
      verify(bindingResult).hasErrors();
      verify(userService).register(requestDto);
      assertThat(result).isEqualTo("redirect:/login?registration_success");
    }

    @Test
    @DisplayName("Should handle successful registration with different user data")
    void shouldHandleSuccessfulRegistrationWithDifferentUserData() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      requestDto.setUserName("María José");
      requestDto.setEmail("maria+jose@example.com");
      requestDto.setPassword("mi_contraseña123");
      requestDto.setRepeatPassword("mi_contraseña123");

      UserResponseDto responseDto = new UserResponseDto();
      responseDto.setId(99L);
      responseDto.setUsername("María José");
      responseDto.setEmail("maria+jose@example.com");

      when(bindingResult.hasErrors()).thenReturn(false);
      when(userService.register(requestDto)).thenReturn(responseDto);

      String result = authController.registerUser(requestDto, bindingResult);

      assertThat(result).isEqualTo("redirect:/login?registration_success");
      verify(userService).register(requestDto);
    }
  }
}
