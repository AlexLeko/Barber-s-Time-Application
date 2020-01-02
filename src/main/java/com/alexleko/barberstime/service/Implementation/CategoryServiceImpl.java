package com.alexleko.barberstime.service.Implementation;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;
import com.alexleko.barberstime.repositorie.CategoryRepository;
import com.alexleko.barberstime.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    public Category convertFromDTO(CategoryDTO dto){
        return new Category(dto.getId(), dto.getDescription());
    }

    public Category insert(Category category) {
        category.setId(null);
        return categoryRepository.save(category);
    }

}
