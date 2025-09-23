package ua.com.javarush.parse.m5.passwordmanager.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
  private String firstFieldName;
  private String secondFieldName;

  @Override
  public void initialize(FieldMatch constraintAnnotation) {
    firstFieldName = constraintAnnotation.first();
    secondFieldName = constraintAnnotation.second();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    Object field = new BeanWrapperImpl(value).getPropertyValue(this.firstFieldName);
    Object fieldMatch = new BeanWrapperImpl(value).getPropertyValue(this.secondFieldName);
    return Objects.equals(field, fieldMatch);
  }
}
