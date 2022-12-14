package com.melody.melody.adapter.persistence.searchedUser;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.adapter.persistence.user.UserOrderBy;
import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.out.SearchedUserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.melody.melody.adapter.persistence.user.QUserEntity.userEntity;

@PersistenceAdapter
@RequiredArgsConstructor
public class SearchedUserRepositoryImpl implements SearchedUserRepository {
    private final JPAQueryFactory factory;

    @Override
    public PagingResult<SearchedUser> search(String keyword, PagingInfo<UserSort> userPaging) {
        BooleanExpression where = userEntity.nickName.contains(keyword);

        List<? extends SearchedUser> result = select()
                .where(where)
                .orderBy(UserOrderBy
                        .get(userPaging.getSorting())
                        .map(UserOrderBy::getOrderSpecifier)
                        .orElseThrow(() -> new InvalidArgumentException(DomainError.of(UserErrorType.Invailid_User_Sort)))
                )
                .offset(userPaging.getPage() * userPaging.getSize())
                .limit(userPaging.getSize())
                .fetch();

        int totalSize = select().where(where).fetch().size();

        return  new PagingResult<SearchedUser>((List<SearchedUser>) result, result.size(), totalSize, (int)Math.ceil((double) totalSize / userPaging.getSize()));
    }

    private JPAQuery<SearchedUserData> select(){
        return factory.select(
                new QSearchedUserData(
                        userEntity.id,
                        userEntity.nickName
                )
        )
                .from(userEntity);
    }
}
