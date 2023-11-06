package io.smanicome.todoback.core.service;

import io.smanicome.todoback.core.exception.*;
import io.smanicome.todoback.core.model.Todo;
import io.smanicome.todoback.core.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class TodoService implements ITodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public Todo create(String title) throws InvalidTitleException, TitleAlreadyInUseException {
        assertTitleIsValid(title);
        final int maxOrder = todoRepository.getMaxOrder();
        return todoRepository.create(title, maxOrder + 1);
    }

    @Override
    public Optional<Todo> findById(UUID id) {
        return todoRepository.findByID(id);
    }

    @Override
    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Override
    public Todo update(UUID id, String title, Integer order, Boolean completed)
            throws TodoNotFoundException, OrderAlreadyInUseException, NegativeOrderException, InvalidTitleException,
            TitleAlreadyInUseException {
        final Todo todoToUpdate = todoRepository.findByID(id).orElseThrow(TodoNotFoundException::new);

        if(title != null && !todoToUpdate.title().equals(title)) assertTitleIsValid(title);
        if(order != null && order != todoToUpdate.order()) assertOrderIsPositive(order);

        final Todo updatedTodo = TodoService.mergeTodo(title, order, completed).apply(todoToUpdate);

        return todoRepository.update(updatedTodo);
    }

    @Override
    public void deleteById(UUID id) throws TodoNotFoundException {
        if(!todoRepository.existsById(id)) throw new TodoNotFoundException();
        todoRepository.delete(id);
    }

    @Override
    public void deleteCompleted() {
        todoRepository.deleteByCompleted(true);
    }

    @Override
    public void deleteAll() {
        todoRepository.deleteAll();
    }

    private static Function<Todo, Todo> mergeTodo(String title, Integer order, Boolean completed) {
        return (Todo todo) -> todo
                .withTitle(Objects.requireNonNullElse(title, todo.title()))
                .withOrder(Objects.requireNonNullElse(order, todo.order()))
                .withCompleted(Objects.requireNonNullElse(completed, todo.completed()));
    }

    private void assertTitleIsValid(String title) throws InvalidTitleException, TitleAlreadyInUseException {
        if (title.isBlank()) throw new InvalidTitleException();

        final int todosWithTitleCount = todoRepository.countByTitle(title);
        if(todosWithTitleCount > 0) {
            throw new TitleAlreadyInUseException();
        }
    }

    private void assertOrderIsPositive(int order) throws NegativeOrderException, OrderAlreadyInUseException {
        if (order < 0) throw new NegativeOrderException();

        final int todosWithOrdersCount = todoRepository.countByOrder(order);
        if(todosWithOrdersCount > 0) {
            throw new OrderAlreadyInUseException();
        }
    }
}