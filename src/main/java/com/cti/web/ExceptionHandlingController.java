package com.cti.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.PersistenceException;
import java.util.Collections;
import java.util.Map;

@ControllerAdvice
class ExceptionHandlingController {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PersistenceException.class)
    public
    @ResponseBody
    Map<String, String> handleConflict(PersistenceException e) {
        return Collections.singletonMap("message", e.getMessage());
    }
}
