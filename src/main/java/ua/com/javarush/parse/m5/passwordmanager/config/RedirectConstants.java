package ua.com.javarush.parse.m5.passwordmanager.config;

public final class RedirectConstants {

  public static final String REDIRECT_HOME = "redirect:/";
  public static final String REDIRECT_COLLECTIONS = "redirect:/collections";

  private RedirectConstants() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
