package com.melody.melody.adapter.web.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.application.service.music.GetMusicService;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = GetMusicController.class)
class GetMusicControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private GetMusicService service;

    @BeforeEach
    public void BeforeEach(WebApplicationContext webApplicationContext,
                           RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(new CharacterEncodingFilter("UTF-8", false, true))
                .build();
    }

    @Test
    void getMusic_Ok() throws Exception {
        Identity musicId = TestMusicDomainGenerator.randomMusicId();
        Music music = TestMusicDomainGenerator.randomMusic();

        GetMusicService.Command command = new GetMusicService.Command(musicId.getValue());
        GetMusicService.Result result =  new GetMusicService.Result(music);

        when(service.execute(eq(command)))
                .thenReturn(result);

        mockMvc.perform(
                get("/music/{musicId}", musicId.getValue())
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")
        )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "get-music",
                                pathParameters(
                                        parameterWithName("musicId").description("?????? ?????????")
                                ),
                                responseFields(
                                        fieldWithPath("musicId").description("?????? ?????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("userId").description("?????? ?????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("emotion").description("??????(?????? ?????? ??????)").type(JsonFieldType.STRING),
                                        fieldWithPath("explanation").description("????????? ??????").type(JsonFieldType.STRING),
                                        fieldWithPath("imageUrl").description("????????? URI").type(JsonFieldType.STRING),
                                        fieldWithPath("musicUrl").description("?????? URI").type(JsonFieldType.STRING),
                                        fieldWithPath("status").description("?????? ??????(?????? ?????? ??????)").type(JsonFieldType.STRING)
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("????????? ??????")
                                )
                        )
                );

    }
}