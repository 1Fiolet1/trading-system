package ru.seleznev.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.seleznev.app.dto.response.ErrorResponse;
import ru.seleznev.domain.exception.EntityNotFoundException;
import ru.seleznev.domain.exception.InsufficientFundsException;
import ru.seleznev.domain.exception.InsufficientPositionException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleEntityNotFound(EntityNotFoundException exception) {
		return new ErrorResponse(exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleIllegalArgument(IllegalArgumentException exception) {
		return new ErrorResponse(exception.getMessage());
	}

	@ExceptionHandler(InsufficientFundsException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInsufficientFunds(InsufficientFundsException exception) {
		return new ErrorResponse(exception.getMessage());
	}

	@ExceptionHandler(InsufficientPositionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInsufficientPosition(InsufficientPositionException exception) {
		return new ErrorResponse(exception.getMessage());
	}
}
