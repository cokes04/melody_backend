package com.melody.melody.adapter.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(controllers = LogoutController.class)
class LogoutControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void logout_200() throws Exception {
        String refreshCookie = refreshTokenName + "=" + "header.payload.signature";

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

        mockMvc.perform(
                post("/logout")
                        .header(HttpHeaders.COOKIE, refreshCookie)
        )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "logout-user",
                                requestHeaders(
                                        headerWithName(HttpHeaders.COOKIE).description(refreshTokenName + " : ???????????? ??????")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.SET_COOKIE).description(refreshTokenName + " : ???????????? ?????? ??????")
                                )
                        )
                );
    }
}