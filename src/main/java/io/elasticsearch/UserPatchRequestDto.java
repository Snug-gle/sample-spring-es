package io.elasticsearch;

public record UserPatchRequestDto(String name, Long age, Boolean isActive) {}
