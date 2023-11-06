package io.smanicome.todoback.core.service;

import io.smanicome.todoback.core.exception.*;
import io.smanicome.todoback.core.model.Todo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITodoService {
    Todo create(String title) throws InvalidTitleException, TitleAlreadyInUseException;

    Optional<Todo> findById(UUID id);

    List<Todo> findAll();

    Todo update(UUID id, String title, Integer order, Boolean completed) throws
            TodoNotFoundException, OrderAlreadyInUseException, TitleAlreadyInUseException, NegativeOrderException,
            InvalidTitleException;

    void deleteById(UUID id) throws TodoNotFoundException;

    void deleteCompleted();

    void deleteAll();
}
