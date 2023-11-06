package io.smanicome.todoback.data.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CrudTodoRepository extends CrudRepository<TodoEntity, UUID> {
    @Query(value = "SELECT MAX(t.order) FROM TodoEntity t")
    Optional<Integer> findMaxOrder();

    int countByOrder(int order);

    int countByTitle(String title);

    @Transactional
    void deleteByCompleted(boolean completed);
}
