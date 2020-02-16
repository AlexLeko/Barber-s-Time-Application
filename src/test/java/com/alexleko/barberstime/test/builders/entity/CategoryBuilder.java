package com.alexleko.barberstime.test.builders.entity;

import com.alexleko.barberstime.domain.Category;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CategoryBuilder {

    private static final String DESCRIPTION = "Teste";

    private Category category;

    public static CategoryBuilder mockCategory(){
        CategoryBuilder builder = new CategoryBuilder();
        builder.category = new Category();
        builder.category.setDescription(DESCRIPTION);
        return builder;
    }

    public Category build(){
        return category;
    }

    public CategoryBuilder WithId(Long id){
        category.setId(id);
        return this;
    }

}
