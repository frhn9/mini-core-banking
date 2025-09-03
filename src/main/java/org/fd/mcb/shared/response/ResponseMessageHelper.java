package org.fd.mcb.shared.response;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.shared.response.attribute.ResponseSchemaAttribute;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ResponseMessageHelper {

  private final MessageSource responseMessageSource;

  public ResponseSchemaAttribute getResponseSchema(ResponseEnum responseEnum) {
    return ResponseSchemaAttribute.builder()
            .responseCode(responseEnum.getResponseCode())
            .responseMessage(getMessage(responseEnum.getResponseMessage()))
            .build();
  }

  private String getMessage(String code) {
    return responseMessageSource.getMessage(code, null, Locale.getDefault());
  }

}
