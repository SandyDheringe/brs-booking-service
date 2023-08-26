package com.brsbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(BRSResourceNotFoundException.class)
    public BRSError handleBRSResourceNotFoundException(BRSResourceNotFoundException ex) {
        return new BRSError(BRSErrorType.NOT_FOUND_ERROR, ex.getMessage(), LocalDateTime.now(), null);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(BRSException.class)
    public BRSError handleBRSException(BRSResourceNotFoundException ex) {
        return new BRSError(BRSErrorType.SERVER_ERROR, ex.getMessage(), LocalDateTime.now(), null);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred");
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//    }
}