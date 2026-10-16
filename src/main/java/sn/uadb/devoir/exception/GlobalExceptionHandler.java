package sn.uadb.devoir.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, HttpHeaders.EMPTY, exception.getMessage(), Map.of());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return response(status, headers, "Les données fournies sont invalides.", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        return response(HttpStatus.BAD_REQUEST, HttpHeaders.EMPTY, "Les paramètres fournis sont invalides.", errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException exception) {
        return response(HttpStatus.BAD_REQUEST, HttpHeaders.EMPTY, exception.getMessage(), Map.of());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return response(status, headers,
                "Le corps JSON est invalide. Vérifiez les types des champs : la quantité doit être un entier.", Map.of());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String message = "Le paramètre '" + exception.getParameterName() + "' est obligatoire.";
        return response(status, headers, message, Map.of(exception.getParameterName(), "Paramètre obligatoire."));
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (exception instanceof MethodArgumentTypeMismatchException mismatch) {
            String message = "Le paramètre '" + mismatch.getName() + "' a un type invalide.";
            return response(status, headers, message, Map.of(mismatch.getName(), "Type de valeur invalide."));
        }
        return response(status, headers, "Un paramètre a un type invalide.", Map.of());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return response(status, headers, "La méthode HTTP '" + exception.getMethod() + "' n'est pas autorisée.", Map.of());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return response(status, headers, "Le type de contenu n'est pas pris en charge. Utilisez application/json.", Map.of());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = switch (status.value()) {
            case 400 -> "La requête est invalide.";
            case 404 -> "La ressource demandée est introuvable.";
            case 406 -> "Le format de réponse demandé n'est pas pris en charge.";
            default -> "La requête n'a pas pu être traitée.";
        };
        return super.handleExceptionInternal(exception, apiError(status, message, Map.of()), headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception exception) {
        logger.error("Erreur inattendue lors du traitement de la requête.", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY,
                "Une erreur interne est survenue.", Map.of());
    }

    private ResponseEntity<Object> response(
            HttpStatusCode status, HttpHeaders headers, String message, Map<String, String> validationErrors) {
        return new ResponseEntity<>(apiError(status, message, validationErrors), headers, status);
    }

    private ApiError apiError(HttpStatusCode status, String message, Map<String, String> validationErrors) {
        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                httpStatus == null ? "HTTP " + status.value() : httpStatus.getReasonPhrase(),
                message,
                validationErrors);
        return body;
    }
}
