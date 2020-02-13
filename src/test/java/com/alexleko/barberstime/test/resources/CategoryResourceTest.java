package com.alexleko.barberstime.test.resources;

import com.alexleko.barberstime.domain.Category;
import com.alexleko.barberstime.dto.CategoryDTO;
import com.alexleko.barberstime.resources.CategoryResource;
import com.alexleko.barberstime.services.CategoryService;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(CategoryResource.class)
@AutoConfigureMockMvc
public class CategoryResourceTest {

    private static final String CATEGORY_URN = "/v1/categories";

    @Autowired
    MockMvc mvc;

    @MockBean
    CategoryService categoryService;

    @Test
    @DisplayName("Should create a new category")
    public void createMockTest() throws Exception {
        // cenario
        String description = "teste";

        CategoryDTO dto = new CategoryDTO();
        dto.setDescription(description);
        dto.removeLinks();

        Category category = new Category();
        category.setId(99L);
        category.setDescription(description);

        BDDMockito.given(categoryService.convertFromDTO(Mockito.any(CategoryDTO.class)))
                .willReturn(new Category());

        BDDMockito.given(categoryService.insert(Mockito.any(Category.class)))
                .willReturn(category);

        String json = new ObjectMapper().writeValueAsString(dto);
//                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
//                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        // ação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CATEGORY_URN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificação
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(category.getId()))
                .andExpect(jsonPath("description").value(category.getDescription()));
    }

}
