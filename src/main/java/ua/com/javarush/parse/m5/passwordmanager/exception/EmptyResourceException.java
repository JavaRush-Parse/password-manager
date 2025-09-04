package ua.com.javarush.parse.m5.passwordmanager.exception;

public class EmptyResourceException extends RuntimeException {
  public EmptyResourceException() {
    super("Empty Resource Exception");
  }

  public EmptyResourceException(String message) {
    super(message);
  }
}
