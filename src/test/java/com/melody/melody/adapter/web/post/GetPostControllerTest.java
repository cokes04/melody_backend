package com.melody.melody.adapter.web.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.service.post.GetPostService;
import com.melody.melody.application.service.post.TestPostDetailServiceGenerator;
import com.melody.melody.domain.model.Post;
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
import org.springframework.test.context.ContextConfiguration;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(controllers = GetPostController.class)
@ContextConfiguration
class GetPostControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetPostService service;

    private final long requesterUserId = 30;

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
    void getPost_Ok() throws Exception {
        long postId = 7540201;

        GetPostService.Command command = new GetPostService.Command(new Post.PostId(postId));
        when(service.execute(command))
                .thenReturn(new GetPostService.Result(TestPostDetailServiceGenerator.randomPostDetail()));

        mockMvc.perform(
                get("/posts/{postId}", postId)
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "get-post",
                                pathParameters(
                                        parameterWithName("postId").description("게시물 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시물 아이디"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시물 제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시물 내용"),
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                        fieldWithPath("open").type(JsonFieldType.STRING).description("공개 여부(y|n)"),
                                        fieldWithPath("deleted").type(JsonFieldType.STRING).description("삭제 여부(y|n)"),
                                        fieldWithPath("createdDate").type(JsonFieldType.STRING).description("게시물 생성 일자"),
                                        fieldWithPath("music.musicId").type(JsonFieldType.NUMBER).description("음악 아이디"),
                                        fieldWithPath("music.userId").type(JsonFieldType.NUMBER).description("음악 소유 유저 아이디"),
                                        fieldWithPath("music.emotion").type(JsonFieldType.STRING).description("음악 감정(감정 분류 코드)"),
                                        fieldWithPath("music.explanation").type(JsonFieldType.STRING).description("이미지 설명"),
                                        fieldWithPath("music.imageUrl").type(JsonFieldType.STRING).description("이미지 URI"),
                                        fieldWithPath("music.musicUrl").type(JsonFieldType.STRING).description("음악 URI"),
                                        fieldWithPath("music.status").type(JsonFieldType.STRING).description("음악 상태(음악 상태 코드)"),
                                        fieldWithPath("writer.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                        fieldWithPath("writer.nickname").type(JsonFieldType.STRING).description("유저 닉네임")
                                )
                        )
                );
    }
}