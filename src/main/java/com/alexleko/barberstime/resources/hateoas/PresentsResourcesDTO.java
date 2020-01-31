package com.alexleko.barberstime.resources.hateoas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class PresentsResourcesDTO extends RepresentationModel<PresentsResourcesDTO> {

    private final String content;

    @JsonCreator
    public PresentsResourcesDTO(@JsonProperty("content") String content) {
        this.content = content;
    }

}
