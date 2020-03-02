package com.alexleko.barberstime.test.builders.entity;

import com.alexleko.barberstime.domain.Category;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class CategoryBuilder {

    private static final String DESCRIPTION = "Teste";

    private Category category;

    private Category cat1;
    private Category cat2;
    private Category cat3;
    private List<Category> categoryList = new ArrayList<>();

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

    public static CategoryBuilder mockCategoryList(){
        CategoryBuilder builder = new CategoryBuilder();
        builder.categoryList = new ArrayList<>();

        builder.cat1 = new Category();
        builder.cat1.setId(1L);
        builder.cat1.setDescription("Cat-001");
        builder.categoryList.add(builder.cat1);

        builder.cat2 = new Category();
        builder.cat2.setId(2L);
        builder.cat2.setDescription("Cat-002");
        builder.categoryList.add(builder.cat2);

        builder.cat3 = new Category();
        builder.cat3.setId(3L);
        builder.cat3.setDescription("Cat-003");
        builder.categoryList.add(builder.cat3);

        return builder;
    }

    public List<Category> buildList(){
        return categoryList;
    }
}
