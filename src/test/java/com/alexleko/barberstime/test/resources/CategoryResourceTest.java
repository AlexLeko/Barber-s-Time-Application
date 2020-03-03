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

import java.util.List;

import static com.alexleko.barberstime.test.builders.dto.CategoryDTOBuilder.mockCategoryDTO;
import static com.alexleko.barberstime.test.builders.entity.CategoryBuilder.*;
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
    private static final String EXCEPTION_PATH = "path";
    private static final String EXCEPTION_NOT_FOUND = "Data Not Found";

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

        String hateoasHref = LOCALHOST.concat(CATEGORY_URN + "/" + category.getId().toString());

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
                .andExpect(jsonPath(HATEOAS_SELF_HREF, is(hateoasHref)));
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
                .andExpect(jsonPath(EXCEPTION_MESSAGE).value(mensagem))
                .andExpect(jsonPath(EXCEPTION_PATH).value(CATEGORY_URN));
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
                categoryService.findById(anyLong()))
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
        String urn = CATEGORY_URN.concat("/" + id);

        ObjectNotFoundException exception = new ObjectNotFoundException(
                String.format(ServiceExceptionControl.CATEGORY_NOT_FOUND.getMessage(), id));

        String messageNotFound = String.format(ServiceExceptionControl.CATEGORY_NOT_FOUND.getMessage(), id);

        BDDMockito.given(
                categoryService.findById(anyLong()))
                .willThrow(exception);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(urn)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(EXCEPTION_ERROR).value(EXCEPTION_NOT_FOUND))
                .andExpect(jsonPath(EXCEPTION_MESSAGE).value(messageNotFound))
                .andExpect(jsonPath(EXCEPTION_PATH).value(urn));
    }


    /*[
        {
            "id": 1,
                "description": "leko",
                "links": [
                    {
                        "rel": "find-one",
                            "href": "http://localhost:8080/v1/categories/1"
                    }
                ]
        }, ... continua 2x...
    */
    @Test
    @DisplayName("Deve retornar uma lista com todas as Categorias")
    public void shouldRetrieveAllCategories() throws Exception {
        List<Category> list = mockCategoryList().buildList();
        String urn = CATEGORY_URN.concat("/");

        BDDMockito.given(
                categoryService.findAll())
                .willReturn(list);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(urn)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is(list.get(0).getDescription())))
                .andExpect(jsonPath("$[0].links[0].rel", is("find-one")))
                .andExpect(jsonPath("$[0].links[0].href", is(LOCALHOST.concat(urn + 1L))))

                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is(list.get(1).getDescription())))
                .andExpect(jsonPath("$[1].links[0].rel", is("find-one")))
                .andExpect(jsonPath("$[1].links[0].href", is(LOCALHOST.concat(urn + 2L))))

                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].description", is(list.get(2).getDescription())))
                .andExpect(jsonPath("$[2].links[0].rel", is("find-one")))
                .andExpect(jsonPath("$[2].links[0].href", is(LOCALHOST.concat(urn + 3L))));
    }

    // []
    @Test
    @DisplayName("Deve retornar uma lista vazia quando não haver Categorias no banco. ")
    public void shouldRetrieveEmptyArrayWhenThereAreNoCategories() throws Exception {

        List<Category> list = mockCategoryListEmpty().buildList();

        BDDMockito.given(
                categoryService.findAll())
                .willReturn(list);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CATEGORY_URN.concat("/"))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Deve retornar uma lista de Categorias paginada ")
    public void shouldRetrieveCategoriesListPaginated() throws Exception {
        // todo: implementar test de Categoria com paginação.
    }

    @Test
    @DisplayName("Deve excluir a Categoria informada")
    public void shouldDeleteCategory() throws Exception {
        Long id = 99L;

        BDDMockito.doNothing()
                    .when(categoryService)
                    .delete(anyLong());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(CATEGORY_URN.concat("/" + id));

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    /*{
        "timeStamp": 1583187917127,
        "status": 404,
        "error": "Data Not Found",
        "message": "Category with ID: 99 Not Found.",
        "path": "/v1/categories/99"
      }
    */
    @Test
    @DisplayName("Deve lançar erro quando a Categoria informada não existir.")
    public void shouldThrowExceptionWhenNotFoundCategory() throws Exception {
        Long id = 99L;
        String urn = CATEGORY_URN.concat("/" + id);

        ObjectNotFoundException exception = new ObjectNotFoundException(
                String.format(ServiceExceptionControl.CATEGORY_NOT_FOUND.getMessage(), id));
        String exceptionMessage = String.format(ServiceExceptionControl.CATEGORY_NOT_FOUND.getMessage(), id);

        BDDMockito.doThrow(exception)
                .when(categoryService).delete(anyLong());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(urn);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(EXCEPTION_ERROR).value(EXCEPTION_NOT_FOUND))
                .andExpect(jsonPath(EXCEPTION_MESSAGE).value(exceptionMessage))
                .andExpect(jsonPath(EXCEPTION_PATH).value(urn));
    }

    /*{
        "timestamp": "02-03-2020 08:56:57",
        "status": 405,
        "error": "Request Not Allowed",
        "message": "Request method 'DELETE' not supported",
        "path": "/v1/categories/"
      }
    */
    @Test
    @DisplayName("Deve lançar erro quando não informar o ID da Categoria.")
    public void shouldThrowExceptionWhenNotSendCategoryID() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(CATEGORY_URN);

        mvc.perform(request)
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath(EXCEPTION_ERROR).value("Request Not Allowed"))
                .andExpect(jsonPath(EXCEPTION_MESSAGE).value("Request method 'DELETE' not supported"))
                .andExpect(jsonPath(EXCEPTION_PATH).value(CATEGORY_URN));
    }

    /* {
            "content": "CategoryResource",
            "_links": {
                "self": {
                    "href": "http://localhost:8080/v1/categories/99"
                }
            }
        }
    */
    @Test
    @DisplayName("Deve Atualizar a Categoria")
    public void shouldUpdateCategory() throws Exception {
        Long id = 99L;
        CategoryDTO dtoParam = mockCategoryDTO().WithDescription("Novo").build();
        Category category = mockCategory().build();

        String json = new ObjectMapper().writeValueAsString(dtoParam);

        String url = LOCALHOST.concat(CATEGORY_URN + "/" + id.toString());

        BDDMockito.given(
                categoryService.convertFromDTO(Mockito.any(CategoryDTO.class)))
                .willReturn(category);

        BDDMockito.given(
                categoryService.update(Mockito.any(Category.class)))
                .willReturn(category);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(CATEGORY_URN.concat("/" + id))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath(HATEOAS_SELF_HREF, is(url)));

    }

    /* {
            "timestamp": "03-03-2020 07:37:30",
            "status": 404,
            "error": "Data Not Found",
            "message": "Category with ID: 99 Not Found.",
            "path": "/v1/categories/99"
       }
    */
    @Test
    @DisplayName("Deve lançar erro ao tentar Atualizar uma Categoria não existente")
    public void shouldTHrowExceptionWhenTryUpdateCategoryNotFound() throws Exception {
        Long id = 99L;
        CategoryDTO dtoParam = mockCategoryDTO().WithDescription("Novo").build();
        Category category = mockCategory().build();

        String urn = CATEGORY_URN.concat("/" + id);
        String json = new ObjectMapper().writeValueAsString(dtoParam);

        ObjectNotFoundException exception = new ObjectNotFoundException(
                String.format(ServiceExceptionControl.CATEGORY_NOT_FOUND.getMessage(), id));

        String messageNotFound = String.format(ServiceExceptionControl.CATEGORY_NOT_FOUND.getMessage(), id);

        BDDMockito.given(
                categoryService.convertFromDTO(Mockito.any(CategoryDTO.class)))
                .willReturn(category);

        BDDMockito.doThrow(exception)
                .when(categoryService)
                .update(Mockito.any(Category.class));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(urn)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(EXCEPTION_ERROR).value(EXCEPTION_NOT_FOUND))
                .andExpect(jsonPath(EXCEPTION_MESSAGE).value(messageNotFound))
                .andExpect(jsonPath(EXCEPTION_PATH).value(urn));
    }

    /*{
        "timestamp": "03-03-2020 08:00:57",
        "status": 405,
        "error": "Request Not Allowed",
        "message": "Request method 'PUT' not supported",
        "path": "/v1/categories/"
      }
    */
    @Test
    @DisplayName("Deve lançar erro ao Atualizar sem informar o ID da Categoria.")
    public void shouldThrowExceptionInUpdateWhenUpdateNotSendCategoryID() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(CATEGORY_URN);

        mvc.perform(request)
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath(EXCEPTION_ERROR).value("Request Not Allowed"))
                .andExpect(jsonPath(EXCEPTION_MESSAGE).value("Request method 'PUT' not supported"))
                .andExpect(jsonPath(EXCEPTION_PATH).value(CATEGORY_URN));
    }

}
