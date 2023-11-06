package io.smanicome.todoback.data;

import io.smanicome.todoback.core.model.Todo;
import io.smanicome.todoback.data.jpa.CrudTodoRepository;
import io.smanicome.todoback.data.jpa.TodoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaTodoRepositoryShould {
    @Mock
    private CrudTodoRepository crudTodoRepository;

    @InjectMocks
    private JpaTodoRepository jpaTodoRepository;

    @Test
    void createTodo() {
        final TodoEntity entityToSave = new TodoEntity(null, "test", 0, false);
        final TodoEntity savedEntity = new TodoEntity(
                UUID.randomUUID(),
                entityToSave.getTitle(),
                entityToSave.getOrder(),
                entityToSave.isCompleted()
        );
        final Todo todo = new Todo(
                savedEntity.getId(),
                savedEntity.getTitle(),
                savedEntity.getOrder(),
                savedEntity.isCompleted()
        );
        when(crudTodoRepository.save(any())).thenReturn(savedEntity);

        final Todo resultingTodo = jpaTodoRepository.create(entityToSave.getTitle(), entityToSave.getOrder());

        assertEquals(todo, resultingTodo);
        verify(crudTodoRepository).save(entityToSave);
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @Test
    void returnRequestedTodoForId() {
        final TodoEntity entity = new TodoEntity(
                UUID.randomUUID(),
                "test",
                0,
                false
        );
        final Todo todo = new Todo(
                entity.getId(),
                entity.getTitle(),
                entity.getOrder(),
                entity.isCompleted()
        );
        when(crudTodoRepository.findById(any())).thenReturn(Optional.of(entity));

        final Optional<Todo> resultingTodo = jpaTodoRepository.findByID(entity.getId());

        assertEquals(Optional.of(todo), resultingTodo);
        verify(crudTodoRepository).findById(entity.getId());
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @Test
    void returnEmptyWhenRequestingUnknownTodo() {
        final UUID id = UUID.randomUUID();

        final Optional<Todo> resultingTodo = jpaTodoRepository.findByID(id);

        assertTrue(resultingTodo.isEmpty());
        verify(crudTodoRepository).findById(id);
        verifyNoMoreInteractions(crudTodoRepository);
    }



    @Test
    void returnExistingMaxOrder() {
        int expectedMaxOrder = 5;
        when(crudTodoRepository.findMaxOrder()).thenReturn(Optional.of(expectedMaxOrder));
        final int maxOrder = jpaTodoRepository.getMaxOrder();

        assertEquals(expectedMaxOrder, maxOrder);
        verify(crudTodoRepository).findMaxOrder();
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @Test
    void returnZeroMaxOrderWhenNoTodosAreFound() {
        when(crudTodoRepository.findMaxOrder()).thenReturn(Optional.empty());
        final int maxOrder = jpaTodoRepository.getMaxOrder();

        assertEquals(0, maxOrder);
        verify(crudTodoRepository).findMaxOrder();
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @Test
    void returnListOfAllTodos() {
        final UUID id1 = UUID.randomUUID();
        final UUID id2 = UUID.randomUUID();
        final UUID id3 = UUID.randomUUID();

        final List<TodoEntity> entities = List.of(
                new TodoEntity(id1, "test1", 0, false),
                new TodoEntity(id2, "test2", 1, false),
                new TodoEntity(id3, "test3", 2, false)
        );
        final List<Todo> expectedTodos = entities.stream()
                .map(todoEntity -> new Todo(
                        todoEntity.getId(),
                        todoEntity.getTitle(),
                        todoEntity.getOrder(),
                        todoEntity.isCompleted()
                    )
                )
                .toList();

        when(crudTodoRepository.findAll()).thenReturn(entities);

        final List<Todo> todos = jpaTodoRepository.findAll();

        assertEquals(expectedTodos, todos);

        verify(crudTodoRepository).findAll();
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @Test
    void updateTodo() {
        final Todo todo = new Todo(UUID.randomUUID(), "test", 0, false);
        final TodoEntity entity = new TodoEntity(todo.id(), todo.title(), todo.order(), todo.completed());
        when(crudTodoRepository.save(any())).thenReturn(entity);

        final Todo result = jpaTodoRepository.update(todo);

        assertEquals(todo, result);
        verify(crudTodoRepository).save(entity);
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @Test
    void delete() {
        final UUID id = UUID.randomUUID();
        jpaTodoRepository.delete(id);
        verify(crudTodoRepository).deleteById(id);
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @ParameterizedTest
    @CsvSource({"false", "true"})
    void deleteByCompleted(boolean completed) {
        jpaTodoRepository.deleteByCompleted(completed);
        verify(crudTodoRepository).deleteByCompleted(completed);
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @Test
    void deleteAll() {
        jpaTodoRepository.deleteAll();
        verify(crudTodoRepository).deleteAll();
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @ParameterizedTest
    @CsvSource({"false", "true"})
    void existsById(boolean expectedResult) {
        final UUID id = UUID.randomUUID();
        when(crudTodoRepository.existsById(any())).thenReturn(expectedResult);

        final boolean result = jpaTodoRepository.existsById(id);

        assertEquals(expectedResult, result);

        verify(crudTodoRepository).existsById(id);
        verifyNoMoreInteractions(crudTodoRepository);
    }

    @Test
    void countByOrder() {
        final int order = 0;
        when(crudTodoRepository.countByOrder(order)).thenReturn(10);

        final int result = jpaTodoRepository.countByOrder(order);

        assertEquals(10, result);
        verify(crudTodoRepository).countByOrder(order);
        verifyNoMoreInteractions(crudTodoRepository);
    }
}