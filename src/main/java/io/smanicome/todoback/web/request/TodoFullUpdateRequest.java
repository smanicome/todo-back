package io.smanicome.todoback.web.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TodoFullUpdateRequest(@NotBlank String title, boolean completed, @Min(0) int order) {}
