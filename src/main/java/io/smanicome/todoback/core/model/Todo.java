package io.smanicome.todoback.core.model;

import java.util.UUID;

public record Todo(UUID id, String title, int order, boolean completed) {
    public Todo withTitle(String title) {
        return new Todo(id, title, order, completed);
    }

    public Todo withOrder(int order) {
        return new Todo(id, title, order, completed);
    }

    public Todo withCompleted(boolean completed) {
        return new Todo(id, title, order, completed);
    }
}
