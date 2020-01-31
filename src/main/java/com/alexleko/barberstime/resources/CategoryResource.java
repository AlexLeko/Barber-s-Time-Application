package com.alexleko.barberstime.resources;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;
import com.alexleko.barberstime.resources.hateoas.PresentsResourcesDTO;
import com.alexleko.barberstime.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/v1/categories", produces = { MediaType.APPLICATION_JSON_VALUE } )
public class CategoryResource {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<PresentsResourcesDTO> collectionOptions() {

        PresentsResourcesDTO presentation = new PresentsResourcesDTO(String.format(CategoryResource.class.getSimpleName()));
        presentation.add(linkTo(methodOn(CategoryResource.class)
                        .collectionOptions()).withSelfRel())
                    .add(linkTo(methodOn(CategoryResource.class)
                        .findAll()).withRel("all"))
                    .add(linkTo(methodOn(CategoryResource.class)
                        .findPage(null, null, null, null)).withRel("paged"));;

        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.OPTIONS)
                .body(presentation);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
    public ResponseEntity<PresentsResourcesDTO> singleOptions(@PathVariable Long id) {

        PresentsResourcesDTO presentation = new PresentsResourcesDTO(String.format(CategoryResource.class.getSimpleName()));
        presentation.add(linkTo(methodOn(CategoryResource.class)
                .singleOptions(id)).withSelfRel());

            Link findLink = linkTo(methodOn(CategoryResource.class)
                        .findById(id)).withRel("find");

            Link insertLink = linkTo(methodOn(CategoryResource.class)
                        .insert(new CategoryDTO())).withRel("insert");

            Link updateLink = linkTo(methodOn(CategoryResource.class)
                        .update(new CategoryDTO(), id)).withRel("update");

            presentation.add(Arrays.asList(findLink, insertLink, updateLink));

        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.OPTIONS)
                .body(presentation);
    }

    @PostMapping()
    public ResponseEntity<CategoryDTO> insert(@Valid @RequestBody final CategoryDTO paramCategoryDTO) {

        Category category = categoryService.convertFromDTO(paramCategoryDTO);
        category = categoryService.insert(category);

        CategoryDTO categoryDTO = new CategoryDTO(category);
        categoryDTO.add(
                linkTo(methodOn(CategoryResource.class)
                    .findById(categoryDTO.getId()))
                    .withSelfRel());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(categoryDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody CategoryDTO paramCategoryDTO, @PathVariable Long id) {

        Category category = categoryService.convertFromDTO(paramCategoryDTO);
        category.setId(id);
        categoryService.update(category);

        CategoryDTO categoryDTO = new CategoryDTO(category);
        categoryDTO.add(
                linkTo(methodOn(CategoryResource.class)
                    .findById(id))
                    .withSelfRel());

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

        categoryDTO.add(
                linkTo(methodOn(CategoryResource.class)
                    .findById(id))
                    .withSelfRel());

        return ResponseEntity.ok().body(categoryDTO);
    }

    @GetMapping()
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<Category> categories = categoryService.findAll();
        List<CategoryDTO> listDTO = categories.stream()
                                        .map(cat -> new CategoryDTO(cat))
                                        .collect(Collectors.toList());

        listDTO.stream().forEach(cat ->
                        cat.add(linkTo(methodOn(CategoryResource.class)
                                .findById(cat.getId()))
                                .withSelfRel()));

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

        pagedListDTO.stream().forEach(cat ->
                        cat.add(linkTo(methodOn(CategoryResource.class)
                            .findById(cat.getId()))
                            .withSelfRel()));

        return ResponseEntity.ok().body(pagedListDTO);
    }

}
