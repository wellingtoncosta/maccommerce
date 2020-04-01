package br.com.maccommerce.inventoryservice.app.web.handler;

import br.com.maccommerce.inventoryservice.domain.exception.ApiException;
import br.com.maccommerce.inventoryservice.domain.exception.ApiException.ApiExceptionType;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> handleGenericException(Exception e) {
        ApiExceptionResponse response = new ApiExceptionResponse();
        response.setType(ApiExceptionType.INTERNAL_SERVER_ERROR.toString());
        response.setMessage(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionResponse> handleApiException(ApiException e) {
        ApiExceptionResponse response = new ApiExceptionResponse();
        response.setType(e.type.toString());
        response.setMessage(e.message);

        if(e.type.equals(ApiExceptionType.NOT_FOUND)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    public static @Data class ApiExceptionResponse {

        private String type;
        private String message;

    }

}
