package com.serviceplus.authorization.ExceptionHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = SPRuntimeError.class)
	public ResponseEntity<ErrorDetails> handler(SPRuntimeError  ex, HttpServletRequest request){
		ErrorDetails err = new ErrorDetails(ex.getMessage(),ex.getErrorCode(),ex.getErrorCode().value());
		return ResponseEntity.status(ex.getErrorCode()).body(err);
	}
}
