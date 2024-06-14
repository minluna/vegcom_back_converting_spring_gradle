package com.example.springboot_gradle.exception;

import com.example.springboot_gradle.controller.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 409
    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<Result> ConflictException(ConflictException ex) {
        log.warn("Conflict Error");

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Result.res(HttpStatus.CONFLICT.toString(), ex.getMessage()));
    }

    // 403
    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Result> UnauthorizedException(UnauthorizedException ex) {
        log.warn("Unauthorized Error");

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.res(HttpStatus.FORBIDDEN.toString(), ex.getMessage()));
    }

    // Validation Error
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Result> MethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation Error");

        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.res(HttpStatus.BAD_REQUEST.toString(), errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleOtherExceptions(Exception ex) {
        log.warn("Internal Server Error");

        // 그 외의 모든 예외는 "서버 에러가 발생했습니다." 메시지 반환
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.res(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage()));
    }
}
