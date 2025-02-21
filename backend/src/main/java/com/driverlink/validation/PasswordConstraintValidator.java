package com.driverlink.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    PasswordValidator validator =
        new PasswordValidator(
            Arrays.asList(
                // Length rule                  
                new LengthRule(8, 30),
                // At least one upper case letter
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // At least one lower case letter
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // At least one number
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // At least one special character
                new CharacterRule(EnglishCharacterData.Special, 1),
                // No whitespace
                new WhitespaceRule()));

    RuleResult result = validator.validate(new PasswordData(password));
    if (result.isValid()) {
      return true;
    }

    List<String> messages = validator.getMessages(result);
    String messageTemplate = messages.stream().collect(Collectors.joining(","));
    context
        .buildConstraintViolationWithTemplate(messageTemplate)
        .addConstraintViolation()
        .disableDefaultConstraintViolation();
    return false;
  }
}
