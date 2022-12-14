package com.melody.melody.adapter.persistence.searchedUser;

import com.melody.melody.application.dto.SearchedUser;
import com.querydsl.core.annotations.QueryProjection;

public class SearchedUserData extends SearchedUser {
    @QueryProjection
    public SearchedUserData(long userId, String nickName) {
        super(userId, nickName);
    }
}
