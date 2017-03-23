package com.cti.web;

import com.cti.repository.RepositoryConstraintException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

@ControllerAdvice
class ExceptionHandlingController {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, String> handleInternalServerError() {
        return singletonMap("message", "Internal server error");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(RepositoryConstraintException.class)
    @ResponseBody
    public Map<String, String> handleConflict(RepositoryConstraintException e) {
        return singletonMap("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, List<String>> handleBadRequest(MethodArgumentNotValidException exception) {
        return singletonMap("messages", exception.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList()));
    }


}
