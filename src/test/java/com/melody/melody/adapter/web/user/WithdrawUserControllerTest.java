package com.melody.melody.adapter.web.user;

import com.melody.melody.application.service.user.WithdrawUserService;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WebMvcTest(controllers = WithdrawUserController.class)
class WithdrawUserControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private WithdrawUserService service;

    @MockBean
    private CookieSupporter cookieSupporter;

    @Value("${app.jwt.refreshToken.name}")
    private String refreshTokenName;


    @BeforeEach
    public void BeforeEach(WebApplicationContext webApplicationContext,
                           RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(new CharacterEncodingFilter("UTF-8", false, true))
                .build();
    }

    @Test
    void withdraw_200() throws Exception {
        User user = TestUserDomainGenerator.randomUser();
        WithdrawUserService.Command command = new WithdrawUserService.Command(user.getId().getValue());

        when(service.execute(command))
                .thenReturn(new WithdrawUserService.Result(user));

        when(cookieSupporter.removeRefreshTokenCookie())
                .thenReturn(ResponseCookie
                        .from(refreshTokenName, null)
                        .httpOnly(true)
                        .secure(false)
                        .path(null)
                        .domain(null)
                        .maxAge(0)
                        .build()
                        .toString());

        this.mockMvc.perform(
                delete("/users/{userId}", user.getId().getValue())
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "withdarw-user",
                                pathParameters(
                                        parameterWithName("userId").description("탈퇴할 유저 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.SET_COOKIE).description(refreshTokenName + " : 리프레쉬 토큰 제거")
                                )
                        )
                );
    }
}