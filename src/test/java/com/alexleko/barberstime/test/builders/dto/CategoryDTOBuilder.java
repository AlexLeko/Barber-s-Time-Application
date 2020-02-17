package com.alexleko.barberstime.test.builders.dto;

import com.alexleko.barberstime.dto.CategoryDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CategoryDTOBuilder {

    private static final String DESCRIPTION = "Teste";

    private CategoryDTO categoryDTO;

    public static CategoryDTOBuilder mockCategoryDTO(){
        CategoryDTOBuilder builder = new CategoryDTOBuilder();
        builder.categoryDTO = new CategoryDTO();
        builder.categoryDTO.setDescription(DESCRIPTION);
        return builder;
    }

    public CategoryDTO build(){
        return categoryDTO;
    }

    public CategoryDTOBuilder WithId(Long id){
        categoryDTO.setId(id);
        return this;
    }

    public CategoryDTOBuilder withEmptyDescription() {
        categoryDTO.setDescription("");
        return this;
    }

}
