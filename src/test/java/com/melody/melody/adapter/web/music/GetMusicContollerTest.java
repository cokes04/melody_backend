package com.melody.melody.adapter.web.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.application.service.music.GetMusicService;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.melody.melody.adapter.web.TestWebGenerator.randomMusicResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = GetMusicContoller.class)
class GetMusicContollerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private GetMusicService service;
    @MockBean private MusicResponseMapper responseMapper;

    @BeforeEach
    public void BeforeEach(WebApplicationContext webApplicationContext,
                           RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(new CharacterEncodingFilter("UTF-8", false, true))
                .build();
    }

    @Test
    void get_Ok() throws Exception {
        Music.MusicId musicId = TestMusicDomainGenerator.randomMusicId();
        Music music = TestMusicDomainGenerator.randomMusic();

        GetMusicService.Command command = new GetMusicService.Command(musicId);
        GetMusicService.Result result =  new GetMusicService.Result(music);

        when(service.execute(eq(command)))
                .thenReturn(result);

        when(this.responseMapper.to(eq(result.getMusic())))
                .thenReturn(randomMusicResponse());

        mockMvc.perform(
                get("/music/{id}", musicId.getValue())
        )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "get-music",
                                pathParameters(
                                        parameterWithName("id").description("음악 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("musicId").description("음악 아이디").type(JsonFieldType.NUMBER),
                                        fieldWithPath("emotion").description("감정(delighted, tense, gloomy, relaxed)").type(JsonFieldType.STRING),
                                        fieldWithPath("explanation").description("이미지 설명").type(JsonFieldType.STRING),
                                        fieldWithPath("imageUrl").description("이미지 URI").type(JsonFieldType.STRING),
                                        fieldWithPath("status").description("음악 상태(progress, completion)").type(JsonFieldType.STRING)
                                )
                        )
                );

    }
}