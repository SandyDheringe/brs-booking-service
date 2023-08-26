package com.brsbooking.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ResourceNotFoundException {
    private final BRSErrorType brsErrorType;
    private final String errorMessage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final Date timestamp;
    private final List<BRSFieldError> fieldErrors;
}
