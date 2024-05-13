package com.transit.backend.exeptions.handler;

import com.transit.backend.exeptions.exeption.*;
import com.transit.backend.exeptions.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	public static final String TRACE = "trace";
	
	
	private boolean printStackTrace;
	
	@Override
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	                                                              HttpHeaders headers,
	                                                              HttpStatus status,
	                                                              WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error. Check 'errors' field for details.");
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return ResponseEntity.unprocessableEntity().body(errorResponse);
	}
	
	@Override
	public ResponseEntity<Object> handleExceptionInternal(
			Exception ex,
			Object body,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		
		return buildErrorResponse(ex, status, request);
	}
	
	@ExceptionHandler(NoSuchElementFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleNoSuchElementFoundException(NoSuchElementFoundException itemNotFoundException, WebRequest request) {
		log.error(itemNotFoundException.getStackTrace().toString());
		log.error(itemNotFoundException.getMessage());
		return buildErrorResponse(itemNotFoundException, HttpStatus.NOT_FOUND, request);
	}
	
	private ResponseEntity<Object> buildErrorResponse(Exception exception, HttpStatus httpStatus, WebRequest request) {
		return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
	}
	
	private ResponseEntity<Object> buildErrorResponse(Exception exception, String message, HttpStatus httpStatus, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
		if (printStackTrace && isTraceOn(request)) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
		}
		return ResponseEntity.status(httpStatus).body(errorResponse);
	}
	
	private boolean isTraceOn(WebRequest request) {
		String[] value = request.getParameterValues(TRACE);
		return Objects.nonNull(value)
				&& value.length > 0
				&& value[0].contentEquals("true");
	}
	
	@ExceptionHandler(RightsServiceNotAvailableException.class)
	@ResponseStatus(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED)
	public ResponseEntity<Object> handleRightsNotAvaiableException(RightsServiceNotAvailableException itemNotFoundException, WebRequest request) {
		return buildErrorResponse(itemNotFoundException, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, request);
	}
	
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleBadRequestException(BadRequestException badRequestException, WebRequest request) {
		return buildErrorResponse(badRequestException, HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(ValidationExeption.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleValidationException(ValidationExeption validationExeption, WebRequest request) {
		return buildErrorResponse(validationExeption, HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(NoSuchElementFoundOrDeleted.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleNoSUchElementFoundOrNotDeleted(NoSuchElementFoundOrDeleted noSuchElementFoundOrDeleted, WebRequest request) {
		return buildErrorResponse(noSuchElementFoundOrDeleted, HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(UnprocessableEntityExeption.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ResponseEntity<Object> handleUnprocessableException(UnprocessableEntityExeption unprocessableEntityExeption, WebRequest request) {
		unprocessableEntityExeption.printStackTrace();
		return buildErrorResponse(unprocessableEntityExeption, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}
	
	@ExceptionHandler(InvalidDataAccessApiUsageException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleRequestValidationError(InvalidDataAccessApiUsageException invalidDataAccessApiUsageException, WebRequest request) {
		return buildErrorResponse(invalidDataAccessApiUsageException, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}
	
	@ExceptionHandler(ForbiddenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<Object> forbiddenExceptionError(ForbiddenException forbiddenException, WebRequest request) {
		return buildErrorResponse(forbiddenException, HttpStatus.FORBIDDEN, request);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<Object> accessDeniedExceptionError(AccessDeniedException accessDeniedException, WebRequest request) {
		return buildErrorResponse(accessDeniedException, HttpStatus.UNAUTHORIZED, request);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
		log.error("Internal_Exception\n" + exception.getMessage() + "\n" + exception.getLocalizedMessage());
		exception.printStackTrace();
		return buildErrorResponse(exception, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
	
	private ResponseEntity<Object> buildErrorResponseHAETOAS(Exception exception, String message, HttpStatus httpStatus, WebRequest request) {
		Problem problem = Problem.create().withTitle(message).withDetail(exception.getStackTrace().toString());
		return ResponseEntity.status(httpStatus).body(problem);
	}
}