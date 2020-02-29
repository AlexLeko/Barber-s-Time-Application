package com.alexleko.barberstime.test.resources;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;
import com.alexleko.barberstime.resources.CategoryResource;
import com.alexleko.barberstime.services.CategoryService;
import com.alexleko.barberstime.services.exceptions.BusinessException;
import com.alexleko.barberstime.services.exceptions.ObjectNotFoundException;
import com.alexleko.barberstime.services.exceptions.ServiceExceptionControl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.alexleko.barberstime.test.builders.dto.CategoryDTOBuilder.mockCategoryDTO;
import static com.alexleko.barberstime.test.builders.entity.CategoryBuilder.mockCategory;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(CategoryResource.class)
@AutoConfigureMockMvc
public class CategoryResourceTest {

    private static final String LOCALHOST = "http://localhost";
    private static final String CATEGORY_URN = "/v1/categories";

    private static final String CATEGORY_ID = "id";
    private static final String CATEGORY_DESCRIPTION = "description";
    private static final String EXCEPTION_ERRORS = "errors";
    private static final String EXCEPTION_ERROR = "error";
    private static final String EXCEPTION_MESSAGE = "message";

    private static final String HATEOAS_SELF_HREF = "_links.self.href";


    @Autowired
    MockMvc mvc;

    @MockBean
    CategoryService categoryService;


    /*{
        "id": 1,
        "description": "teste",
        "_links": {
            "self": {
                "href": "http://localhost:8080/v1/categories/1"
            }
        }
      }
    */
    @Test
    @DisplayName("Deve criar uma nova categoria")
    public void shouldCreateNewCategory() throws Exception {

        CategoryDTO dto = mockCategoryDTO().build();
        Category category = mockCategory().WithId(99L).build();

        BDDMockito.given(
                categoryService.convertFromDTO(Mockito.any(CategoryDTO.class)))
                .willReturn(new Category());

        BDDMockito.given(
                categoryService.insert(Mockito.any(Category.class)))
                .willReturn(category);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CATEGORY_URN)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath(CATEGORY_ID).value(category.getId()))
                .andExpect(jsonPath(CATEGORY_DESCRIPTION).value(category.getDescription()))
                .andExpect(jsonPath(HATEOAS_SELF_HREF, is(LOCALHOST.concat(CATEGORY_URN + "/" + category.getId().toString()))));
    }

    @Test
    @DisplayName("Deve lançar um erro quando o objeto da CategoriaDTO estiver NULL")
    public void shouldThrowExceptionWhenCategoryDTONull() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new CategoryDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CATEGORY_URN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath(EXCEPTION_ERRORS, hasSize(1)));

        verify(categoryService, never()).convertFromDTO(new CategoryDTO());
    }

    @Test
    @DisplayName("Deve lançar erro quando a descrição da Categoria for inválida")
    public void shouldThrowExceptionWhenCategoryDTODescriptionInvalid() throws Exception {
        CategoryDTO dto = mockCategoryDTO().WithDescription("lk").build();
        Category category = mockCategory().WithId(99L).build();

        String validationDescription = "The Category Description must be between 3 and 80 characters";

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CATEGORY_URN)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath(EXCEPTION_ERRORS).isArray())
                .andExpect(jsonPath(EXCEPTION_ERRORS, hasSize(1)))
                .andExpect(jsonPath("errors[0].message").value(validationDescription));

        verify(categoryService, never()).convertFromDTO(dto);
        verify(categoryService, never()).insert(category);
    }

    /* {
            "timeStamp": 1582742292340,
            "status": 400,
            "error": "Attempt insert duplicate",
            "message": "This category already exists",
            "path": "/v1/categories"
        }
    */
    @Test
    @DisplayName("Deve lançar um erro ao tentar cadastrar uma Categoria já existente")
    public void shouldThrowExceptionWhenTryInsertCategoryAlreadyExists() throws Exception {

        CategoryDTO dto = mockCategoryDTO().build();
        Category category = mockCategory().WithId(99L).build();

        String errorTitle = "Attempt insert duplicate";
        String mensagem = ServiceExceptionControl.EXISTING_CATEGORY.getMessage();

        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(
                categoryService.convertFromDTO(Mockito.any(CategoryDTO.class)))
                .willReturn(new Category());

        BDDMockito.given(
                categoryService.insert(Mockito.any(Category.class)))
                .willThrow(new BusinessException(mensagem));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CATEGORY_URN)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(EXCEPTION_ERROR).value(errorTitle))
                .andExpect(jsonPath(EXCEPTION_MESSAGE).value(mensagem));
    }


    /* {
            "id": 1,
            "description": "teste",
            "_links": {
                "self": {
                    "href": "http://localhost:8080/v1/categories/1"
                }
            }
        }
    */
    @Test
    @DisplayName("Deve recuperar uma Categoria pelo ID")
    public void shouldRetrieverCategoryById() throws Exception {

        Long id = 99L;
        Category category = mockCategory().WithId(id).build();

        BDDMockito.given(
                categoryService.findById(id))
                .willReturn(category);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CATEGORY_URN.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath(CATEGORY_ID).value(category.getId()))
                .andExpect(jsonPath(CATEGORY_DESCRIPTION).value(category.getDescription()))
                .andExpect(jsonPath(HATEOAS_SELF_HREF, is(LOCALHOST.concat(CATEGORY_URN + "/" + category.getId().toString()))));
    }

    /* {
        "timeStamp": 1582994995869,
        "status": 404,
        "error": "Data Not Found",
        "message": "Category with ID: 99 Not Found.",
        "path": "/v1/categories/99"
       }
    */
    @Test
    @DisplayName("Deve lançar erro quando a Categotia consultada não existir")
    public void shouldThrowExceptionWhenCategoryNotFound() throws Exception {
        Long id = 99L;

        BDDMockito.given(
                categoryService.findById(id))
                .willThrow(new ObjectNotFoundException(
                        String.format(ServiceExceptionControl.CATEGORY_NOT_FOUND.getMessage(), id)));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CATEGORY_URN.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(EXCEPTION_ERROR).value("Data Not Found"))
                .andExpect(jsonPath("message")
                        .value(String.format(ServiceExceptionControl.CATEGORY_NOT_FOUND.getMessage(), id)));
    }

}
