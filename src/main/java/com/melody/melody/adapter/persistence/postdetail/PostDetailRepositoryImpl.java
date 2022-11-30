package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.adapter.persistence.post.PostOrderBy;
import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.melody.melody.adapter.persistence.post.QPostEntity.*;
import static com.melody.melody.adapter.persistence.music.QMusicEntity.*;
import static com.melody.melody.adapter.persistence.user.QUserEntity.*;

@PersistenceAdapter
@RequiredArgsConstructor
public class PostDetailRepositoryImpl implements PostDetailRepository {
    private final JPAQueryFactory factory;

    @Override
    public Optional<PostDetail> findById(Post.PostId postId) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(postEntity.deleted.eq(false));
        where.and(postEntity.id.eq(postId.getValue()));

        PostDetail postDetail = select()
                .where(where)
                .fetchOne();

        return Optional.ofNullable(postDetail);
    }

    public PagingResult<PostDetail> findByUserId(User.UserId userId, Open open, PagingInfo<PostSort> postPaging) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(postEntity.deleted.eq(false));
        where.and(eqOpen(open));
        where.and(postEntity.userEntity.id.eq(userId.getValue()));

        List<? extends PostDetail> result = select()
                .from(postEntity)
                .leftJoin(postEntity.musicEntity, musicEntity)
                .leftJoin(postEntity.userEntity, userEntity)
                .where(where)
                .orderBy(PostOrderBy.get(postPaging.getSorting()).getOrderSpecifier())
                .offset(postPaging.getPage() * postPaging.getSize())
                .limit(postPaging.getSize())
                .fetch();

        int totalSize = factory
                .selectFrom(postEntity)
                .where(where)
                .fetch().size();

        return  new PagingResult<PostDetail>((List<PostDetail>) result, result.size(), totalSize, (int)Math.ceil((double) totalSize / postPaging.getSize()));

    }

    private BooleanExpression eqOpen(Open open){
        switch (open){
            case OnlyOpen:
                return postEntity.open.eq(true);
            case OnlyClose:
                return postEntity.open.eq(false);
            default:
                return null;
        }
    }

    private JPAQuery<PostDetailData> select(){
        return factory.select(
                new QPostDetailData(
                        postEntity.id,
                        postEntity.title,
                        postEntity.content,
                        postEntity.likeCount,
                        postEntity.open,
                        postEntity.deleted,
                        postEntity.createdDate,
                        userEntity.id,
                        userEntity.nickName,
                        musicEntity.id,
                        musicEntity.emotion,
                        musicEntity.explanation,
                        musicEntity.imageUrl,
                        musicEntity.musicUrl
                )
        )
                .from(postEntity)
                .leftJoin(postEntity.musicEntity, musicEntity)
                .leftJoin(postEntity.userEntity, userEntity);
    }
}
