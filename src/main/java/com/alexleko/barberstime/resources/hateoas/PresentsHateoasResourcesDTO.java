package com.alexleko.barberstime.resources.hateoas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class PresentsHateoasResourcesDTO extends RepresentationModel<PresentsHateoasResourcesDTO> {

    private final String content;

    @JsonCreator
    public PresentsHateoasResourcesDTO(@JsonProperty("content") String content) {
        this.content = content;
    }

}
