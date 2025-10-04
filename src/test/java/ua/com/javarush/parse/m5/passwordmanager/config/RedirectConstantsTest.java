package ua.com.javarush.parse.m5.passwordmanager.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class RedirectConstantsTest {

  @Test
  void testRedirectHomeConstant() {
    assertEquals("redirect:/", RedirectConstants.REDIRECT_HOME,
        "REDIRECT_HOME should point to root path");
  }

  @Test
  void testRedirectCollectionsConstant() {
    assertEquals("redirect:/collections", RedirectConstants.REDIRECT_COLLECTIONS,
        "REDIRECT_COLLECTIONS should point to /collections path");
  }

  @Test
  void testConstantsAreNotNull() {
    assertNotNull(RedirectConstants.REDIRECT_HOME, "REDIRECT_HOME should not be null");
    assertNotNull(RedirectConstants.REDIRECT_COLLECTIONS, "REDIRECT_COLLECTIONS should not be null");
  }

  @Test
  void testConstantsStartWithRedirect() {
    assertTrue(RedirectConstants.REDIRECT_HOME.startsWith("redirect:"),
        "REDIRECT_HOME should start with 'redirect:'");
    assertTrue(RedirectConstants.REDIRECT_COLLECTIONS.startsWith("redirect:"),
        "REDIRECT_COLLECTIONS should start with 'redirect:'");
  }

  @Test
  void testClassIsFinal() {
    assertTrue(Modifier.isFinal(RedirectConstants.class.getModifiers()),
        "RedirectConstants class should be final");
  }

  @Test
  void testConstructorIsPrivate() throws NoSuchMethodException {
    Constructor<RedirectConstants> constructor = RedirectConstants.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()),
        "Constructor should be private");
  }

  @Test
  void testConstructorThrowsException() throws NoSuchMethodException {
    Constructor<RedirectConstants> constructor = RedirectConstants.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    InvocationTargetException exception = assertThrows(InvocationTargetException.class,
        constructor::newInstance,
        "Constructor should throw an exception when invoked");

    assertInstanceOf(UnsupportedOperationException.class, exception.getCause(),
        "Constructor should throw UnsupportedOperationException");
    assertEquals("This is a utility class and cannot be instantiated",
        exception.getCause().getMessage(),
        "Exception message should indicate utility class cannot be instantiated");
  }

  @Test
  void testConstantsAreFinal() throws NoSuchFieldException {
    assertTrue(Modifier.isFinal(
            RedirectConstants.class.getField("REDIRECT_HOME").getModifiers()),
        "REDIRECT_HOME should be final");
    assertTrue(Modifier.isFinal(
            RedirectConstants.class.getField("REDIRECT_COLLECTIONS").getModifiers()),
        "REDIRECT_COLLECTIONS should be final");
  }

  @Test
  void testConstantsAreStatic() throws NoSuchFieldException {
    assertTrue(Modifier.isStatic(
            RedirectConstants.class.getField("REDIRECT_HOME").getModifiers()),
        "REDIRECT_HOME should be static");
    assertTrue(Modifier.isStatic(
            RedirectConstants.class.getField("REDIRECT_COLLECTIONS").getModifiers()),
        "REDIRECT_COLLECTIONS should be static");
  }

  @Test
  void testConstantsArePublic() throws NoSuchFieldException {
    assertTrue(Modifier.isPublic(
            RedirectConstants.class.getField("REDIRECT_HOME").getModifiers()),
        "REDIRECT_HOME should be public");
    assertTrue(Modifier.isPublic(
            RedirectConstants.class.getField("REDIRECT_COLLECTIONS").getModifiers()),
        "REDIRECT_COLLECTIONS should be public");
  }
}
