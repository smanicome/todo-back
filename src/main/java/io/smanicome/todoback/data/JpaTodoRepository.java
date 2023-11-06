package io.smanicome.todoback.data;

import io.smanicome.todoback.core.model.Todo;
import io.smanicome.todoback.core.repository.TodoRepository;
import io.smanicome.todoback.data.jpa.CrudTodoRepository;
import io.smanicome.todoback.data.jpa.TodoEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JpaTodoRepository implements TodoRepository {
    private final CrudTodoRepository repository;

    public JpaTodoRepository(CrudTodoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Todo create(String title, int order) {
        TodoEntity entity = new TodoEntity();
        entity.setTitle(title);
        entity.setOrder(order);
        entity.setCompleted(false);

        entity = repository.save(entity);

        return mapEntity(entity);
    }

    @Override
    public Optional<Todo> findByID(UUID id) {
        return repository.findById(id).map(JpaTodoRepository::mapEntity);
    }

    @Override
    public int getMaxOrder() {
        return repository.findMaxOrder().orElse(0);
    }

    @Override
    public List<Todo> findAll() {
        final List<Todo> todos = new ArrayList<>();

        repository.findAll().forEach(todoEntity -> todos.add(mapEntity(todoEntity)));

        return Collections.unmodifiableList(todos);
    }

    @Override
    public Todo update(Todo todo) {
        final TodoEntity entity = mapTodo(todo);
        return mapEntity(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteByCompleted(boolean completed) {
        repository.deleteByCompleted(completed);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public int countByOrder(int order) {
        return repository.countByOrder(order);
    }

    @Override
    public int countByTitle(String title) {
        return repository.countByTitle(title);
    }

    private static Todo mapEntity(TodoEntity todoEntity) {
        return new Todo(
            todoEntity.getId(),
            todoEntity.getTitle(),
            todoEntity.getOrder(),
            todoEntity.isCompleted()
        );
    }

    private static TodoEntity mapTodo(Todo todo) {
        return new TodoEntity(
                todo.id(),
                todo.title(),
                todo.order(),
                todo.completed()
        );
    }
}
