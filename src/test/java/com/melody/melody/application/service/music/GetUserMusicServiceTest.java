package com.melody.melody.application.service.music;

import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GetUserMusicServiceTest {
    private GetUserMusicService service;

    private MusicRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(MusicRepository.class);
        service = new GetUserMusicService(repository);
    }

    @Test
    void excute_ShouldReturnList() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        MusicPublish publish = MusicPublish.Everything;
        PagingInfo<MusicSort> musicPaging = new PagingInfo<>(2, 10, MusicSort.newest);

        List<Music> musicList = IntStream.range(0, 10)
                .mapToObj(i -> TestMusicDomainGenerator.randomMusic())
                .collect(Collectors.toList());

        when(repository.findByUserId(userId, publish, musicPaging))
                .thenReturn(new PagingResult<>(musicList, musicList.size(), 50, 5));

        GetUserMusicService.Command command = new GetUserMusicService.Command(userId.getValue(), publish, musicPaging);
        GetUserMusicService.Result result = service.execute(command);
        PagingResult<Music> actual = result.getPagingResult();

        assertEquals(musicList, actual.getList());
        assertEquals(musicList.size(), actual.getCount());
    }
}