package com.melody.melody.adapter.web.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.dto.*;
import com.melody.melody.application.service.post.GetUserPostService;
import com.melody.melody.application.service.post.TestPostDetailServiceGenerator;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(controllers = GetUserPostController.class)
@ContextConfiguration
class GetUserPostControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetUserPostService service;

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
        User.UserId userId = new User.UserId(requesterUserId);

        GetUserPostService.Command command = new GetUserPostService.Command(userId, Open.Everything, new PagingInfo(0, 4, PostSort.newest));
        PagingResult<PostDetail> pagingResult = new PagingResult<>(
                IntStream.range(0, 4)
                        .mapToObj(i -> TestPostDetailServiceGenerator.randomPostDetail(userId.getValue(), true, false))
                        .collect(Collectors.toList()),
                4,
                20,
                5
        );

        when(service.execute(eq(command)))
                .thenReturn(new GetUserPostService.Result(pagingResult));

        mockMvc.perform(
                get("/users/{userId}/posts", requesterUserId)
                        .param("page", "0")
                        .param("size", "4")
                        .param("sorting", "newest")
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "get-users-post",
                                pathParameters(
                                        parameterWithName("userId").description("유저 아이디")
                                ),
                                requestParameters(
                                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                        parameterWithName("size").description("페이지 사이즈"),
                                        parameterWithName("sorting").description("정렬 기준 (게시물 정렬 기준 코드)")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("count").type(JsonFieldType.NUMBER).description("리스트 요소 개수"),
                                        fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("전체 요소 개수"),
                                        fieldWithPath("totalPage").type(JsonFieldType.NUMBER).description("전체 페이지"),

                                        fieldWithPath("list[].postId").type(JsonFieldType.NUMBER).description("게시물 아이디"),
                                        fieldWithPath("list[].title").type(JsonFieldType.STRING).description("게시물 제목"),
                                        fieldWithPath("list[].content").type(JsonFieldType.STRING).description("게시물 내용"),
                                        fieldWithPath("list[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                        fieldWithPath("list[].open").type(JsonFieldType.STRING).description("공개 여부(y|n)"),
                                        fieldWithPath("list[].deleted").type(JsonFieldType.STRING).description("삭제 여부(y|n)"),
                                        fieldWithPath("list[].createdDate").type(JsonFieldType.STRING).description("게시물 생성 일자"),
                                        fieldWithPath("list[].music.musicId").type(JsonFieldType.NUMBER).description("음악 아이디"),
                                        fieldWithPath("list[].music.userId").type(JsonFieldType.NUMBER).description("음악 소유 유저 아이디"),
                                        fieldWithPath("list[].music.emotion").type(JsonFieldType.STRING).description("음악 감정(감정 분류 코드)"),
                                        fieldWithPath("list[].music.explanation").type(JsonFieldType.STRING).description("이미지 설명"),
                                        fieldWithPath("list[].music.imageUrl").type(JsonFieldType.STRING).description("이미지 URI"),
                                        fieldWithPath("list[].music.musicUrl").type(JsonFieldType.STRING).description("음악 URI"),
                                        fieldWithPath("list[].music.status").type(JsonFieldType.STRING).description("음악 상태(음악 상태 코드)"),
                                        fieldWithPath("list[].writer.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                        fieldWithPath("list[].writer.nickname").type(JsonFieldType.STRING).description("유저 닉네임")
                                )
                        )
                );
    }
}