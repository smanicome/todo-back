package io.smanicome.todoback.web.request;


import jakarta.validation.constraints.NotBlank;

public record TodoCreationRequest(@NotBlank String title) {}
