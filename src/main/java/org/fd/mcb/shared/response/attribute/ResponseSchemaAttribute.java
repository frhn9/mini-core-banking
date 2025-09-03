package org.fd.mcb.shared.response.attribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSchemaAttribute {

  @JsonProperty("responseCode")
  private String responseCode;

  @JsonProperty("responseMessage")
  private String responseMessage;

}
