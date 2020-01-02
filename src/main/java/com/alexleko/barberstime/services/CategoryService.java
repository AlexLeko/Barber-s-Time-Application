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

}
