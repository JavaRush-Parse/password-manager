package ua.com.javarush.parse.m5.passwordmanager.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private CustomUserDetailsService userDetailsService;

  @Test
  @DisplayName("Load user by username successfully")
  void loadUserByUsername_shouldReturnUserDetails() {
    // Given
    String email = "test@example.com";
    User user = new User();
    user.setEmail(email);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    // When
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    // Then
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(email);
  }

  @Test
  @DisplayName("Load user by username - User Not Found")
  void loadUserByUsername_shouldThrowException() {
    // Given
    String email = "test@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
  }
}
