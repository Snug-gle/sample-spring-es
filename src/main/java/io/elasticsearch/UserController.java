package io.elasticsearch;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
  private final UserDocumentRepository userDocumentRepository;
  private final ElasticsearchOperations elasticsearchOperations;

  @PostMapping
  public UserDocument createUser(@Valid @RequestBody UserCreateRequestDto requestDto) {
    UserDocument user =
        new UserDocument(
            requestDto.id(), requestDto.name(), requestDto.age(), requestDto.isActive());

    return userDocumentRepository.save(user);
  }

  @GetMapping
  public Page<UserDocument> findUsers(@PageableDefault(size = 10) Pageable pageable) {
    return userDocumentRepository.findAll(pageable);
  }

  @GetMapping("/{id}")
  public UserDocument findUserById(@PathVariable String id) {
    return findUserOrThrow(id);
  }

  // upsert
  @PutMapping("/{id}")
  public UserDocument updateUser(
      @PathVariable String id, @Valid @RequestBody UserUpdateRequestDto requestDto) {
    if (!userDocumentRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
    }
    return userDocumentRepository.save(
        new UserDocument(id, requestDto.name(), requestDto.age(), requestDto.isActive()));
  }

  @PatchMapping("/{id}")
  public UserDocument patchUser(
      @PathVariable String id, @RequestBody UserPatchRequestDto requestDto) {
    if (!userDocumentRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
    }

    Map<String, Object> partial = new HashMap<>();
    if (requestDto.name() != null) partial.put("name", requestDto.name());
    if (requestDto.age() != null) partial.put("age", requestDto.age());
    if (requestDto.isActive() != null) partial.put("isActive", requestDto.isActive());

    if (!partial.isEmpty()) {
      UpdateQuery updateQuery =
          UpdateQuery.builder(id).withDocument(Document.from(partial)).build();
      elasticsearchOperations.update(updateQuery, IndexCoordinates.of(UserDocument.INDEX));
    }
    return findUserOrThrow(id);
  }

  @DeleteMapping("/{id}")
  public void deleteUserById(@PathVariable String id) {
    findUserOrThrow(id);
    userDocumentRepository.deleteById(id);
  }

  private UserDocument findUserOrThrow(String id) {
    return userDocumentRepository
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
  }
}
