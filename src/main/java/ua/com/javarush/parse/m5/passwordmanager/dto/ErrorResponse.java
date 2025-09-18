package ua.com.javarush.parse.m5.passwordmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
  private String message;

  public static ErrorResponse of(String message) {
    return new ErrorResponse(message);
  }
}
