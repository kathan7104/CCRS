package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = Controller.class)
public class GlobalWebExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalWebExceptionHandler.class);

    @ExceptionHandler({
            MaxUploadSizeExceededException.class,
            MultipartException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
            DataIntegrityViolationException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public String handleWebErrors(Exception ex,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        String message;
        if (ex instanceof MaxUploadSizeExceededException || ex instanceof MultipartException) {
            message = "Upload too large. Please use smaller files (max 20MB per file, 50MB total).";
        } else {
            message = ex.getMessage() == null || ex.getMessage().isBlank()
                    ? "Request failed. Please verify all form fields and try again."
                    : ex.getMessage();
        }

        redirectAttributes.addFlashAttribute("errorMessage", message);

        String uri = request.getRequestURI();
        if (uri != null && uri.matches("^/courses/\\d+/enroll$")) {
            return "redirect:" + uri;
        }
        if (uri != null && (uri.equals("/director/courses") || uri.matches("^/director/courses/\\d+$"))) {
            return "redirect:/director/courses/new";
        }
        return "redirect:/dashboard";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        log.error("Unexpected web error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        String message = buildSafeMessage(ex);
        redirectAttributes.addFlashAttribute("errorMessage", message);
        String uri = request.getRequestURI();
        if (uri != null && (uri.equals("/director/courses") || uri.matches("^/director/courses/\\d+$"))) {
            return "redirect:/director/courses/new";
        }
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/dashboard";
    }

    private String buildSafeMessage(Exception ex) {
        Throwable current = ex;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        String msg = current.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = ex.getMessage();
        }
        if (msg == null || msg.isBlank()) {
            return "Unexpected error occurred. Please verify all course fields and try again.";
        }
        return msg;
    }
}
