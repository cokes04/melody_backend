package com.melody.melody.application.port.out;

import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PagingResult;
import com.melody.melody.application.dto.SearchedUser;
import com.melody.melody.application.dto.UserSort;

public interface SearchedUserRepository {
    PagingResult<SearchedUser> search(String keyword, PagingInfo<UserSort> pagingInfo);
}
