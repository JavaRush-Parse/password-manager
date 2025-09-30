package ua.com.javarush.parse.m5.passwordmanager.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomAuthenticationEntryPoint Tests")
class CustomAuthenticationEntryPointTest {

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private AuthenticationException authException;
  @Mock private ServletOutputStream servletOutputStream;

  private CustomAuthenticationEntryPoint authenticationEntryPoint;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() throws IOException {
    authenticationEntryPoint = new CustomAuthenticationEntryPoint();
    objectMapper = new ObjectMapper();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(outputStream);

    when(response.getOutputStream()).thenReturn(servletOutputStream);
  }

  @Nested
  @DisplayName("Response Configuration")
  class ResponseConfiguration {

    @Test
    @DisplayName("Should set unauthorized status code")
    void shouldSetUnauthorizedStatusCode() throws IOException, ServletException {
      when(request.getRequestURI()).thenReturn("/api/test");

      authenticationEntryPoint.commence(request, response, authException);

      verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should set JSON content type")
    void shouldSetJsonContentType() throws IOException, ServletException {
      when(request.getRequestURI()).thenReturn("/api/test");

      authenticationEntryPoint.commence(request, response, authException);

      verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
  }

  @Nested
  @DisplayName("Response Body Content")
  class ResponseBodyContent {

    @Test
    @DisplayName("Should include correct status in response body")
    void shouldIncludeCorrectStatusInResponseBody() throws IOException, ServletException {
      String requestUri = "/api/v1/vault";
      when(request.getRequestURI()).thenReturn(requestUri);

      // Capture the output written to ServletOutputStream
      ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
      ServletOutputStream testOutputStream =
          new ServletOutputStream() {
            @Override
            public boolean isReady() {
              return true;
            }

            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}

            @Override
            public void write(int b) throws IOException {
              capturedOutput.write(b);
            }

            @Override
            public void println(String s) throws IOException {
              capturedOutput.write(s.getBytes());
              capturedOutput.write('\n');
            }
          };

      when(response.getOutputStream()).thenReturn(testOutputStream);

      authenticationEntryPoint.commence(request, response, authException);

      String responseBody = capturedOutput.toString();
      JsonNode jsonResponse = objectMapper.readTree(responseBody);

      assertThat(jsonResponse.get("status").asInt()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should include error field in response body")
    void shouldIncludeErrorFieldInResponseBody() throws IOException, ServletException {
      String requestUri = "/api/v1/vault";
      when(request.getRequestURI()).thenReturn(requestUri);

      ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
      ServletOutputStream testOutputStream = createTestOutputStream(capturedOutput);
      when(response.getOutputStream()).thenReturn(testOutputStream);

      authenticationEntryPoint.commence(request, response, authException);

      String responseBody = capturedOutput.toString();
      JsonNode jsonResponse = objectMapper.readTree(responseBody);

      assertThat(jsonResponse.get("error").asText()).isEqualTo("Unauthorized");
    }

    @Test
    @DisplayName("Should include authentication message in response body")
    void shouldIncludeAuthenticationMessageInResponseBody() throws IOException, ServletException {
      String requestUri = "/api/v1/vault";
      when(request.getRequestURI()).thenReturn(requestUri);

      ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
      ServletOutputStream testOutputStream = createTestOutputStream(capturedOutput);
      when(response.getOutputStream()).thenReturn(testOutputStream);

      authenticationEntryPoint.commence(request, response, authException);

      String responseBody = capturedOutput.toString();
      JsonNode jsonResponse = objectMapper.readTree(responseBody);

      assertThat(jsonResponse.get("message").asText())
          .isEqualTo("Authentication token was not provided or is invalid.");
    }

    @Test
    @DisplayName("Should include request path in response body")
    void shouldIncludeRequestPathInResponseBody() throws IOException, ServletException {
      String requestUri = "/api/v1/vault/items";
      when(request.getRequestURI()).thenReturn(requestUri);

      ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
      ServletOutputStream testOutputStream = createTestOutputStream(capturedOutput);
      when(response.getOutputStream()).thenReturn(testOutputStream);

      authenticationEntryPoint.commence(request, response, authException);

      String responseBody = capturedOutput.toString();
      JsonNode jsonResponse = objectMapper.readTree(responseBody);

      assertThat(jsonResponse.get("path").asText()).isEqualTo(requestUri);
    }
  }

  @Nested
  @DisplayName("Different Request Scenarios")
  class DifferentRequestScenarios {

    @Test
    @DisplayName("Should handle different request URIs correctly")
    void shouldHandleDifferentRequestUrisCorrectly() throws IOException, ServletException {
      String[] testUris = {
        "/api/v1/auth/login", "/api/v1/vault", "/api/v1/collections", "/api/v1/password/generate"
      };

      for (String uri : testUris) {
        when(request.getRequestURI()).thenReturn(uri);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        ServletOutputStream testOutputStream = createTestOutputStream(capturedOutput);
        when(response.getOutputStream()).thenReturn(testOutputStream);

        authenticationEntryPoint.commence(request, response, authException);

        String responseBody = capturedOutput.toString();
        JsonNode jsonResponse = objectMapper.readTree(responseBody);

        assertThat(jsonResponse.get("path").asText()).isEqualTo(uri);
      }
    }

    @Test
    @DisplayName("Should handle different authentication exceptions")
    void shouldHandleDifferentAuthenticationExceptions() throws IOException, ServletException {
      AuthenticationException[] exceptions = {
        new BadCredentialsException("Bad credentials"),
        new AuthenticationException("Generic auth error") {}
      };

      for (AuthenticationException exception : exceptions) {
        when(request.getRequestURI()).thenReturn("/api/test");

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        ServletOutputStream testOutputStream = createTestOutputStream(capturedOutput);
        when(response.getOutputStream()).thenReturn(testOutputStream);

        authenticationEntryPoint.commence(request, response, exception);

        String responseBody = capturedOutput.toString();
        JsonNode jsonResponse = objectMapper.readTree(responseBody);

        // Should always return the same standardized response regardless of exception type
        assertThat(jsonResponse.get("status").asInt()).isEqualTo(401);
        assertThat(jsonResponse.get("error").asText()).isEqualTo("Unauthorized");
        assertThat(jsonResponse.get("message").asText())
            .isEqualTo("Authentication token was not provided or is invalid.");
      }
    }
  }

  @Nested
  @DisplayName("JSON Response Validation")
  class JsonResponseValidation {

    @Test
    @DisplayName("Should produce valid JSON response")
    void shouldProduceValidJsonResponse() throws IOException, ServletException {
      when(request.getRequestURI()).thenReturn("/api/v1/test");

      ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
      ServletOutputStream testOutputStream = createTestOutputStream(capturedOutput);
      when(response.getOutputStream()).thenReturn(testOutputStream);

      authenticationEntryPoint.commence(request, response, authException);

      String responseBody = capturedOutput.toString();

      // Should be able to parse as valid JSON
      JsonNode jsonResponse = objectMapper.readTree(responseBody);
      assertThat(jsonResponse).isNotNull();
      assertThat(jsonResponse.isObject()).isTrue();
    }

    @Test
    @DisplayName("Should include all required fields in JSON response")
    void shouldIncludeAllRequiredFieldsInJsonResponse() throws IOException, ServletException {
      when(request.getRequestURI()).thenReturn("/api/v1/test");

      ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
      ServletOutputStream testOutputStream = createTestOutputStream(capturedOutput);
      when(response.getOutputStream()).thenReturn(testOutputStream);

      authenticationEntryPoint.commence(request, response, authException);

      String responseBody = capturedOutput.toString();
      JsonNode jsonResponse = objectMapper.readTree(responseBody);

      // Verify all required fields are present
      assertThat(jsonResponse.has("status")).isTrue();
      assertThat(jsonResponse.has("error")).isTrue();
      assertThat(jsonResponse.has("message")).isTrue();
      assertThat(jsonResponse.has("path")).isTrue();

      // Verify field values are not null
      assertThat(jsonResponse.get("status").isNull()).isFalse();
      assertThat(jsonResponse.get("error").isNull()).isFalse();
      assertThat(jsonResponse.get("message").isNull()).isFalse();
      assertThat(jsonResponse.get("path").isNull()).isFalse();
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCases {

    @Test
    @DisplayName("Should handle null request URI")
    void shouldHandleNullRequestUri() throws IOException, ServletException {
      when(request.getRequestURI()).thenReturn(null);

      ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
      ServletOutputStream testOutputStream = createTestOutputStream(capturedOutput);
      when(response.getOutputStream()).thenReturn(testOutputStream);

      authenticationEntryPoint.commence(request, response, authException);

      String responseBody = capturedOutput.toString();
      JsonNode jsonResponse = objectMapper.readTree(responseBody);

      assertThat(jsonResponse.get("path").isNull()).isTrue();
      assertThat(jsonResponse.get("status").asInt()).isEqualTo(401);
      assertThat(jsonResponse.get("error").asText()).isEqualTo("Unauthorized");
    }

    @Test
    @DisplayName("Should handle empty request URI")
    void shouldHandleEmptyRequestUri() throws IOException, ServletException {
      when(request.getRequestURI()).thenReturn("");

      ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
      ServletOutputStream testOutputStream = createTestOutputStream(capturedOutput);
      when(response.getOutputStream()).thenReturn(testOutputStream);

      authenticationEntryPoint.commence(request, response, authException);

      String responseBody = capturedOutput.toString();
      JsonNode jsonResponse = objectMapper.readTree(responseBody);

      assertThat(jsonResponse.get("path").asText()).isEmpty();
      assertThat(jsonResponse.get("status").asInt()).isEqualTo(401);
    }
  }

  private ServletOutputStream createTestOutputStream(ByteArrayOutputStream capturedOutput) {
    return new ServletOutputStream() {
      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}

      @Override
      public void write(int b) throws IOException {
        capturedOutput.write(b);
      }

      @Override
      public void println(String s) throws IOException {
        capturedOutput.write(s.getBytes());
        capturedOutput.write('\n');
      }
    };
  }
}
