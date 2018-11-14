package com.ingenico.transfer_service.exception.handler;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleException(HttpServletRequest req, Exception e) {
        log.error("Unhandled exception on '{} {}'",
                req.getMethod(),
                req.getRequestURI() + (req.getQueryString() != null ? req.getQueryString() : ""),
                e
        );
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}