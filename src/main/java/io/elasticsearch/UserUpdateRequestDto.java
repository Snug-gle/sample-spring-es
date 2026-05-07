package io.elasticsearch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequestDto(
  @NotBlank String name, @NotNull Long age, @NotNull Boolean isActive) {}
