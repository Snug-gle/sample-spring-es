package io.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Document(indexName = UserDocument.INDEX)
public class UserDocument {
  public static final String INDEX = "users";

  @Id private String id;

  @Field(type = FieldType.Keyword)
  private String name;

  @Field(type = FieldType.Long)
  private Long age;

  @Field(type = FieldType.Boolean)
  private Boolean isActive;

  public void updateUser(String name, Long age, Boolean isActive) {
    this.name = name;
    this.age = age;
    this.isActive = isActive;
  }
}
