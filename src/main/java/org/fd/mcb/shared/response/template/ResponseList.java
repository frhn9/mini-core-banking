package org.fd.mcb.shared.response.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.fd.mcb.shared.response.attribute.ResponseSchemaAttribute;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseList<T> {

  @JsonProperty("responseSchema")
  private ResponseSchemaAttribute responseSchemaAttribute;

  @JsonProperty("data")
  private List<T> data;

}