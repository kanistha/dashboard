package org.user.dashboard.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.user.dashboard.exception.ErrorCode.USER_NOT_FOUND;

@ControllerAdvice
public class GlobalControllerExceptionAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(USER_NOT_FOUND.name(), ex.getMessage());
        return new ResponseEntity<>(response, NOT_FOUND);
    }

    @ExceptionHandler(UserDashboardException.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        ErrorResponse response = new ErrorResponse("SERVER_ERROR", ex.getMessage());
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }
}
