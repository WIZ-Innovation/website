package com.wiz.annotations;

import java.lang.annotation.Annotation;
import javax.validation.ConstraintValidator;

public abstract class BaseAnnotationValidator<A extends Annotation, V>
        implements ConstraintValidator<A, V> {
    @Override
    public abstract void initialize(A annotation);
}

