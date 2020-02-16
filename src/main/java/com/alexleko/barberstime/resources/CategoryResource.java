package com.alexleko.barberstime.resources;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;
import com.alexleko.barberstime.resources.enumerators.ActionTypeFromRequest;
import com.alexleko.barberstime.resources.hateoas.PresentsHateoasResourcesDTO;
import com.alexleko.barberstime.services.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@RequestMapping(value = "/v1/categories")
public class CategoryResource {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "Information about category collection features",
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<PresentsHateoasResourcesDTO> collectionOptions() {

        PresentsHateoasResourcesDTO presentation =
                new PresentsHateoasResourcesDTO(String.format(CategoryResource.class.getSimpleName()));

        presentation
            .add(linkTo(methodOn(CategoryResource.class)
                .collectionOptions())
                .withSelfRel())
            .add(linkTo(methodOn(CategoryResource.class)
                .findAll())
                .withRel(ActionTypeFromRequest.FIND_All.getAction()))
            .add(linkTo(methodOn(CategoryResource.class)
                .findPage(null, null, null, null))
                .withRel(ActionTypeFromRequest.FIND_PAGED.getAction()));

        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.OPTIONS)
                .body(presentation);
    }

    @ApiOperation(value = "Information about category features",
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
    public ResponseEntity<PresentsHateoasResourcesDTO> singleOptions(
            @ApiParam(value = "Category ID to query the resources of a Category", required = true)
                @PathVariable Long id) {

        PresentsHateoasResourcesDTO presentation = new PresentsHateoasResourcesDTO(String.format(CategoryResource.class.getSimpleName()));
        presentation
            .add(linkTo(methodOn(CategoryResource.class)
                .singleOptions(id))
                .withSelfRel());

        Link findLink = linkTo(methodOn(CategoryResource.class)
                .findById(id))
                .withRel(ActionTypeFromRequest.FIND_BY_ID.getAction());

        Link insertLink = linkTo(methodOn(CategoryResource.class)
                .insert(new CategoryDTO()))
                .withRel(ActionTypeFromRequest.INSERT.getAction());

        Link updateLink = linkTo(methodOn(CategoryResource.class)
                .update(new CategoryDTO(), id))
                .withRel(ActionTypeFromRequest.UPDATE.getAction());

        Link deleteLink = linkTo(methodOn(CategoryResource.class)
                .delete(id))
                .withRel(ActionTypeFromRequest.DELETE.getAction());

        presentation.add(Arrays.asList(findLink, insertLink, updateLink, deleteLink));

        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.OPTIONS)
                .body(presentation);
    }

    @ApiOperation(value = "Add a new category",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE },
                    consumes = { MediaType.APPLICATION_JSON_VALUE }  )
    public ResponseEntity<CategoryDTO> insert(
            @ApiParam(value = "Data of a new Category that will be registered in the database", required = true)
                @Valid @RequestBody final CategoryDTO paramCategoryDTO) {

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

    @ApiOperation(value = "Update a category",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE },
                                    consumes = { MediaType.APPLICATION_JSON_VALUE } )
    public ResponseEntity<PresentsHateoasResourcesDTO> update(
            @ApiParam( value="Category data to be updated in the database", required = true)
                @Valid @RequestBody CategoryDTO paramCategoryDTO,
            @ApiParam( value="Category ID to be updated", required = true)
                @PathVariable Long id) {

        Category category = categoryService.convertFromDTO(paramCategoryDTO);
        category.setId(id);
        categoryService.update(category);

        PresentsHateoasResourcesDTO categoryDTO = new PresentsHateoasResourcesDTO(String.format(CategoryResource.class.getSimpleName()));
        categoryDTO.add(
                linkTo(methodOn(CategoryResource.class)
                    .findById(id))
                    .withSelfRel());

        return ResponseEntity.ok().body(categoryDTO);
    }

    @ApiOperation(value = "Delete a category", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Could not delete a category with works"),
            @ApiResponse(code = 404, message = "Invalid ID")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(
            @ApiParam( value="Category ID to be deleted", required = true)
                @PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Retrieve a category by id", produces = MediaType.APPLICATION_JSON_VALUE )
    @GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE } )
    public ResponseEntity<CategoryDTO> findById(
            @ApiParam(value = "Category ID to be retrieved", required = true)
                @PathVariable Long id) {
        Category category = categoryService.findById(id);
        CategoryDTO categoryDTO = new CategoryDTO(category);

        categoryDTO.add(
                linkTo(methodOn(CategoryResource.class)
                    .findById(id))
                    .withSelfRel());

        return ResponseEntity.ok().body(categoryDTO);
    }

    @ApiOperation(value = "Retrieve all categories",
                    notes = "Note on all categories search feature",
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE } )
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<Category> categories = categoryService.findAll();
        List<CategoryDTO> listDTO = categories.stream()
                                        .map(cat -> new CategoryDTO(cat))
                                        .collect(Collectors.toList());

        listDTO.stream()
                .forEach(cat ->
                    cat.add(linkTo(methodOn(CategoryResource.class)
                        .findById(cat.getId()))
                        .withRel(ActionTypeFromRequest.FIND_BY_ID.getAction())));

        return ResponseEntity.ok().body(listDTO);
    }

    @ApiOperation(value = "Retrieve categories with pagination", produces = MediaType.APPLICATION_JSON_VALUE )
    @GetMapping(value = "/page", produces = { MediaType.APPLICATION_JSON_VALUE } )
    public ResponseEntity<Page<CategoryDTO>> findPage(
            @ApiParam(value = "Pagination start page", required = false)
                @RequestParam(value = "page", defaultValue = "0") Integer page,
            @ApiParam(value = "Number of records listed on the pagination", required = false)
                @RequestParam(value = "linesPerPage", defaultValue = "3") Integer linesPerPage,
            @ApiParam(value = "Pagination ordering", required = false)
                @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @ApiParam(value = "Priority field for pagination ordering", required = false)
                @RequestParam(value = "orderBy", defaultValue = "description") String orderBy
    ) {

        Page<Category> pagedCategories = categoryService.findPaged(page, linesPerPage, direction, orderBy);
        Page<CategoryDTO> pagedListDTO = pagedCategories.map(cat -> new CategoryDTO(cat));

        pagedListDTO.stream()
                .forEach(cat ->
                    cat.add(linkTo(methodOn(CategoryResource.class)
                        .findById(cat.getId()))
                        .withRel(ActionTypeFromRequest.FIND_BY_ID.getAction())));

        return ResponseEntity.ok().body(pagedListDTO);
    }

}
