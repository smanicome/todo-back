package io.smanicome.todoback.data.jpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "TODOS")
public class TodoEntity {
    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @Column(name = "\"order\"")
    private int order;

    @Column(name = "completed")
    private boolean completed;

    public TodoEntity() {}

    @PersistenceCreator
    public TodoEntity(UUID id, String name, int order, boolean completed) {
        this.id = id;
        this.title = name;
        this.order = order;
        this.completed = completed;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getOrder() {
        return order;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoEntity that = (TodoEntity) o;
        return Objects.equals(id, that.id) && completed == that.completed && order == that.order && title.equals(that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, order, completed);
    }
}
