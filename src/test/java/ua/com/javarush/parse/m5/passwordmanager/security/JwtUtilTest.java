package ua.com.javarush.parse.m5.passwordmanager.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil Tests")
class JwtUtilTest {

  private JwtUtil jwtUtil;

  @BeforeEach
  void setUp() {
    jwtUtil =
        new JwtUtil("test_secret_key_for_jwt_util_testing_purpose_only_and_not_for_production_use");
    jwtUtil.expiration = 3600000; // 1 hour
  }

  @Test
  @DisplayName("Generate token successfully")
  void generateToken_shouldReturnToken() {
    // Given
    String username = "testuser";

    // When
    String token = jwtUtil.generateToken(username);

    // Then
    assertThat(token).isNotNull();
  }

  @Test
  @DisplayName("Get username from valid token")
  void getUsername_shouldReturnUsername() {
    // Given
    String username = "testuser";
    String token = jwtUtil.generateToken(username);

    // When
    String result = jwtUtil.getUsername(token);

    // Then
    assertThat(result).isEqualTo(username);
  }

  @Test
  @DisplayName("Validate valid token")
  void isValidToken_shouldReturnTrue() {
    // Given
    String username = "testuser";
    String token = jwtUtil.generateToken(username);

    // When
    boolean result = jwtUtil.isValidToken(token);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Validate invalid token")
  void isValidToken_shouldThrowException() {
    // Given
    String token = "invalid_token";

    // When & Then
    assertThrows(JwtException.class, () -> jwtUtil.isValidToken(token));
  }
}
