package com.alexleko.barberstime.test.repository;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.repositories.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.alexleko.barberstime.test.builders.entity.CategoryBuilder.mockCategory;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CategoryRepository categoryRepository;


    @Test
    @DisplayName("Deve inserir uma nova Categoria")
    public void shouldCreateNewCategory() {

        Category category = mockCategory().build();

        Category categorySaved = categoryRepository.save(category);

        assertThat(categorySaved).isNotNull();
        assertThat(categorySaved.getId()).isNotNull().isGreaterThan(0);
        assertThat(categorySaved.getDescription()).isNotEmpty().isEqualTo(category.getDescription());
    }

    @Test
    @DisplayName("Deve retornar TRUE quando existir uma Categoria com a description informada")
    public void shouldTrueReturnWhenCategoryExists() {

        Category category = mockCategory().build();
        entityManager.persist(category);

        boolean exists = categoryRepository.existsByDescription(category.getDescription());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar FALSE quando NÃO existir uma Categoria com a description informada")
    public void shouldFalseReturnWhenCategoryDoesntExist() {

        Category category = mockCategory().build();

        boolean exists = categoryRepository.existsByDescription(category.getDescription());

        assertThat(exists).isFalse();
    }


}
