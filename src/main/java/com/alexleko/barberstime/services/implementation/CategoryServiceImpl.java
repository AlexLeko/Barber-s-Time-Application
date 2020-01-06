package com.alexleko.barberstime.services.implementation;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;
import com.alexleko.barberstime.repositories.CategoryRepository;
import com.alexleko.barberstime.services.CategoryService;
import com.alexleko.barberstime.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("categoryService")
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

    public Category find(Long id) {
        Optional<Category> category = categoryRepository.findById(id);

        return category.orElseThrow(() -> new ObjectNotFoundException(
                "Category with ID: " + id + " Not Found. Type: " + Category.class.getName()));
    }

    public Category update(Category category) {
        Category newCategory = find(category.getId());
        newCategory.setDescription(category.getDescription());

        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        find(id);

        try {
            categoryRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete a Category with linked Works.");
        }
    }

}
