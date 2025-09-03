package org.fd.mcb.configs.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.shared.response.ResponseEnum;
import org.fd.mcb.shared.response.ResponseHelper;
import org.fd.mcb.shared.response.attribute.ErrorAttribute;
import org.fd.mcb.shared.response.template.ResponseError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

  private final ResponseHelper responseHelper;

  @ExceptionHandler(ModuleException.class)
  public ResponseEntity<ResponseError> handleException(ModuleException exception) {
    log.error("Error : {}", exception.getResponseEnum());
    return responseHelper.createResponseError(exception.getResponseEnum(), null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseError> handleException(
          Exception ex,
          HttpServletRequest request,
          HttpServletResponse response
  ) {
    Arrays.stream(ex.getStackTrace()).limit(5).forEach(logger::error);
    logger.error(ex.getMessage());
    return responseHelper.createResponseError(ResponseEnum.INTERNAL_SERVER_ERROR, null);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex,
          HttpHeaders headers,
          HttpStatusCode status,
          WebRequest request
  ) {
    List<ErrorAttribute> errors = new ArrayList<>();
    ex.getFieldErrors().forEach(fieldError ->
            errors.add(ErrorAttribute.builder().field(fieldError.getField()).message(fieldError.getDefaultMessage()).build())
    );

    return ResponseEntity.badRequest()
            .body(responseHelper.createResponseError(ResponseEnum.INVALID_PARAM, errors));
  }

}
