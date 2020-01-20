package com.alexleko.barberstime.resources;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;
import com.alexleko.barberstime.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long id) {

        Category category = categoryService.convertFromDTO(categoryDTO);
        category.setId(id);
        categoryService.update(category);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        CategoryDTO categoryDTO = new CategoryDTO(category);

        return ResponseEntity.ok().body(categoryDTO);
    }

    @GetMapping()
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<Category> categories = categoryService.findAll();
        List<CategoryDTO> listDTO = categories.stream()
                                        .map(cat -> new CategoryDTO(cat))
                                        .collect(Collectors.toList());

        return ResponseEntity.ok().body(listDTO);
    }


    @GetMapping(value = "/page")
    public ResponseEntity<Page<CategoryDTO>> findPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "3") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "description") String orderBy
    ) {

        Page<Category> pagedCategories = categoryService.findPaged(page, linesPerPage, direction, orderBy);
        Page<CategoryDTO> pagedListDTO = pagedCategories.map(cat -> new CategoryDTO(cat));

        return ResponseEntity.ok().body(pagedListDTO);
    }

}
