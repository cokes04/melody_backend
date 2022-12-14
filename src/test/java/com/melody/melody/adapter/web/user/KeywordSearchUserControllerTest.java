package com.melody.melody.adapter.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.web.security.TokenProvider;
import com.melody.melody.adapter.web.user.request.LoginRequest;
import com.melody.melody.adapter.web.user.request.UserPagingRequest;
import com.melody.melody.adapter.web.user.response.SearchedUserResponse;
import com.melody.melody.application.dto.*;
import com.melody.melody.application.service.authentication.AuthenticationService;
import com.melody.melody.application.service.post.GetUserPostService;
import com.melody.melody.application.service.post.TestPostDetailServiceGenerator;
import com.melody.melody.application.service.user.KeywordSearchUserService;
import com.melody.melody.application.service.user.TestSearchedUserServiceGenerator;
import com.melody.melody.config.JwtConfig;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = KeywordSearchUserController.class)
class KeywordSearchUserControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KeywordSearchUserService searchUserService;

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
    void search_Ok() throws Exception{
        String keyword = "김밥";
        KeywordSearchUserService.Command command = new KeywordSearchUserService.Command(keyword, new PagingInfo(0, 4, UserSort.recent_activity));

        PagingResult<SearchedUser> pagingResult = new PagingResult<>(
                IntStream.range(0, 4)
                        .mapToObj(i -> TestSearchedUserServiceGenerator.randomSearchedUser(keyword))
                        .collect(Collectors.toList()),
                4,
                20,
                5
        );

        when(searchUserService.execute(command))
                .thenReturn(new KeywordSearchUserService.Result(pagingResult));

        mockMvc.perform(
                get("/users/search/keyword")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "4")
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")
        )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "search-keyword-user",
                                requestParameters(
                                        parameterWithName("keyword").description("유저 닉네임에 포함되는 키워드"),
                                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                        parameterWithName("size").description("페이지 사이즈")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("count").type(JsonFieldType.NUMBER).description("리스트 요소 개수"),
                                        fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("전체 요소 개수"),
                                        fieldWithPath("totalPage").type(JsonFieldType.NUMBER).description("전체 페이지"),
                                        fieldWithPath("list[].userId").description("유저 아이디").type(JsonFieldType.NUMBER),
                                        fieldWithPath("list[].nickname").description("닉네임").type(JsonFieldType.STRING)
                                )
                        )
                );
    }

}