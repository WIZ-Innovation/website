package com.wiz.exceptions;

import com.wiz.models.BaseResponse;

public class AnnotationException extends BusinessException {

    protected AnnotationException(BaseResponse errorResponse) {
        super(errorResponse);
    }

    public static AnnotationException of(String message, Object data) {
        return new AnnotationException(BaseResponse.fail(message, data));
    }
}
