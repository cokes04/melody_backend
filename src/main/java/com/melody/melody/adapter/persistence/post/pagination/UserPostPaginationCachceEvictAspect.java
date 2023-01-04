package com.melody.melody.adapter.persistence.post.pagination;

import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class UserPostPaginationCachceEvictAspect {
    private final UserPostPaginationCache cache;

    @Before(value = "execution(* com.melody.melody.adapter.persistence.post.PostDao.save(com.melody.melody.domain.model.Post))")
    public void evictWhenSave(JoinPoint joinPoint) {
        Post inputPost = (Post) joinPoint.getArgs()[0];
        Identity userId = inputPost.getUserId();

        //create
        if (isInsert(inputPost)) {
            // user의 내림차순 페이지 정보 제거
            cache.evict(userId, false);

        }
        // do nothing, delete, open -> close, close -> open
        else {
            // post가 포함된 index 이후 페이지 정보 제거
            cache.evict(userId, inputPost.getId());
        }

    }

    @Before(value = "execution(* com.melody.melody.adapter.persistence.post.PostDao.deleteByUserId(com.melody.melody.domain.model.Identity))")
    public void evictWhenDeleteByUserId(JoinPoint joinPoint) throws RuntimeException {
        Identity userId = (Identity) joinPoint.getArgs()[0];
        // user의 모든 페이지 정보 제거
        cache.evict(userId);
    }

    private boolean isInsert(Post inputPost){
        return inputPost.getId().isEmpty();
    }
}
