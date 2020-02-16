package com.alexleko.barberstime.dto;

import com.alexleko.barberstime.domain.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@Component
@Getter @Setter
@NoArgsConstructor
@ToString(exclude = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(NON_NULL)
@JsonRootName(value = "category")
@Relation(collectionRelation = "categories", itemRelation = "category")
@ApiModel(description = "Category Features")
public class CategoryDTO extends RepresentationModel<CategoryDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String CATEGORY_DESCRIPTION_REQUIRED = "The description is required";
    private static final String CATEGORY_LENGTH_DESCRIPTION = "The Category Description must be between 3 and 80 characters";


    @EqualsAndHashCode.Include
    @ApiModelProperty( notes = "Database-generated id for Category" )
    private Long id;

    @NotNull(message = CATEGORY_DESCRIPTION_REQUIRED)
    @Length(min = 3, max = 80, message = CATEGORY_LENGTH_DESCRIPTION)
    @ApiModelProperty( notes = "This is the name of the category" )
    private String description;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.description = category.getDescription();
    }

}
