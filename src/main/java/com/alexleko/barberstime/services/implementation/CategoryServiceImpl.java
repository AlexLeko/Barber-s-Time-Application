package com.alexleko.barberstime.services.implementation;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;
import com.alexleko.barberstime.repositories.CategoryRepository;
import com.alexleko.barberstime.services.CategoryService;
import com.alexleko.barberstime.services.exceptions.BusinessException;
import com.alexleko.barberstime.services.exceptions.ObjectNotFoundException;
import com.alexleko.barberstime.services.exceptions.ServiceExceptionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    public Category convertFromDTO(CategoryDTO dto){
        return new Category(dto.getId(), dto.getDescription());
    }

    public Category insert(Category category) {
        if (categoryRepository.existsByDescription(category.getDescription())) {
            throw new BusinessException(ServiceExceptionControl.EXISTING_CATEGORY.getMessage());
        }

        category.setId(null);
        return categoryRepository.save(category);
    }

    public Category update(Category category) {
        if (categoryRepository.existsByDescription(category.getDescription())) {
            throw new BusinessException(ServiceExceptionControl.EXISTING_CATEGORY.getMessage());
        }

        Category newCategory = findById(category.getId());
        newCategory.setDescription(category.getDescription());

        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        findById(id);

        try {
            categoryRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    ServiceExceptionControl.CANNOT_DELETE_CATEGORY_WITH_WORK.getMessage()
            );
        }
    }

    public Category findById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);

        return category.orElseThrow(() -> new ObjectNotFoundException(
                String.format(ServiceExceptionControl.CATEGORY_NOT_FOUND.getMessage(), id)));
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Page<Category> findPaged(Integer page, Integer linesPerPage, String direction, String orderBy) {
        PageRequest pageRequest = PageRequest.of(   page,
                                                    linesPerPage,
                                                    Sort.Direction.valueOf(direction.toUpperCase()),
                                                    orderBy);

        return  categoryRepository.findAll(pageRequest);
    }

}
