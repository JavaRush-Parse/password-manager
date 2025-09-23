package ua.com.javarush.parse.m5.passwordmanager.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserLoginRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserLoginResponseDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserRegistrationRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserResponseDto;
import ua.com.javarush.parse.m5.passwordmanager.security.AuthenticationService;
import ua.com.javarush.parse.m5.passwordmanager.service.UserService;

@Tag(name = "Authentication Management", description = "Endpoints for user registration and login")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final UserService userService;
  private final AuthenticationService authenticationService;

  @PostMapping("/register")
  @Operation(
      summary = "Register a new user",
      description = "Creates a new user account based on the provided data.")
  @ApiResponse(
      responseCode = "200",
      description = "User registered successfully",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserResponseDto.class)))
  public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto) {
    return userService.register(requestDto);
  }

  @PostMapping("/login")
  @Operation(
      summary = "Authenticate a user",
      description = "Authenticates a user and returns a JWT token upon success.")
  public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
    return authenticationService.authenticate(requestDto);
  }
}
