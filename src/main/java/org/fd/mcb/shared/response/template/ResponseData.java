package org.fd.mcb.shared.response.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.fd.mcb.shared.response.attribute.ResponseSchemaAttribute;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData<T> {

  @JsonProperty("responseSchema")
  private ResponseSchemaAttribute responseSchema;

  @JsonProperty("data")
  private T data;

}