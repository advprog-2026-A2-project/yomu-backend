package id.ac.ui.cs.advprog.yomubackend.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        // Ini yang bakal muncul di frontend lo nanti
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}