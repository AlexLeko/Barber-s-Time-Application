package com.alexleko.barberstime.services;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;

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

}
