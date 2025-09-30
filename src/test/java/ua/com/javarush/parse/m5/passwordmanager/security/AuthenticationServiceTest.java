package ua.com.javarush.parse.m5.passwordmanager.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserLoginRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserLoginResponseDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

  @Mock private JwtUtil jwtUtil;

  @Mock private AuthenticationManager authenticationManager;

  @InjectMocks private AuthenticationService authenticationService;

  @Test
  @DisplayName("Authenticate user and return JWT token")
  void authenticate_shouldReturnToken() {
    // Given
    UserLoginRequestDto requestDto = new UserLoginRequestDto("test@example.com", "password");
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("test@example.com");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(jwtUtil.generateToken("test@example.com")).thenReturn("test_token");

    // When
    UserLoginResponseDto responseDto = authenticationService.authenticate(requestDto);

    // Then
    assertThat(responseDto).isNotNull();
    assertThat(responseDto.token()).isEqualTo("test_token");
  }
}
