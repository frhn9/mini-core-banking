package org.fd.mcb.shared.response.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.fd.mcb.shared.response.attribute.ErrorAttribute;
import org.fd.mcb.shared.response.attribute.ResponseSchemaAttribute;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseError {

  @JsonProperty("responseSchema")
  private ResponseSchemaAttribute responseSchema;

  @JsonProperty("errors")
  private List<ErrorAttribute> errors;

}
