package com.alexleko.barberstime.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor()
public enum ServiceExceptionControl {

    CANNOT_DELETE_CATEGORY_WITH_WORK("Cannot delete a Category with linked Works."),
    CATEGORY_NOT_FOUND("Category with ID: %s Not Found. Type: ");


    private String message;
}
