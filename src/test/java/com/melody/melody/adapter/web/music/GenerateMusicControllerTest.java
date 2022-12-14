package com.melody.melody.adapter.web.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.web.music.request.GenerateMusicRequest;
import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = GenerateMusicController.class)
@ContextConfiguration
class GenerateMusicControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private GenerateMusicService service;

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
    void generateMusic_Ok() throws Exception {
        GenerateMusicRequest generateMusicRequest = GenerateMusicRequest.builder()
                .noise(1)
                .musicLength(213)
                .build();

        MockMultipartFile image = new MockMultipartFile(
                "image", "test_image.jpng", MediaType.IMAGE_JPEG_VALUE, "jpng".getBytes()
        );

        MockMultipartFile body = new MockMultipartFile(
                "body", "json-data", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(generateMusicRequest)
        );

        Music music = TestMusicDomainGenerator.randomProgressMusic();
        GenerateMusicService.Result result = new GenerateMusicService.Result(music);

        when(service.execute(eq(generateMusicRequest.toCommand(image, requesterUserId))))
                .thenReturn(result);

        mockMvc.perform(
                multipart("/music")
                        .file(image)
                        .file(body)
                        .header(HttpHeaders.AUTHORIZATION, "header.payload.signature")
        )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "generate-music",
                                requestParts(
                                        partWithName("image").description("????????? ??????"),
                                        partWithName("body").description("?????? ??????")
                                ),
                                requestPartBody("body"),
                                requestPartFields(
                                        "body",
                                        fieldWithPath("musicLength").description("?????? ??????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("noise").description("?????? ??????").type(JsonFieldType.NUMBER)
                                ),
                                responseFields(
                                        fieldWithPath("musicId").description("?????? ?????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("userId").description("?????? ?????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("emotion").description("??????(?????? ?????? ??????)").type(JsonFieldType.STRING),
                                        fieldWithPath("explanation").description("????????? ??????").type(JsonFieldType.STRING),
                                        fieldWithPath("imageUrl").description("????????? URI").type(JsonFieldType.STRING),
                                        fieldWithPath("status").description("?????? ??????(?????? ?????? ??????)").type(JsonFieldType.STRING)
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("????????? ??????")
                                )
                        )
                );

    }
}