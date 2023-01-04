package com.melody.melody.adapter.persistence.post.pagination;

import com.melody.melody.adapter.persistence.post.PostOrderBy;
import com.melody.melody.adapter.persistence.post.PostQuerySupport;
import com.melody.melody.adapter.persistence.post.size.SizeInfo;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.model.Identity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.melody.melody.adapter.persistence.post.QPostEntity.postEntity;

@Repository
@RequiredArgsConstructor
public class PostPaginationDao {
    private final JPAQueryFactory factory;
    private final UserPostPaginationCache userCache;

    private final int selectCriteria = 3;

    public PostPagination find(Identity userId, Open open, PagingInfo<PostSort> pagingInfo){
        if (isFirstPage(pagingInfo))
            return new PostPagination(null, false, 0);

        if (Open.Everything.equals(open))
            return findEverything(userId, pagingInfo);

        SizeInfo sizeInfo = SizeInfo.getOrElseThrow(open);
        boolean asc = PostOrderBy.getOrElseThrow(pagingInfo.getSorting()).getOrderSpecifier().isAscending();
        int offset = pagingInfo.getPage() * pagingInfo.getSize();
        UserPostPaginationCache.Result cachceResult = userCache.get(userId, sizeInfo, asc, offset);

        if (cachceResult.getNeededPages() < selectCriteria)
            return cachceResult.getPostPagination();

        Long startId = cachceResult.getPostPagination().getStartPostId();
        long cacheOffset = cachceResult.getPostPagination().getOffset();

        List<Long> list = findInternal(userId, open, startId, cachceResult.getPostPagination().isStartInclude(),cacheOffset + 1, pagingInfo.getSorting());
        userCache.put(userId, sizeInfo, asc, offset - cacheOffset, list);

        long resultOffset = (cacheOffset + 1) - list.size();
        return PostPagination.builder()
                .startPostId(list.get(list.size()-1))
                .startInclude(true)
                .offset(resultOffset)
                .build();
    }

    private PostPagination findEverything(Identity userId, PagingInfo<PostSort> pagingInfo){
        boolean asc = PostOrderBy.getOrElseThrow(pagingInfo.getSorting()).getOrderSpecifier().isAscending();
        long limit = pagingInfo.getPage() * pagingInfo.getSize() + 1;
        List<PostInfo> postInfoList = findInternal(userId, null, false, limit, pagingInfo.getSorting());

        int listCapacity = Math.max(postInfoList.size()/2, 16);
        List<Long> closeIdList = new ArrayList<>(listCapacity), openIdList = new ArrayList<>(listCapacity);
        for (PostInfo postInfo : postInfoList){
            if (postInfo.open)
                openIdList.add(postInfo.postId);
            else
                closeIdList.add(postInfo.postId);
        }

        userCache.put(userId, SizeInfo.Open, asc, 0, openIdList);
        userCache.put(userId, SizeInfo.Close, asc, 0, closeIdList);

        return PostPagination.builder()
                .startPostId(postInfoList.get(postInfoList.size()-1).postId)
                .startInclude(true)
                .offset(limit - postInfoList.size())
                .build();
    }

    private List<Long> findInternal(Identity userId, Open open, Long startId, boolean startIdInclude, long limit, PostSort postSort){
        return findInternal(postEntity.id, userId, open, startId, startIdInclude, limit, postSort)
                .fetch();
    }

    private List<PostInfo> findInternal(Identity userId, Long startId, boolean startIdInclude, long limit, PostSort postSort){
        return findInternal(
                new QPostPaginationDao_PostInfo(
                        postEntity.id,
                        postEntity.open
                ), userId, null, startId, startIdInclude, limit, postSort)
                .fetch();
    }

    private <T> JPAQuery<T> findInternal(Expression<T> select, Identity userId, Open open, Long startId, boolean startIdInclude, long limit, PostSort postSort){
        OrderSpecifier orderSpecifier = PostOrderBy.getOrElseThrow(postSort).getOrderSpecifier();

        BooleanBuilder where = new BooleanBuilder();
        where.and(PostQuerySupport.eqDeleted(false));
        where.and(PostQuerySupport.eqUserId(userId.getValue()));
        where.and(PostQuerySupport.eqOpen(open));
        where.and(startIdInclude ? PostQuerySupport.startPostId(startId, orderSpecifier) : PostQuerySupport.nextPostId(startId, orderSpecifier));

        return factory
                .select(select)
                .from(postEntity)
                .where(where)
                .limit(limit)
                .orderBy(orderSpecifier);
    }

    private boolean isFirstPage(PagingInfo<PostSort> pagingInfo){
        return pagingInfo.getPage() == 0;
    }

    public static class PostInfo {
        private long postId;
        private boolean open;

        @QueryProjection
        public PostInfo(long postId, boolean open) {
            this.postId = postId;
            this.open = open;
        }
    }

}
