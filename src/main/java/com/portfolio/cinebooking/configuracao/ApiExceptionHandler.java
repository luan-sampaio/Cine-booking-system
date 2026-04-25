package com.portfolio.cinebooking.configuracao;

import com.portfolio.cinebooking.dto.ApiErrorResponseDTO;
import com.portfolio.cinebooking.dto.ApiFieldErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {
        HttpStatus status = resolveHttpStatus(ex.getStatusCode());
        String mensagem = ex.getReason() != null ? ex.getReason() : "Erro na requisição";
        return ResponseEntity.status(status)
                .body(ApiErrorResponseDTO.of(status, mensagem, request.getRequestURI()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponseDTO.of(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciais inválidas",
                        request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<ApiFieldErrorDTO> errosDeCampo = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiFieldErrorDTO(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(ApiErrorResponseDTO.of(
                        HttpStatus.BAD_REQUEST,
                        "Dados da requisição são inválidos",
                        request.getRequestURI(),
                        errosDeCampo));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBindException(
            BindException ex,
            HttpServletRequest request) {
        List<ApiFieldErrorDTO> errosDeCampo = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiFieldErrorDTO(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(ApiErrorResponseDTO.of(
                        HttpStatus.BAD_REQUEST,
                        "Parâmetros da requisição são inválidos",
                        request.getRequestURI(),
                        errosDeCampo));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponseDTO.of(
                        HttpStatus.BAD_REQUEST,
                        "Parâmetros da requisição são inválidos",
                        request.getRequestURI(),
                        ex.getConstraintViolations().stream()
                                .map(violation -> new ApiFieldErrorDTO(
                                        violation.getPropertyPath().toString(),
                                        violation.getMessage()))
                                .toList()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponseDTO.of(
                        HttpStatus.BAD_REQUEST,
                        "Corpo da requisição inválido ou malformado",
                        request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponseDTO.of(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro interno do servidor",
                        request.getRequestURI()));
    }

    private HttpStatus resolveHttpStatus(HttpStatusCode statusCode) {
        return HttpStatus.resolve(statusCode.value()) != null
                ? HttpStatus.valueOf(statusCode.value())
                : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
