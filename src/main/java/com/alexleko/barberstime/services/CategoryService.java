package com.alexleko.barberstime.services;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    /**
     * Performs a conversion from a transfer object to an entity.
     */
    Category convertFromDTO(CategoryDTO dto);

    /**
     * Performs persistence in the database of a new record.
     */
    Category insert(Category category);

    /**
     * Performs update in the database of a exist record.
     */
    Category update(Category category);

    /**
     * Performs delete in the database of a exist record, without linked works.
     */
    void delete(Long id);

    /**
     * Search the registration of a category by Id in the database.
     */
    Category findById(Long id);

    /**
     * Retrieve all category records in the database.
     */
    List<Category> findAll();

}
