package com.wiz.annotations.impl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import javax.validation.Constraint;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import com.wiz.annotations.BaseAnnotationValidator;
import com.wiz.utils.CommonUtils;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HasLength.Validator.class)
public @interface HasLength {
    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};

    String message() default "Must be not empty!";

    class Validator extends BaseAnnotationValidator<HasLength, Collection<?>> {

        @Override
        public void initialize(HasLength annotation) {}

        @Override
        public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
            return CommonUtils.hasLength(value);
        }
    }
}
