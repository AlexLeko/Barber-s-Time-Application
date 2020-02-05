package com.alexleko.barberstime.resources.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionTypeFromRequest {

    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete"),
    OPTIONS("options"),

    FIND("find"),
    FIND_BY_ID("find-one"),
    FIND_All("find-all"),
    FIND_PAGED("find-paged");


    private final String action;
}
