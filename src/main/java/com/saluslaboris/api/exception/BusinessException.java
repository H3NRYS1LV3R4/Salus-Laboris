package com.saluslaboris.api.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
    public static BusinessException missing(String recurso) {
        return new BusinessException(HttpStatus.NOT_FOUND, recurso + " no encontrado");
    }
    public static BusinessException conflict(String mensaje) {
        return new BusinessException(HttpStatus.CONFLICT, mensaje);
    }
}
