package ua.com.javarush.parse.m5.passwordmanager.exception;

public class EmptyLoginException extends RuntimeException {
  public EmptyLoginException() {
    super("Empty Login Exception");
  }

  public EmptyLoginException(String message) {
    super(message);
  }
}
