package com.alexleko.barberstime.dto;

import com.alexleko.barberstime.domain.Category;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Component
@Getter @Setter
@NoArgsConstructor
@ToString(exclude = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "The Name is required")
    @Length(min = 3, max = 80, message = "The Category Description must be between 3 and 80 characters")
    private String description;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.description = category.getDescription();
    }

}
