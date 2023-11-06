package io.smanicome.todoback.web.response;

import java.util.UUID;


public record TodoResponse(UUID id, String title, int order, boolean completed, String url) {
}
