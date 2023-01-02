package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.adapter.persistence.post.PostOrderBy;
import com.melody.melody.adapter.persistence.post.PostQuerySupport;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.melody.melody.adapter.persistence.music.QMusicEntity.musicEntity;
import static com.melody.melody.adapter.persistence.post.QPostEntity.postEntity;
import static com.melody.melody.adapter.persistence.user.QUserEntity.userEntity;

@Repository
@RequiredArgsConstructor
public class PostDetailDao {
    private final JPAQueryFactory factory;

    public Optional<PostDetail> findById(Identity postId) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(postEntity.deleted.eq(false));
        where.and(postEntity.id.eq(postId.getValue()));

        PostDetail postDetail = select()
                .where(where)
                .fetchOne();

        return Optional.ofNullable(postDetail);
    }


    public List<PostDetail> findByUserId(Identity userId, Open open, Long startId, boolean startIdInclude, long offset, int size, PostSort postSort){
        OrderSpecifier orderSpecifier = PostOrderBy.getOrElseThrow(postSort).getOrderSpecifier();

        BooleanBuilder where = new BooleanBuilder();
        where.and(PostQuerySupport.eqDeleted(false));
        where.and(PostQuerySupport.eqOpen(open));
        where.and(PostQuerySupport.eqUserId(userId.getValue()));
        where.and(startIdInclude ? PostQuerySupport.startPostId(startId, orderSpecifier) : PostQuerySupport.nextPostId(startId, orderSpecifier));

        List<? extends PostDetail> result = select()
                .where(where)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(size)
                .fetch();

        return (List<PostDetail>) result;

    }

    public List<PostDetail> findByUserId(Identity userId, Open open, PagingInfo<PostSort> postPaging) {
        return findByUserId(userId, open, null, false, postPaging.getPage() * postPaging.getSize(), postPaging.getSize(), postPaging.getSorting());
    }

    private JPAQuery<PostDetailData> select(){
        return factory.select(
                new QPostDetailDao_PostDetailData(
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
                        musicEntity.musicUrl,
                        musicEntity.status
                )
        )
                .from(postEntity)
                .leftJoin(postEntity.musicEntity, musicEntity)
                .leftJoin(postEntity.userEntity, userEntity);
    }

    public static class PostDetailData extends PostDetail {

        @QueryProjection
        public PostDetailData(Long id, String title, String content, int likeCount, boolean open, boolean deleted, LocalDateTime createdDate,
                              Long userId, String nickname, Long musicId, Music.Emotion emotion, String explanation, String imageUrl, String musicUrl, Music.Status musicStatus) {
            super(id, title, content, likeCount, open, deleted, createdDate, userId, nickname, musicId, emotion, explanation, imageUrl, musicUrl, musicStatus);
        }
    }
}
