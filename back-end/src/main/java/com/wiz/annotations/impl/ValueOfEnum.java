package com.wiz.annotations.impl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Constraint;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import com.wiz.annotations.BaseAnnotationValidator;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnum.Validator.class)
public @interface ValueOfEnum {
    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};

    Class<? extends Enum<?>> enumClass();

    String message() default "Must be any of ";

    class Validator extends BaseAnnotationValidator<ValueOfEnum, CharSequence> {
        private String message;

        private List<String> acceptedValues;

        @Override
        public void initialize(ValueOfEnum annotation) {
            acceptedValues = Stream.of(annotation.enumClass().getEnumConstants()).map(Enum::name)
                    .collect(Collectors.toList());

            this.message = annotation.message() + String.join(",", acceptedValues);
        }

        @Override
        public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
            Boolean isValid = Objects.isNull(value);

            if (Boolean.FALSE.equals(isValid)) {
                isValid = acceptedValues.contains(value.toString());
            }

            if (Boolean.FALSE.equals(isValid)) {
                // disable existing violation message
                context.disableDefaultConstraintViolation();
                // build new violation message and add it
                context.buildConstraintViolationWithTemplate(this.message).addConstraintViolation();
            }

            return isValid;
        }
    }
}
