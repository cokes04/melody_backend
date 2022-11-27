package com.melody.melody.adapter.persistence.post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {
}
