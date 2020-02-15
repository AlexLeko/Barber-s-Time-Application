package com.alexleko.barberstime.test.services;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.repositories.CategoryRepository;
import com.alexleko.barberstime.services.implementation.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.alexleko.barberstime.test.builders.entity.CategoryBuilder.mockCategory;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class CategoryServiceTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("Should create a new category")
    public void insertCategoryTest() {

        Category category = mockCategory().build();
        Category saved = mockCategory().WithId(99L).build();

        Mockito.doReturn(saved)
                .when(categoryRepository)
                .save(category);

        Category savedCategory = categoryService.insert(category);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getDescription()).isEqualTo(category.getDescription());
    }


}
