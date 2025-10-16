package ua.com.javarush.parse.m5.passwordmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.UserRepository;

@RequiredArgsConstructor
public abstract class BaseUserAwareService {

  protected final UserRepository userRepository;

  protected User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new RuntimeException("Access denied: Authentication required");
    }

    String email = authentication.getName();
    if ("anonymousUser".equals(email)) {
      throw new RuntimeException("Access denied: Authentication required");
    }

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found: " + email));
  }

  protected String getCurrentUserEmail() {
    return getCurrentUser().getEmail();
  }

  protected Long getCurrentUserId() {
    return getCurrentUser().getId();
  }
}
