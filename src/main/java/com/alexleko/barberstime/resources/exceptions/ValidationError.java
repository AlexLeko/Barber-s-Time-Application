package com.alexleko.barberstime.resources.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationError extends StandardError {
    private static final long serialVersionUID = 1L;

    private List<FieldMessage>  errors = new ArrayList<>();

    public ValidationError(LocalDateTime timeStamp, Integer status, String error, String message, String path) {
        super(timeStamp, status, error, message, path);
    }

    public void addErrors(String field, String message) {
        errors.add(new FieldMessage(field, message));
    }

}
