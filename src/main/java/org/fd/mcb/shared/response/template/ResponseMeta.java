package org.fd.mcb.shared.response.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.fd.mcb.shared.response.attribute.MetaAttribute;
import org.fd.mcb.shared.response.attribute.ResponseSchemaAttribute;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMeta<T> {

	@JsonProperty("responseSchema")
	private ResponseSchemaAttribute responseSchema;

	@JsonProperty("meta")
	private MetaAttribute meta;

	@JsonProperty("data")
	private List<T> data;

}