package ua.com.javarush.parse.m5.passwordmanager.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtUtil jwtUtil;

  @Mock private UserDetailsService userDetailsService;

  @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Mock private UserDetails userDetails;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_withValidToken_shouldAuthenticateUser()
      throws ServletException, IOException {
    // Given
    String token = "validToken";
    String username = "user";
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtUtil.isValidToken(token)).thenReturn(true);
    when(jwtUtil.getUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_withInvalidToken_shouldNotAuthenticateUser()
      throws ServletException, IOException {
    // Given
    String token = "invalidToken";
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtUtil.isValidToken(token)).thenReturn(false);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_withoutToken_shouldNotAuthenticateUser()
      throws ServletException, IOException {
    // Given
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_withBearerTokenWrongFormat_shouldNotAuthenticateUser()
      throws ServletException, IOException {
    // Given
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Invalid Bearer token");

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_withValidTokenButUserNotFound_shouldNotAuthenticateUser()
      throws ServletException, IOException {
    // Given
    String token = "validToken";
    String username = "user";
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtUtil.isValidToken(token)).thenReturn(true);
    when(jwtUtil.getUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(null);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}
