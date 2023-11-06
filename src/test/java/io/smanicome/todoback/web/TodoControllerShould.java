package io.smanicome.todoback.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smanicome.todoback.core.exception.TodoNotFoundException;
import io.smanicome.todoback.core.model.Todo;
import io.smanicome.todoback.core.service.TodoService;
import io.smanicome.todoback.web.request.TodoCreationRequest;
import io.smanicome.todoback.web.request.TodoFullUpdateRequest;
import io.smanicome.todoback.web.request.TodoPartialUpdateRequest;
import io.smanicome.todoback.web.response.TodoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Todos Controller")
class TodoControllerShould {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TodoService todosService;

    @DisplayName("should persist given todo")
    @Test
    void createTodo() throws Exception {
        final var id = UUID.randomUUID();
        final var todo = new Todo(id, "title", 1, false);
        final var expectedResponse = new TodoResponse(id, "title", 1, false, "http://localhost/todos/" + id);

        when(todosService.create(any())).thenReturn(todo);

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TodoCreationRequest("title")))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse), true));

        verify(todosService).create("title");
        verifyNoMoreInteractions(todosService);
    }

    @DisplayName("should not persist todo when given an invalid title")
    @ParameterizedTest(name = "title = \"{0}\"")
    @CsvSource(value = {
            "''",
            "'   '",
            "'\t'",
            "null"
    }, nullValues = "null")
    void notCreateATodo(String title) throws Exception {
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TodoCreationRequest(title)))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(todosService);
    }

    @DisplayName("should retrieve all todos")
    @Test
    void getAllTodos() throws Exception {
        final var id1 = UUID.randomUUID();
        final var id2 = UUID.randomUUID();
        final var id3 = UUID.randomUUID();
        final var id4 = UUID.randomUUID();

        final var todos = List.of(
                new Todo(id1, "title1", 1, false),
                new Todo(id2, "title2", 2, true),
                new Todo(id3, "title3", 3, false),
                new Todo(id4, "title4", 4, true)
        );

        final var expectedResponse = List.of(
                new TodoResponse(id1, "title1", 1, false, "http://localhost/todos/" + id1),
                new TodoResponse(id2, "title2", 2, true, "http://localhost/todos/" + id2),
                new TodoResponse(id3, "title3", 3, false, "http://localhost/todos/" + id3),
                new TodoResponse(id4, "title4", 4, true, "http://localhost/todos/" + id4)
        );

        when(todosService.findAll()).thenReturn(todos);
        mockMvc.perform(
                        get("/todos").accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse), true));

        verify(todosService).findAll();
        verifyNoMoreInteractions(todosService);
    }

    @DisplayName("should retrieve the todo matching the id")
    @Test
    void getSpecificTodo() throws Exception {
        final var id = UUID.randomUUID();
        final var todo = new Todo(id, "title", 1, false);
        final var expectedResponse = new TodoResponse(id, "title", 1, false, "http://localhost/todos/" + id);

        when(todosService.findById(any())).thenReturn(Optional.of(todo));

        mockMvc.perform(
                        get("/todos/" + id).accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse), true));

        verify(todosService).findById(id);
        verifyNoMoreInteractions(todosService);
    }

    @DisplayName("should return not found on unknown todo")
    @Test
    void returnNotFoundOnUnknownTodo() throws Exception {
        final var id = UUID.randomUUID();

        mockMvc.perform(
                        get("/todos/" + id).accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(todosService).findById(id);
        verifyNoMoreInteractions(todosService);
    }

    @DisplayName("should update every fields of the todo matching the id")
    @Test
    void updateSpecificTodoEntirely() throws Exception {
        final var id = UUID.randomUUID();
        final var todo = new Todo(id, "title", 0, false);
        final var updateRequest = new TodoFullUpdateRequest("title", false, 0);
        final var expectedResponse = new TodoResponse(id, "title", 0, false, "http://localhost/todos/" + id);

        when(todosService.update(any(), anyString(), anyInt(), anyBoolean())).thenReturn(todo);

        mockMvc.perform(
                        put("/todos/" + id)
                                .content(mapper.writeValueAsString(updateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse), true));

        verify(todosService).update(id, "title", 0, false);
        verifyNoMoreInteractions(todosService);
    }

    @DisplayName("should update only specified fields of the todo matching the id")
    @ParameterizedTest(name = "title = {0}, completed = {1}, order = {2}")
    @CsvSource(
            value = {
                    "null, null, null",
                    "'title2', null, null",
                    "'title2', true, null",
                    "'title2', null, 1",
                    "null, true, null",
                    "null, true, 1",
                    "null, null, 1",
                    "'title2', true, 1",
            },
            nullValues = "null"
    )
    void updateSpecificTodoPartially(String title, Boolean completed, Integer order) throws Exception {
        final var id = UUID.randomUUID();
        final var todo = new Todo(id, "title", 1, false);
        final var updatedTodo = new Todo(
                id,
                Objects.requireNonNullElse(title, todo.title()),
                Objects.requireNonNullElse(order, todo.order()),
                Objects.requireNonNullElse(completed, todo.completed())
        );

        final var partialUpdateRequest = new TodoPartialUpdateRequest(
                title,
                completed,
                order
        );

        final var expectedResponse = new TodoResponse(
                id,
                updatedTodo.title(),
                updatedTodo.order(),
                updatedTodo.completed(),
                "http://localhost/todos/" + id
        );

        when(todosService.update(any(), any(), any(), any())).thenReturn(updatedTodo);

        mockMvc.perform(
                        patch("/todos/" + id)
                                .content(mapper.writeValueAsString(partialUpdateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse), true));

        verify(todosService).update(id, title, order, completed);
        verifyNoMoreInteractions(todosService);
    }

    @DisplayName("should return not found when updating unknown todo")
    @Test
    void returnNotFoundWhenUpdatingUnknownTodo() throws Exception {
        final var id = UUID.randomUUID();
        final var updateRequest = new TodoFullUpdateRequest("title", false, 0);

        when(todosService.update(any(), any(), any(), any())).thenThrow(new TodoNotFoundException());

        mockMvc.perform(
                        put("/todos/" + id)
                                .content(mapper.writeValueAsString(updateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(todosService).update(id, "title", 0, false);
        verifyNoMoreInteractions(todosService);
    }

    @DisplayName("should delete the todo matching the id")
    @Test
    void deleteSpecificTodo() throws Exception {
        final var id = UUID.randomUUID();

        mockMvc.perform(delete("/todos/" + id)).andExpect(status().isNoContent());

        verify(todosService).deleteById(id);
        verifyNoMoreInteractions(todosService);
    }

    @DisplayName("should return not found when deleting unknown todo")
    @Test
    void returnNotFoundWhenDeletingUnknownTodo() throws Exception {
        final var id = UUID.randomUUID();

        doThrow(TodoNotFoundException.class).when(todosService).deleteById(any());

        mockMvc.perform(delete("/todos/" + id)).andExpect(status().isNotFound());

        verify(todosService).deleteById(id);
        verifyNoMoreInteractions(todosService);
    }

    @DisplayName("should delete all todos")
    @Test
    void deleteAllTodos() throws Exception {
        mockMvc.perform(delete("/todos")).andExpect(status().isNoContent());

        verify(todosService).deleteAll();
        verifyNoMoreInteractions(todosService);
    }
}