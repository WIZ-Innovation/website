package com.wiz.exceptions;

import com.wiz.models.BaseResponse;

public class BusinessException extends RuntimeException {

    private final BaseResponse response;

    protected BusinessException(BaseResponse errorResponse) {
        this.response = errorResponse;
    }

    public BaseResponse errorResponse() {
        return response;
    }

    public static BusinessException of(int code, String message) {
        return new BusinessException(BaseResponse.fail(code, message));
    }

    public static BusinessException of(String message, Object data) {
        return new BusinessException(BaseResponse.fail(message, data));
    }
}
