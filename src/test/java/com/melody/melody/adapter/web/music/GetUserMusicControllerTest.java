package com.melody.melody.adapter.web.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.dto.*;
import com.melody.melody.application.service.music.GetUserMusicService;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(controllers = GetUserMusicController.class)
@ContextConfiguration
class GetUserMusicControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetUserMusicService service;

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
    void getUsersMusic_Ok() throws Exception {
        User.UserId userId = new User.UserId(requesterUserId);
        MusicPublish publish = MusicPublish.Published;
        GetUserMusicService.Command command = new GetUserMusicService.Command(userId, publish, new PagingInfo(0, 4, MusicSort.newest));
        PagingResult<Music> pagingResult = new PagingResult<>(
                IntStream.range(0, 4)
                        .mapToObj(i -> TestMusicDomainGenerator.randomCompletionMusic(userId))
                        .collect(Collectors.toList()),
                4,
                20,
                5
        );

        when(service.execute(eq(command)))
                .thenReturn(new GetUserMusicService.Result(pagingResult));

        mockMvc.perform(
                get("/users/{userId}/music", requesterUserId)
                        .param("page", "0")
                        .param("size", "4")
                        .param("sorting", "newest")
                        .param("publish", "published")
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "get-users-music",
                                pathParameters(
                                        parameterWithName("userId").description("유저 아이디")
                                ),
                                requestParameters(
                                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                        parameterWithName("size").description("페이지 사이즈"),
                                        parameterWithName("sorting").description("정렬 기준 (음악 정렬 기준 코드)"),
                                        parameterWithName("publish").description("음악 게시 상태 (음악 게시 코드)")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("count").type(JsonFieldType.NUMBER).description("리스트 요소 개수"),
                                        fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("전체 요소 개수"),
                                        fieldWithPath("totalPage").type(JsonFieldType.NUMBER).description("전체 페이지"),

                                        fieldWithPath("list[].musicId").description("음악 아이디").type(JsonFieldType.NUMBER),
                                        fieldWithPath("list[].userId").description("유저 아이디").type(JsonFieldType.NUMBER),
                                        fieldWithPath("list[].emotion").description("감정(감정 분류 코드)").type(JsonFieldType.STRING),
                                        fieldWithPath("list[].explanation").description("이미지 설명").type(JsonFieldType.STRING),
                                        fieldWithPath("list[].imageUrl").description("이미지 URI").type(JsonFieldType.STRING),
                                        fieldWithPath("list[].musicUrl").description("음악 URI").type(JsonFieldType.STRING),
                                        fieldWithPath("list[].status").description("음악 상태(음악 상태 코드)").type(JsonFieldType.STRING)
                                )
                        )
                );
    }
}