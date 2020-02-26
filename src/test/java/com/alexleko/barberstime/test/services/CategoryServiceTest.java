package com.alexleko.barberstime.test.services;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.repositories.CategoryRepository;
import com.alexleko.barberstime.services.exceptions.BusinessException;
import com.alexleko.barberstime.services.exceptions.ServiceExceptionControl;
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
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class CategoryServiceTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("Deve criar uma nova categoria")
    public void insertCategoryTest() {

        Category category = mockCategory().build();
        Category saved = mockCategory().WithId(99L).build();

        Mockito.when(
                categoryRepository.existsByDescription(Mockito.anyString()))
                .thenReturn(false);

        Mockito.doReturn(saved)
                .when(categoryRepository)
                .save(category);

        Category savedCategory = categoryService.insert(category);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getDescription()).isEqualTo(category.getDescription());
    }

    @Test
    @DisplayName("Deve criar uma nova categoria mesmo quando o ID não for null")
    public void shouldCreateCategoryWhenCategoryIdNull() {

        Category catParam = mockCategory().WithId(22L).build();

        Mockito.doReturn(mockCategory().WithId(99L).build())
                .when(categoryRepository)
                .save(catParam);

        Category category = categoryService.insert(catParam);

        assertThat(catParam.getId()).isNull();
        assertThat(category.getId()).isNotNull();
        assertThat(category.getId()).isNotEqualTo(catParam.getId());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar uma categoria já existente")
    public void shouldThrowBusinessExceptionWhenTryInsertCategoryDuplicate() {
        Category category = mockCategory().build();

        Mockito.when(
                categoryRepository.existsByDescription(Mockito.anyString()))
                .thenReturn(true);

        Throwable exception = catchThrowable(() -> categoryService.insert(category));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ServiceExceptionControl.EXISTING_CATEGORY.getMessage());

        Mockito.verify(categoryRepository, Mockito.never()).save(category);
    }


}
