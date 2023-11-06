package io.smanicome.todoback.web;

import io.smanicome.todoback.core.exception.*;
import io.smanicome.todoback.core.model.Todo;
import io.smanicome.todoback.core.service.ITodoService;
import io.smanicome.todoback.web.request.*;
import io.smanicome.todoback.web.response.TodoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final ITodoService todoService;

    public TodoController(ITodoService todoService) {
        this.todoService = todoService;
    }

    private static String getUrlOfTodo(Todo todo) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment("todos", "{id}")
                .buildAndExpand(todo.id())
                .toString();
    }

    private static TodoResponse convertTodoToTodoResponse(Todo todo) {
        final String url = getUrlOfTodo(todo);
        return new TodoResponse(todo.id(), todo.title(), todo.order(), todo.completed(), url);
    }

    @GetMapping
    public List<TodoResponse> getAll() {
        return todoService.findAll().stream()
                .map(TodoController::convertTodoToTodoResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.of(todoService.findById(id).map(TodoController::convertTodoToTodoResponse));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse create(@RequestBody @Valid TodoCreationRequest request) throws InvalidTitleException, TitleAlreadyInUseException {
        var todo = todoService.create(request.title());
        return convertTodoToTodoResponse(todo);
    }

    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable("id") UUID id, @RequestBody @Valid TodoFullUpdateRequest todoFullUpdateRequest) throws OrderAlreadyInUseException, TodoNotFoundException, NegativeOrderException, InvalidTitleException, TitleAlreadyInUseException {
        final var todo = todoService.update(id, todoFullUpdateRequest.title(), todoFullUpdateRequest.order(), todoFullUpdateRequest.completed());
        return convertTodoToTodoResponse(todo);
    }

    @PatchMapping("/{id}")
    public TodoResponse updatePartially(@PathVariable("id") UUID id, @RequestBody TodoPartialUpdateRequest todoPartialUpdateRequest) throws OrderAlreadyInUseException, NegativeOrderException, InvalidTitleException, TodoNotFoundException, TitleAlreadyInUseException {
        final var todo = todoService.update(id, todoPartialUpdateRequest.title(), todoPartialUpdateRequest.order(), todoPartialUpdateRequest.completed());
        return convertTodoToTodoResponse(todo);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll()  {
        todoService.deleteAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) throws TodoNotFoundException {
        todoService.deleteById(id);
    }

    @DeleteMapping("/completed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByCompleted() {
        todoService.deleteCompleted();
    }
}
