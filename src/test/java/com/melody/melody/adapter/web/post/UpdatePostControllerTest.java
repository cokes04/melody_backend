package com.melody.melody.adapter.web.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.adapter.web.post.request.UpdatePostRequest;
import com.melody.melody.application.service.post.UpdatePostService;
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
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(controllers = {UpdatePostController.class})
class UpdatePostControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UpdatePostService service;

    private final long requesterUserId = 5401;

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
    void update_200() throws Exception{
        long postId = 40210;

        UpdatePostRequest request = TestPostWebGenerator.randomUpdatePostRequest();

        Map<String, Object> requestMap = mapper.convertValue(request, Map.class);
        requestMap.put("open", "y");


        when(service.execute(any(UpdatePostService.Command.class)))
                .thenReturn(new UpdatePostService.Result(TestPostDomainGenerator.randomOpenPost()));


        this.mockMvc.perform(
                patch("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestMap))
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "update-post",
                                pathParameters(
                                        parameterWithName("postId").description("게시물 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시물 제목").optional(),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시물 내용").optional(),
                                        fieldWithPath("open").type(JsonFieldType.STRING).description("공개 여부").optional()
                                                .attributes(new Attributes.Attribute("openFormat", "y|n"))
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                                )
                        )
                );
    }
}