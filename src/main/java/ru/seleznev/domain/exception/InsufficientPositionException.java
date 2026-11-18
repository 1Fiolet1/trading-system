package ru.seleznev.domain.exception;

public class InsufficientPositionException extends RuntimeException {

	public InsufficientPositionException(String message) {
		super(message);
	}
}
