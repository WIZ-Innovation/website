package com.wiz.utils;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.bval.jsr.ApacheValidationProvider;
import com.wiz.exceptions.AnnotationException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ValidateAnnotationUtils {
    private static Validator getValidator() {
        return ValidatorFactoryEnum.APACHE_INSTANCE.getValidator();
    }

    public static <T> void validate(T value) {
        Set<ConstraintViolation<T>> constraintViolations = getValidator().validate(value);

        if (!constraintViolations.isEmpty()) {
            JsonObject response = new JsonObject();
            constraintViolations.forEach(constraintViolation -> {
                response.put(constraintViolation.getPropertyPath().toString(),
                        new JsonObject().put("rejectedValue", constraintViolation.getInvalidValue())
                                .put("message", constraintViolation.getMessage()));
            });

            throw AnnotationException.of("Dữ liệu không hợp lệ", new JsonArray().add(response));
        }
    }
}


enum ValidatorFactoryEnum {

    APACHE_INSTANCE {

        ValidatorFactory validatorFactory = Validation.byProvider(ApacheValidationProvider.class)
                .configure().buildValidatorFactory();

        @Override
        public Validator getValidator() {
            return validatorFactory.getValidator();
        }

    };

    public abstract Validator getValidator();
}

