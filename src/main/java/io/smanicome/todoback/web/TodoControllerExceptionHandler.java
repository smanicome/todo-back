package io.smanicome.todoback.web;

import io.smanicome.todoback.core.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TodoControllerExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TodoNotFoundException.class)
    public void handleTodoNotFound() {
        // Nothing to do
    }

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler({OrderAlreadyInUseException.class, TitleAlreadyInUseException.class})
    public void handleConflict() {
        // Nothing to do
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NegativeOrderException.class)
    public void handleNegativeOrder() {
        // Nothing to do
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTitleException.class)
    public void handleInvalidTitle() {
        // Nothing to do
    }
}
