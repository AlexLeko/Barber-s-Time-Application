package com.alexleko.barberstime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Header;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableSwagger2WebMvc
@Import(SpringDataRestConfiguration.class)
public class SwaggerConfig {

    // Api Documentation Info
    private static final String RESOURCES = "com.alexleko.barberstime.resources";

    private static final String INFO_TITLE = "Api Documentation Barber's Time";
    private static final String INFO_DESCRIPTION = "Barber's Time store management API";
    private static final String INFO_API_VERSION = "1.0";

    // Contacts Info
    private static final String INFO_CONTACT_NAME = "Alex DS";
    private static final String INFO_CONTACT_URL = "https://github.com/AlexLeko";
    private static final String INFO_CONTACT_MAIL = "lk.alexds@gmail.com";

    // Global Messages
    private static final String MSG_UPDATE = "Updated";
    private static final String MSG_DELETE = "Deleted";
    private static final String MSG_NOT_AUTHORIZED = "Access Not authorized";
    private static final String MSG_NOT_FOUND = "Not Found";
    private static final String MSG_VALIDATION_ERROR = "Validation Error";
    private static final String MSG_UNEXPECTED_ERROR = "Sorry! Unexpected Error";

    // Header Location Message  - Post
    private static final String POST_HEADER_LOCATION = "location";
    private static final String POST_HEADER_NEW_URI = "New resource URI";
    private static final String POST_HEADER_MESSAGE = "Resource Criated";

    // Global Messages Definition
    private final ResponseMessage statusCode_201 = customHeaderLocationMessage();
    private final ResponseMessage statusCode_204_PUT = globalMessage(HttpStatus.NO_CONTENT.value(), MSG_UPDATE);
    private final ResponseMessage statusCode_204_DEL = globalMessage(HttpStatus.NO_CONTENT.value(), MSG_DELETE);
    private final ResponseMessage statusCode_403 = globalMessage(HttpStatus.FORBIDDEN.value(), MSG_NOT_AUTHORIZED);
    private final ResponseMessage statusCode_404 = globalMessage(HttpStatus.NOT_FOUND.value(), MSG_NOT_FOUND);
    private final ResponseMessage statusCode_422 = globalMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), MSG_VALIDATION_ERROR);
    private final ResponseMessage statusCode_500 = globalMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), MSG_UNEXPECTED_ERROR);

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)

                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, Arrays.asList(statusCode_403, statusCode_404, statusCode_500))
                .globalResponseMessage(RequestMethod.POST, Arrays.asList(statusCode_201, statusCode_403, statusCode_422, statusCode_500))
                .globalResponseMessage(RequestMethod.PUT, Arrays.asList(statusCode_204_PUT, statusCode_403, statusCode_404, statusCode_422, statusCode_500))
                .globalResponseMessage(RequestMethod.DELETE, Arrays.asList(statusCode_204_DEL, statusCode_403, statusCode_404, statusCode_500))

                .apiInfo(apiInfo())
                .select()
                    .apis(RequestHandlerSelectors.basePackage(RESOURCES))
                    .paths(PathSelectors.any())
                    .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(INFO_TITLE)
                .description(INFO_DESCRIPTION)
                .contact(new Contact(INFO_CONTACT_NAME, INFO_CONTACT_URL, INFO_CONTACT_MAIL))
                .version(INFO_API_VERSION)
                .build();
    }

    private ResponseMessage globalMessage(int code, String msg) {
        return new ResponseMessageBuilder().code(code).message(msg).build();
    }

    private ResponseMessage customHeaderLocationMessage() {

        Map<String, Header> map = new HashMap<>();
        map.put(POST_HEADER_LOCATION,
                new Header(POST_HEADER_LOCATION,
                            POST_HEADER_NEW_URI,
                            new ModelRef("string")
                )
        );

        return new ResponseMessageBuilder()
                .code(HttpStatus.CREATED.value())
                .message(POST_HEADER_MESSAGE)
                .headersWithDescription(map)
                .build();
    }

}
