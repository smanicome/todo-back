package io.smanicome.todoback.core.service;

import io.smanicome.todoback.core.exception.*;
import io.smanicome.todoback.core.model.Todo;
import io.smanicome.todoback.core.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceShould {
    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;


    @Test
    void createAndReturnTodo() throws InvalidTitleException, TitleAlreadyInUseException {
        final String title = "test";
        final int maxOrder = 0;
        final Todo todo = new Todo(UUID.randomUUID(), title, maxOrder + 1, false);

        when(todoRepository.getMaxOrder()).thenReturn(maxOrder);
        when(todoRepository.create(anyString(), anyInt())).thenReturn(todo);

        final Todo resultingTodo = todoService.create(title);

        final InOrder orderVerifier = inOrder(todoRepository);
        orderVerifier.verify(todoRepository).getMaxOrder();
        orderVerifier.verify(todoRepository).create(title, maxOrder + 1);
        orderVerifier.verifyNoMoreInteractions();

        assertEquals(todo, resultingTodo);
    }

    @Test
    void returnEmptyWhenRequestingUnknownTodo() {
        when(todoRepository.findByID(any())).thenReturn(Optional.empty());

        final Optional<Todo> result = todoService.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void returnTodoWhenFound() {
        final UUID id = UUID.randomUUID();
        final Todo todo = new Todo(id, "test", 0, false);
        when(todoRepository.findByID(any())).thenReturn(Optional.of(todo));

        final Optional<Todo> result = todoService.findById(id);

        verify(todoRepository).findByID(id);
        verifyNoMoreInteractions(todoRepository);
        assertTrue(result.isPresent());
        assertEquals(todo, result.get());
    }

    @Test
    void returnAllTodos() {
        final Todo todo = new Todo(UUID.randomUUID(), "test", 0, false);
        when(todoRepository.findAll()).thenReturn(List.of(todo));

        final List<Todo> todos = todoService.findAll();

        verify(todoRepository).findAll();
        verifyNoMoreInteractions(todoRepository);
        assertEquals(List.of(todo), todos);
    }

    public static Stream<Arguments> updateTodoArguments() {
        final Todo todo = new Todo(UUID.randomUUID(), "test", 0, false);
        final String title = "update";
        final int order = 1;
        final boolean completed = true;

        return Stream.of(
                Arguments.of(title, null, null, todo.withTitle(title)),
                Arguments.of(null, order, null, todo.withOrder(order)),
                Arguments.of(null, null, completed, todo.withCompleted(completed)),
                Arguments.of(title, order, null, todo.withTitle(title).withOrder(order)),
                Arguments.of(title, null, completed, todo.withTitle(title).withCompleted(completed)),
                Arguments.of(null, order, completed, todo.withOrder(order).withCompleted(completed)),
                Arguments.of(title, order, completed, todo.withTitle(title).withOrder(order).withCompleted(completed))
        );
    }

    @ParameterizedTest
    @MethodSource("updateTodoArguments")
    void updateTodo(String title, Integer order, Boolean completed, Todo expectedTodo) throws OrderAlreadyInUseException, TodoNotFoundException, NegativeOrderException, InvalidTitleException, TitleAlreadyInUseException {
        final Todo todo = new Todo(expectedTodo.id(), "test", 0, false);

        when(todoRepository.findByID(any())).thenReturn(Optional.of(todo));
        when(todoRepository.update(any())).thenReturn(expectedTodo);

        final Todo result = todoService.update(todo.id(), title, order, completed);

        assertEquals(expectedTodo, result);

        final InOrder orderVerifier = inOrder(todoRepository);
        orderVerifier.verify(todoRepository).findByID(todo.id());
        if(order != null) {
            orderVerifier.verify(todoRepository).countByOrder(order);
        }
        orderVerifier.verify(todoRepository).update(expectedTodo);
        orderVerifier.verifyNoMoreInteractions();
    }


    @Test
    void throwWhenUpdatingUnknownTodo() {
        final UUID id = UUID.randomUUID();

        assertThrows(TodoNotFoundException.class, () -> todoService.update(
                id,
                null, null, null
            )
        );

        verify(todoRepository).findByID(id);
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void throwWhenUpdatingWithConflictingOrder() {
        final Todo todo = new Todo(UUID.randomUUID(), "test", 0, false);
        when(todoRepository.findByID(any())).thenReturn(Optional.of(todo));
        when(todoRepository.countByOrder(anyInt())).thenReturn(1);

        assertThrows(OrderAlreadyInUseException.class, () -> todoService.update(
                todo.id(),
                null, 1, null
            )
        );

        final InOrder orderVerifier = inOrder(todoRepository);
        orderVerifier.verify(todoRepository).findByID(todo.id());
        orderVerifier.verify(todoRepository).countByOrder(1);
        orderVerifier.verifyNoMoreInteractions();
    }

    @Test
    void deleteTodo() {
        final UUID id = UUID.randomUUID();
        when(todoRepository.existsById(any())).thenReturn(true);

        assertDoesNotThrow(() -> todoService.deleteById(id));

        final InOrder orderVerifier = inOrder(todoRepository);
        orderVerifier.verify(todoRepository).existsById(id);
        orderVerifier.verify(todoRepository).delete(id);
        orderVerifier.verifyNoMoreInteractions();
    }

    @Test
    void throwWhenDeletingUnknownTodo() {
        final UUID id = UUID.randomUUID();
        when(todoRepository.existsById(id)).thenReturn(false);

        assertThrows(TodoNotFoundException.class, () -> todoService.deleteById(id));

        verify(todoRepository).existsById(id);
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void deleteCompletedTodos() {
        todoService.deleteCompleted();

        verify(todoRepository).deleteByCompleted(true);
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void deleteAll() {
        todoService.deleteAll();

        verify(todoRepository).deleteAll();
        verifyNoMoreInteractions(todoRepository);
    }
}