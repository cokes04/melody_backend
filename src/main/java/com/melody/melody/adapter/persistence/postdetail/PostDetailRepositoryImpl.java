package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.adapter.persistence.post.PostOrderBy;
import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class PostDetailRepositoryImpl implements PostDetailRepository {
    private final PostDetailDao dao;
    private final PostTotalSizeCache totalSizeCache;

    @Override
    public Optional<PostDetail> findById(Identity postId) {
        return dao.findById(postId);
    }

    @Override
    public PagingResult<PostDetail> findByUserId(Identity userId, Open open, PagingInfo<PostSort> postPaging) {
        long totalSize = getTotalSize(userId, open);
        List<PostDetail> list = getPostDetailList(userId, open, postPaging);

        return new PagingResult(list, list.size(), totalSize, getTotalPage(totalSize, postPaging.getSize()));
    }

    private int getTotalPage(long totalSize, long pageSize){
        return  (int)Math.ceil((double) totalSize / pageSize);
    }

    private List<PostDetail> getPostDetailList(Identity userId, Open open, PagingInfo<PostSort> postPaging){
        return dao.findByUserId(userId, open, postPaging);
    }

    private long getTotalSize(Identity userId, Open open){
        Optional<Long> optional = totalSizeCache.getTotalSize(userId, open);

        if (optional.isPresent())
            return optional.get();

        if (Open.Everything.equals(open))
            return getTotalSize(userId, Open.OnlyOpen) + getTotalSize(userId, Open.OnlyClose);

        return getTotalSizeFromDao(userId, open, true);
    }

    private long getTotalSizeFromDao(Identity userId, Open open, boolean putCache){
        long totalSize = dao.findTotalSizeByUserId(userId, open);
        if (putCache)
            totalSizeCache.putTotalSize(userId, open, totalSize);
        return totalSize;
    }

    private boolean isASC(PostSort postSort){
        return PostOrderBy.get(postSort)
                .orElseThrow(() -> new InvalidArgumentException(DomainError.of(PostErrorType.Invalid_Post_Sort)))
                .getOrderSpecifier()
                .isAscending();
    }
}
