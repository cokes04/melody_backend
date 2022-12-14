package com.melody.melody.adapter.persistence.user;

import com.melody.melody.adapter.persistence.music.QMusicEntity;
import com.melody.melody.application.dto.MusicSort;
import com.melody.melody.application.dto.UserSort;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum UserOrderBy {
    ASC_Recent_Activity(UserSort.recent_activity, new OrderSpecifier(Order.DESC, Expressions.path(DateTimePath.class, QUserEntity.userEntity, "lastActivityDate")));

    private final UserSort userSort;
    private final OrderSpecifier orderSpecifier;

    public static Optional<UserOrderBy> get(UserSort userSort){
        for (UserOrderBy userOrderBy : UserOrderBy.values()){
            if (userOrderBy.userSort.equals(userSort))
                return Optional.of(userOrderBy);
        }
        return Optional.empty();
    }

}
