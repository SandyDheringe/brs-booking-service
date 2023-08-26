package com.brsbooking.exception;

import lombok.Data;

@Data
public class BRSFieldError {

    private final String field;
    private final String errorCode;
    private final String defaultMessage;

}
