package com.cti.web;

import com.cti.repository.RepositoryConstraintException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.Map;

@ControllerAdvice
class ExceptionHandlingController {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public
    @ResponseBody
    Map<String, String> handleInternalServerError() {
        return Collections.singletonMap("message", "Internal server error");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(RepositoryConstraintException.class)
    public
    @ResponseBody
    Map<String, String> handleConflict(RepositoryConstraintException e) {
        return Collections.singletonMap("message", e.getMessage());
    }
}
