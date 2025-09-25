package ua.com.javarush.parse.m5.passwordmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserRegistrationRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserResponseDto;
import ua.com.javarush.parse.m5.passwordmanager.entity.Role;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.mapper.UserMapper;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.RoleRepository;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserService userService;

  @Test
  @DisplayName("Register new user successfully")
  void register_whenUserDoesNotExist_shouldRegisterUser() {
    // Given
    UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
    requestDto.setEmail("test@example.com");
    requestDto.setPassword("password");
    requestDto.setUsername("testuser");

    when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);

    User user = new User();
    user.setEmail(requestDto.getEmail());
    user.setUserName(requestDto.getUsername());
    when(userMapper.toModel(requestDto)).thenReturn(user);

    when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");

    Role userRole = new Role();
    userRole.setRole(Role.RoleName.USER);
    when(roleRepository.findByRole(Role.RoleName.USER)).thenReturn(Optional.of(userRole));

    User savedUser = new User();
    savedUser.setId(1L);
    savedUser.setEmail(requestDto.getEmail());
    savedUser.setUserName(requestDto.getUsername());
    savedUser.setRoles(Set.of(userRole));
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    UserResponseDto responseDto = new UserResponseDto();
    responseDto.setId(1L);
    responseDto.setEmail(requestDto.getEmail());
    responseDto.setUsername(requestDto.getUsername());
    when(userMapper.toDto(savedUser)).thenReturn(responseDto);

    // When
    UserResponseDto result = userService.register(requestDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(requestDto.getEmail());
    assertThat(result.getUsername()).isEqualTo(requestDto.getUsername());
  }

  @Test
  @DisplayName("Register user that already exists")
  void register_whenUserExists_shouldThrowException() {
    // Given
    UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
    requestDto.setEmail("test@example.com");

    when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

    // When & Then
    assertThrows(RuntimeException.class, () -> userService.register(requestDto));
  }
}
