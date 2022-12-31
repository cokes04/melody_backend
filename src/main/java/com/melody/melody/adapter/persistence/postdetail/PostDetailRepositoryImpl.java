package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.adapter.persistence.post.PostDao;
import com.melody.melody.adapter.persistence.post.SizeInfo;
import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.melody.melody.domain.model.Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class PostDetailRepositoryImpl implements PostDetailRepository {
    private final PostDetailDao postDetailDao;
    private final PostDao postDao;

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
        switch (open){
            case Everything:
                return postDao.findSize(userId, Open.OnlyOpen, SizeInfo.Open)
                    + postDao.findSize(userId, Open.OnlyClose, SizeInfo.Close);

            case OnlyOpen:
                return postDao.findSize(userId, Open.OnlyOpen, SizeInfo.Open);

            case OnlyClose:
                return postDao.findSize(userId, Open.OnlyClose, SizeInfo.Close);

            default:
                throw new InvalidArgumentException(DomainError.of(PostErrorType.Invalid_Open));
        }
    }

    private int getTotalPage(long totalSize, long pageSize){
        return (int)Math.ceil((double) totalSize / pageSize);
    }

    private List<PostDetail> getPage(Identity userId, Open open, PagingInfo<PostSort> postPaging){
        return postDetailDao.findByUserId(userId, open, postPaging);
        /*
        List<PostDao.PageStatistic> pageStatistics = postDao.findPageStatistics(userId, postPaging.getSize(), postPaging.getSorting());

        return null;
        */
    }
}
