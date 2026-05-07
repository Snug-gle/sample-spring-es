package io.elasticsearch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequestDto(
  @NotBlank String id, @NotBlank String name, @NotNull Long age, @NotNull Boolean isActive) {}
