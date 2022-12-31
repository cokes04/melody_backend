package com.melody.melody.adapter.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.web.security.Token;
import com.melody.melody.adapter.web.security.TokenIssuanceService;
import com.melody.melody.adapter.web.user.request.LoginRequest;
import com.melody.melody.application.service.authentication.AuthenticationService;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = LoginController.class)
class LoginControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private TokenIssuanceService tokenIssuanceService;

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
    void login_Ok() throws Exception{
        LoginRequest request = LoginRequest.builder()
                .email(TestUserWebGenerator.randomEmail())
                .password(TestUserWebGenerator.randomPassword())
                .build();

        User user = TestUserDomainGenerator.randomUser();
        Identity userId = user.getId();

        String refreshToken = "header.refreshTokenClaim.signature";

        when(authenticationService.execute( any(AuthenticationService.Command.class) ))
                .thenReturn( new AuthenticationService.Result(user) );

        when(tokenIssuanceService.issuance(userId))
                .thenReturn( new Token(userId, "header.refreshTokenClaim.signature", "header.accessTokenClaim.signature", LocalDateTime.now()) );

        when(cookieSupporter.getRefreshTokenCookie(anyString()))
                .thenReturn(ResponseCookie
                        .from(refreshTokenName, refreshToken)
                        .httpOnly(true)
                        .secure(false)
                        .path(null)
                        .domain(null)
                        .maxAge(10000000)
                        .build()
                        .toString());

        mockMvc.perform(
                post("/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "login-user",
                                requestFields(
                                        fieldWithPath("email").description("이메일").type(JsonFieldType.STRING),
                                        fieldWithPath("password").description("비밀번호").type(JsonFieldType.STRING)
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.SET_COOKIE).description(refreshTokenName + " : 리프레쉬 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("token").description("엑세스 토큰").type(JsonFieldType.STRING)
                                )
                        )
                );
    }
}