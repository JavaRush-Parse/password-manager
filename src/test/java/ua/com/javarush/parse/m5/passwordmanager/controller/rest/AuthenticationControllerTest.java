package ua.com.javarush.parse.m5.passwordmanager.controller.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserLoginRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserLoginResponseDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserRegistrationRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserResponseDto;
import ua.com.javarush.parse.m5.passwordmanager.security.AuthenticationService;
import ua.com.javarush.parse.m5.passwordmanager.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationController Tests")
class AuthenticationControllerTest {

  @Mock private UserService userService;
  @Mock private AuthenticationService authenticationService;

  private AuthenticationController authenticationController;

  @BeforeEach
  void setUp() {
    authenticationController = new AuthenticationController(userService, authenticationService);
  }

  @Nested
  @DisplayName("User Registration")
  class UserRegistration {

    @Test
    @DisplayName("Should register user successfully with valid data")
    void shouldRegisterUserSuccessfullyWithValidData() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      requestDto.setUserName("John Doe");
      requestDto.setEmail("john@example.com");
      requestDto.setPassword("password123");
      requestDto.setRepeatPassword("password123");

      UserResponseDto responseDto = new UserResponseDto();
      responseDto.setId(1L);
      responseDto.setUsername("John Doe");
      responseDto.setEmail("john@example.com");

      when(userService.register(any(UserRegistrationRequestDto.class))).thenReturn(responseDto);

      UserResponseDto result = authenticationController.register(requestDto);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(1L);
      assertThat(result.getUsername()).isEqualTo("John Doe");
      assertThat(result.getEmail()).isEqualTo("john@example.com");
      verify(userService).register(requestDto);
    }

    @Test
    @DisplayName("Should delegate registration to UserService")
    void shouldDelegateRegistrationToUserService() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      requestDto.setUserName("Jane Smith");
      requestDto.setEmail("jane@example.com");
      requestDto.setPassword("securepass");
      requestDto.setRepeatPassword("securepass");

      UserResponseDto responseDto = new UserResponseDto();
      responseDto.setId(2L);
      responseDto.setUsername("Jane Smith");
      responseDto.setEmail("jane@example.com");

      when(userService.register(requestDto)).thenReturn(responseDto);

      UserResponseDto result = authenticationController.register(requestDto);

      assertThat(result).isSameAs(responseDto);
      verify(userService).register(requestDto);
    }

    @Test
    @DisplayName("Should handle special characters in registration data")
    void shouldHandleSpecialCharactersInRegistrationData() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      requestDto.setUserName("José García");
      requestDto.setEmail("jose+test@example.com");
      requestDto.setPassword("myP@ssw0rd!");
      requestDto.setRepeatPassword("myP@ssw0rd!");

      UserResponseDto responseDto = new UserResponseDto();
      responseDto.setId(3L);
      responseDto.setUsername("José García");
      responseDto.setEmail("jose+test@example.com");

      when(userService.register(requestDto)).thenReturn(responseDto);

      UserResponseDto result = authenticationController.register(requestDto);

      assertThat(result.getUsername()).isEqualTo("José García");
      assertThat(result.getEmail()).isEqualTo("jose+test@example.com");
      verify(userService).register(requestDto);
    }
  }

  @Nested
  @DisplayName("User Authentication")
  class UserAuthentication {

    @Test
    @DisplayName("Should authenticate user successfully with valid credentials")
    void shouldAuthenticateUserSuccessfullyWithValidCredentials() {
      UserLoginRequestDto requestDto = new UserLoginRequestDto("john@example.com", "password123");
      UserLoginResponseDto responseDto = new UserLoginResponseDto("jwt-token-here");

      when(authenticationService.authenticate(requestDto)).thenReturn(responseDto);

      UserLoginResponseDto result = authenticationController.login(requestDto);

      assertThat(result).isNotNull();
      assertThat(result.token()).isEqualTo("jwt-token-here");
      verify(authenticationService).authenticate(requestDto);
    }

    @Test
    @DisplayName("Should delegate authentication to AuthenticationService")
    void shouldDelegateAuthenticationToAuthenticationService() {
      UserLoginRequestDto requestDto = new UserLoginRequestDto("jane@example.com", "securepass");
      UserLoginResponseDto responseDto = new UserLoginResponseDto("another-jwt-token");

      when(authenticationService.authenticate(requestDto)).thenReturn(responseDto);

      UserLoginResponseDto result = authenticationController.login(requestDto);

      assertThat(result).isSameAs(responseDto);
      verify(authenticationService).authenticate(requestDto);
    }

    @Test
    @DisplayName("Should handle login with special characters in email")
    void shouldHandleLoginWithSpecialCharactersInEmail() {
      UserLoginRequestDto requestDto = new UserLoginRequestDto("user+test@example.com", "password");
      UserLoginResponseDto responseDto = new UserLoginResponseDto("special-token");

      when(authenticationService.authenticate(requestDto)).thenReturn(responseDto);

      UserLoginResponseDto result = authenticationController.login(requestDto);

      assertThat(result.token()).isEqualTo("special-token");
      verify(authenticationService).authenticate(requestDto);
    }
  }

  @Nested
  @DisplayName("Service Integration")
  class ServiceIntegration {

    @Test
    @DisplayName("Should properly inject UserService dependency")
    void shouldProperlyInjectUserServiceDependency() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      requestDto.setUserName("Test User");
      requestDto.setEmail("test@example.com");
      requestDto.setPassword("testpass");
      requestDto.setRepeatPassword("testpass");

      UserResponseDto responseDto = new UserResponseDto();
      when(userService.register(requestDto)).thenReturn(responseDto);

      authenticationController.register(requestDto);

      verify(userService).register(requestDto);
    }

    @Test
    @DisplayName("Should properly inject AuthenticationService dependency")
    void shouldProperlyInjectAuthenticationServiceDependency() {
      UserLoginRequestDto requestDto = new UserLoginRequestDto("test@example.com", "testpass");
      UserLoginResponseDto responseDto = new UserLoginResponseDto("test-token");

      when(authenticationService.authenticate(requestDto)).thenReturn(responseDto);

      authenticationController.login(requestDto);

      verify(authenticationService).authenticate(requestDto);
    }
  }

  @Nested
  @DisplayName("Controller Behavior")
  class ControllerBehavior {

    @Test
    @DisplayName("Should return exact response from UserService during registration")
    void shouldReturnExactResponseFromUserServiceDuringRegistration() {
      UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
      UserResponseDto serviceResponse = new UserResponseDto();
      serviceResponse.setId(999L);
      serviceResponse.setUsername("Exact User");
      serviceResponse.setEmail("exact@example.com");

      when(userService.register(requestDto)).thenReturn(serviceResponse);

      UserResponseDto controllerResponse = authenticationController.register(requestDto);

      assertThat(controllerResponse).isSameAs(serviceResponse);
    }

    @Test
    @DisplayName("Should return exact response from AuthenticationService during login")
    void shouldReturnExactResponseFromAuthenticationServiceDuringLogin() {
      UserLoginRequestDto requestDto = new UserLoginRequestDto("user@example.com", "password");
      UserLoginResponseDto serviceResponse = new UserLoginResponseDto("exact-token-123");

      when(authenticationService.authenticate(requestDto)).thenReturn(serviceResponse);

      UserLoginResponseDto controllerResponse = authenticationController.login(requestDto);

      assertThat(controllerResponse).isSameAs(serviceResponse);
    }
  }
}