package com.example.mind_laundromat.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseBuilder.error(ResponseCode.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.error(ResponseCode.SERVER_ERROR));
    }
}
