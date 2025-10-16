package ua.com.javarush.parse.m5.passwordmanager.service;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserRegistrationRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserResponseDto;
import ua.com.javarush.parse.m5.passwordmanager.entity.Role;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.mapper.UserMapper;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.RoleRepository;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Transactional
  public UserResponseDto register(UserRegistrationRequestDto requestDto) throws RuntimeException {
    if (userRepository.existsByEmail(requestDto.getEmail())) {
      throw new RuntimeException("User with this email already exists");
    }

    User user = userMapper.toModel(requestDto);
    user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

    user.setUserName(requestDto.getUserName());
    user.setEmail(requestDto.getEmail());

    Role userRole =
        roleRepository
            .findByRole(Role.RoleName.USER)
            .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
    user.setRoles(Set.of(userRole));

    User savedUser = userRepository.save(user);
    return userMapper.toDto(savedUser);
  }

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new RuntimeException("No authenticated user found");
    }

    String email = authentication.getName();
    if ("anonymousUser".equals(email)) {
      throw new RuntimeException("Access denied: Authentication required");
    }

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found: " + email));
  }

  public Optional<User> getCurrentUserIfAuthenticated() {
    try {
      return Optional.of(getCurrentUser());
    } catch (RuntimeException e) {
      return Optional.empty();
    }
  }
}
