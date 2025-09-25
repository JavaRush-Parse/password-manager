package ua.com.javarush.parse.m5.passwordmanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserLoginRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserLoginResponseDto;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
    final Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    String token = jwtUtil.generateToken(authentication.getName());
    return new UserLoginResponseDto(token);
  }
}
