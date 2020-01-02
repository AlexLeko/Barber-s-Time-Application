package com.alexleko.barberstime.resource;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;
import com.alexleko.barberstime.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/v1/categories")
public class CategoryResource {

    @Autowired
    private CategoryService categoryService;



    @PostMapping()
    public ResponseEntity<Void> insert(@Valid @RequestBody CategoryDTO categoryDTO) {

        Category category = categoryService.convertFromDTO(categoryDTO);
        category = categoryService.insert(category);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(category.getId())
                .toUri();

        return ResponseEntity.created(uri).build();
    }





}
