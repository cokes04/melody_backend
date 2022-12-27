package com.melody.melody.adapter.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.web.security.TokenProvider;
import com.melody.melody.application.service.user.GetUserService;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = GetUserController.class)
class GetUserControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetUserService service;

    @MockBean
    private TokenProvider tokenProvider;

    @BeforeEach
    public void BeforeEach(WebApplicationContext webApplicationContext,
                           RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(new CharacterEncodingFilter("UTF-8", false, true))
                .build();
    }

    @Test
    void getUser_Ok() throws Exception{
        User user = TestUserDomainGenerator.randomUser();
        long userId = user.getId().getValue();
        GetUserService.Command command = new GetUserService.Command(userId);

        when(service.execute(command))
                .thenReturn(new GetUserService.Result(user));

        mockMvc.perform(
                get("/users/{userId}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")
        )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "get-user",
                                pathParameters(
                                        parameterWithName("userId").description("유저 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("userId").description("유저 아이디").type(JsonFieldType.NUMBER),
                                        fieldWithPath("nickName").description("닉네임").type(JsonFieldType.STRING),
                                        fieldWithPath("email").description("이메일").type(JsonFieldType.STRING)
                                )
                        )
                );
    }

}