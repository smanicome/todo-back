package io.smanicome.todoback.core.repository;

import io.smanicome.todoback.core.model.Todo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoRepository {
    Todo create(String title, int order);

    Optional<Todo> findByID(UUID id);

    int getMaxOrder();

    List<Todo> findAll();

    Todo update(Todo todo);

    void delete(UUID id);

    void deleteByCompleted(boolean completed);

    void deleteAll();

    boolean existsById(UUID id);

    int countByOrder(int order);

    int countByTitle(String title);
}
