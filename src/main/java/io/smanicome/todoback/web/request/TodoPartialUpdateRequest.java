package io.smanicome.todoback.web.request;

public record TodoPartialUpdateRequest(String title, Boolean completed, Integer order) {}
