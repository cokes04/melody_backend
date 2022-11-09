package com.melody.melody.adapter.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.web.user.request.CreateUserRequest;
import com.melody.melody.application.service.user.CreateUserService;
import com.melody.melody.application.service.user.TestUserServiceGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.melody.melody.adapter.web.user.TestUserWebGenerator.randomCreateUserRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = CreateUserContoller.class)
class CreateUserContollerTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateUserService service;

    @BeforeEach
    public void BeforeEach(WebApplicationContext webApplicationContext,
                           RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(new CharacterEncodingFilter("UTF-8", false, true))
                .build();
    }

    @Test
    void createUser_Ok() throws Exception{
        CreateUserRequest request = randomCreateUserRequest();
        CreateUserService.Command command = request.toCommand();
        CreateUserService.Result result = TestUserServiceGenerator.randomCreateUserResult();

        when(service.execute(eq(command)))
                .thenReturn(result);


        mockMvc.perform(
                post("/users")
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "create-user",
                                requestFields(
                                        fieldWithPath("lastName").description("성").type(JsonFieldType.STRING),
                                        fieldWithPath("firstName").description("이름").type(JsonFieldType.STRING),
                                        fieldWithPath("email").description("이메일").type(JsonFieldType.STRING),
                                        fieldWithPath("password").description("비밀번호").type(JsonFieldType.STRING)
                                )
                        )
                );

    }
}