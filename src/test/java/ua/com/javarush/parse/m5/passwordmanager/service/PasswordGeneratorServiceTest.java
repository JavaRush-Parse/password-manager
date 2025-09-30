package ua.com.javarush.parse.m5.passwordmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.javarush.parse.m5.passwordmanager.config.PasswordGeneratorProperties;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordGeneratorService Tests")
class PasswordGeneratorServiceTest {

  private static final String DEFAULT_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
  private static final String DEFAULT_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String DEFAULT_NUMBERS = "0123456789";
  private static final String DEFAULT_SYMBOLS = "!@#$%^&*()-_=+<>?";

  @Mock private PasswordGeneratorProperties properties;
  @Mock private SecureRandom secureRandom;

  private PasswordGeneratorService passwordGeneratorService;

  @BeforeEach
  void setUp() {
    passwordGeneratorService = new PasswordGeneratorService(properties, secureRandom);
    setupDefaultProperties();
  }

  private void setupDefaultProperties() {
    when(properties.getLength()).thenReturn(16);
    when(properties.getLowercaseChars()).thenReturn(DEFAULT_LOWERCASE);
    when(properties.getUppercaseChars()).thenReturn(DEFAULT_UPPERCASE);
    when(properties.getNumbers()).thenReturn(DEFAULT_NUMBERS);
    when(properties.getSymbols()).thenReturn(DEFAULT_SYMBOLS);
  }

  @Nested
  @DisplayName("Basic Password Generation")
  class BasicPasswordGeneration {

    @Test
    @DisplayName("Should return password with correct length")
    void shouldReturnPasswordWithCorrectLength() {
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      String password = passwordGeneratorService.generateStrongPassword();

      assertThat(password).hasSize(16);
      verify(properties).getLength();
    }

    @Test
    @DisplayName("Should contain at least one character from each type")
    void shouldContainAtLeastOneCharacterFromEachType() {
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      String password = passwordGeneratorService.generateStrongPassword();

      assertThat(password).matches(".*[a-z].*");
      assertThat(password).matches(".*[A-Z].*");
      assertThat(password).matches(".*[0-9].*");
      assertThat(password).matches(".*[!@#$%^&*()\\-_=+<>?].*");
    }

    @Test
    @DisplayName("Should only contain allowed characters")
    void shouldOnlyContainAllowedCharacters() {
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      String password = passwordGeneratorService.generateStrongPassword();

      Pattern allowedCharsPattern = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()\\-_=+<>?]+$");
      assertThat(password).matches(allowedCharsPattern);
    }

    @Test
    @DisplayName("Should not be empty or null")
    void shouldNotBeEmptyOrNull() {
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      String password = passwordGeneratorService.generateStrongPassword();

      assertThat(password).isNotNull().isNotEmpty();
    }
  }

  @Nested
  @DisplayName("Custom Configuration")
  class CustomConfiguration {

    @Test
    @DisplayName("Should respect custom length")
    void shouldRespectCustomLength() {
      when(properties.getLength()).thenReturn(20);
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      String password = passwordGeneratorService.generateStrongPassword();

      assertThat(password).hasSize(20);
    }

    @Test
    @DisplayName("Should respect custom character sets")
    void shouldRespectCustomCharacterSets() {
      when(properties.getLength()).thenReturn(10);
      when(properties.getLowercaseChars()).thenReturn("abc");
      when(properties.getUppercaseChars()).thenReturn("XYZ");
      when(properties.getNumbers()).thenReturn("123");
      when(properties.getSymbols()).thenReturn("!@#");
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      String password = passwordGeneratorService.generateStrongPassword();

      assertThat(password).hasSize(10);
      assertThat(password).matches("^[abcXYZ123!@#]+$");
      assertThat(password).containsAnyOf("a", "b", "c");
      assertThat(password).containsAnyOf("X", "Y", "Z");
      assertThat(password).containsAnyOf("1", "2", "3");
      assertThat(password).containsAnyOf("!", "@", "#");
    }

    @Test
    @DisplayName("Should handle minimum length of 4")
    void shouldHandleMinimumLength() {
      when(properties.getLength()).thenReturn(4);
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      String password = passwordGeneratorService.generateStrongPassword();

      assertThat(password).hasSize(4);
      assertThat(password).matches(".*[a-z].*");
      assertThat(password).matches(".*[A-Z].*");
      assertThat(password).matches(".*[0-9].*");
      assertThat(password).matches(".*[!@#$%^&*()\\-_=+<>?].*");
    }
  }

  @Nested
  @DisplayName("Randomness and Security")
  class RandomnessAndSecurity {

    @Test
    @DisplayName("Should generate different passwords on multiple calls")
    void shouldGenerateDifferentPasswordsOnMultipleCalls() {
      passwordGeneratorService = new PasswordGeneratorService(properties, new SecureRandom());

      String password1 = passwordGeneratorService.generateStrongPassword();
      String password2 = passwordGeneratorService.generateStrongPassword();

      assertThat(password1).isNotEqualTo(password2);
    }

    @Test
    @DisplayName("Should use SecureRandom for character selection")
    void shouldUseSecureRandomForCharacterSelection() {
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      passwordGeneratorService.generateStrongPassword();

      verify(secureRandom, atLeastOnce()).nextInt(anyInt());
    }

    @Test
    @DisplayName("Should use SecureRandom for shuffling")
    void shouldUseSecureRandomForShuffling() {
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      passwordGeneratorService.generateStrongPassword();

      verify(secureRandom, atLeastOnce()).nextInt(anyInt());
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCases {

    @Test
    @DisplayName("Should handle large password length")
    void shouldHandleLargePasswordLength() {
      when(properties.getLength()).thenReturn(100);
      when(secureRandom.nextInt(anyInt())).thenReturn(0);

      String password = passwordGeneratorService.generateStrongPassword();

      assertThat(password).hasSize(100);
      assertThat(password).matches(".*[a-z].*");
      assertThat(password).matches(".*[A-Z].*");
      assertThat(password).matches(".*[0-9].*");
      assertThat(password).matches(".*[!@#$%^&*()\\-_=+<>?].*");
    }
  }
}
