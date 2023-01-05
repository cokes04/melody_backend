package com.melody.melody.adapter.persistence.post.pagination;

import com.melody.melody.adapter.persistence.post.PostOrderBy;
import com.melody.melody.adapter.persistence.post.PostQuerySupport;
import com.melody.melody.adapter.persistence.post.size.SizeInfo;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.model.Identity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.melody.melody.adapter.persistence.post.QPostEntity.postEntity;

@Repository
@RequiredArgsConstructor
public class PostPaginationDao {
    private final JPAQueryFactory factory;
    private final UserPostPaginationCache userCache;

    private final int selectCriteria = 3;

    public PostPagination find(Identity userId, Open open, PagingInfo<PostSort> pagingInfo){
        if (Open.Everything.equals(open)) return findEverything(userId, pagingInfo);

        SizeInfo sizeInfo = SizeInfo.getOrElseThrow(open);
        boolean asc = PostOrderBy.getOrElseThrow(pagingInfo.getSorting()).getOrderSpecifier().isAscending();
        int existingOffset = pagingInfo.getPage() * pagingInfo.getSize();

        UserPostPaginationCache.Result cachceResult = userCache.get(userId, sizeInfo, asc, existingOffset);
        Long startId = cachceResult.getPostPagination().getStartPostId();
        long cacheOffset = cachceResult.getPostPagination().getOffset();

        if (cachceResult.getNeededIndexs() < selectCriteria)
            return cachceResult.getPostPagination();

        long offset = Math.max(0, cacheOffset - selectCriteria * cachceResult.getCountPerIndex());
        long limit =  pagingInfo.getSize() + (selectCriteria * cachceResult.getCountPerIndex() * 2);
        List<Long> list = findInternal(userId, open, startId, cachceResult.getPostPagination().isStartInclude(), offset, limit, pagingInfo.getSorting());

        userCache.put(userId, sizeInfo, asc, offset + existingOffset - cacheOffset, list);

        List<Long> resultIdList = getIdList(list, selectCriteria * cachceResult.getCountPerIndex(), pagingInfo.getSize());

        return PostPagination.builder()
                .startPostId(resultIdList.size() == 0 ? (asc ? 0L : Long.MAX_VALUE): resultIdList.get(0))
                .startInclude(true)
                .offset(resultIdList.size() == 0 ? existingOffset : 0)
                .inIdList(resultIdList)
                .noResult(resultIdList.size() == 0)
                .build();
    }

    private PostPagination findEverything(Identity userId, PagingInfo<PostSort> pagingInfo){
        long offset = pagingInfo.getPage() * pagingInfo.getSize();
        List<Long> list = findInternal(userId, Open.Everything, null,false, offset, pagingInfo.getSize(), pagingInfo.getSorting());

        return PostPagination.builder()
                .startPostId(list.get(0))
                .startInclude(false)
                .offset(0)
                .inIdList(list)
                .build();
    }

    private List<Long> findInternal(Identity userId, Open open, Long startId, boolean startIdInclude, long offset, long limit, PostSort postSort){
        return findInternal(postEntity.id, userId, open, startId, startIdInclude, offset, limit, postSort)
                .fetch();
    }

    private <T> JPAQuery<T> findInternal(Expression<T> select, Identity userId, Open open, Long startId, boolean startIdInclude, long offset, long limit, PostSort postSort){
        OrderSpecifier orderSpecifier = PostOrderBy.getOrElseThrow(postSort).getOrderSpecifier();

        BooleanBuilder where = new BooleanBuilder();
        where.and(startIdInclude ? PostQuerySupport.startPostId(startId, orderSpecifier) : PostQuerySupport.nextPostId(startId, orderSpecifier));
        where.and(PostQuerySupport.eqUserId(userId.getValue()));
        where.and(PostQuerySupport.eqDeleted(false));
        where.and(PostQuerySupport.eqOpen(open));

        return factory
                .select(select)
                .from(postEntity)
                .where(where)
                .offset(offset)
                .limit(limit)
                .orderBy(orderSpecifier);
    }

    private List<Long> getIdList(List<Long> idList, int offset, int size){
        List<Long> newList = new ArrayList<Long>(size);
        if (idList.size() < offset) return newList;

        size = Math.min(idList.size() - offset, size);
        for (int i = offset; i < offset + size; i++)
            newList.add(idList.get(i));

        return newList;
    }
}
