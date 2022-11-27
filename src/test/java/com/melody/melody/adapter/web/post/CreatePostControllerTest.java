package com.melody.melody.adapter.web.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.web.post.request.CreatePostRequest;
import com.melody.melody.adapter.web.security.WithMockRequester;
import com.melody.melody.application.service.post.CreatePostService;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(controllers = CreatePostController.class)
@ContextConfiguration
class CreatePostControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreatePostService service;

    private final long requesterUserId = 10;

    @BeforeEach
    public void BeforeEach(WebApplicationContext webApplicationContext,
                           RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(new CharacterEncodingFilter("UTF-8", false, true))
                .build();
    }

    @Test
    @WithMockRequester(userId = requesterUserId)
    void create_Created() throws Exception {
        CreatePostRequest request = TestPostWebGenerator.randomCreatePostRequest();

        Map<String, Object> requestMap = objectMapper.convertValue(request, Map.class);
        requestMap.put("open", "y");

        when(service.execute(any(CreatePostService.Command.class)))
                .thenReturn(new CreatePostService.Result(TestPostDomainGenerator.randomOpenPost()));

        mockMvc.perform(
                post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMap))
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")

        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "create-post",
                                requestFields(
                                        fieldWithPath("musicId").type(JsonFieldType.NUMBER).description("음악 아이디"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시물 제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시물 내용"),
                                        fieldWithPath("open").type(JsonFieldType.STRING).description("공개 여부")

                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("게시물 URI")
                                )
                        )
                );
    }
}