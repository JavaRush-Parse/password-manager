package ua.com.javarush.parse.m5.passwordmanager.exception;

public class ImportEntryDuplicateException extends RuntimeException {
  public ImportEntryDuplicateException() {
    super("Duplicate entry in the import");
  }

  public ImportEntryDuplicateException(String message) {
    super(message);
  }
}
