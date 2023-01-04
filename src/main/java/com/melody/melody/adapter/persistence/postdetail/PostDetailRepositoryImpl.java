package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.adapter.persistence.post.size.PostSizeDao;
import com.melody.melody.adapter.persistence.post.pagination.PostPaginationDao;
import com.melody.melody.adapter.persistence.post.size.SizeInfo;
import com.melody.melody.adapter.persistence.post.pagination.PostPagination;
import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.domain.model.Identity;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class PostDetailRepositoryImpl implements PostDetailRepository {
    private final PostDetailDao postDetailDao;
    private final PostPaginationDao postPaginationDao;
    private final PostSizeDao sizeDao;

    @Override
    public Optional<PostDetail> findById(Identity postId) {
        return postDetailDao.findById(postId);
    }

    @Override
    public PagingResult<PostDetail> findByUserId(Identity userId, Open open, PagingInfo<PostSort> postPaging) {
        long totalSize = getTotalSize(userId, open);
        List<PostDetail> list = getPage(userId, open, postPaging);

        return new PagingResult<PostDetail>(list, list.size(), totalSize, getTotalPage(totalSize, postPaging.getSize()));
    }

    private long getTotalSize(Identity userId, Open open){
        if (Open.Everything.equals(open))
            return sizeDao.findSize(userId, SizeInfo.Open) + sizeDao.findSize(userId,  SizeInfo.Close);

        return sizeDao.findSize(userId, SizeInfo.getOrElseThrow(open));
    }

    private List<PostDetail> getPage(Identity userId, Open open, PagingInfo<PostSort> postPaging){
        PostPagination postPagination = postPaginationDao.find(userId, open, postPaging);

        return postDetailDao.findByUserId(
                userId,
                open,
                postPagination.getStartPostId(),
                postPagination.isStartInclude(),
                postPagination.getOffset(),
                postPaging.getSize(), postPaging.getSorting()
        );
    }

    private int getTotalPage(long totalSize, long pageSize){
        return (int)Math.ceil((double) totalSize / pageSize);
    }
}
