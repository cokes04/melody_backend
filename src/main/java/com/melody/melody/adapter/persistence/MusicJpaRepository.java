package com.melody.melody.adapter.persistence;

import com.melody.melody.adapter.persistence.entity.MusicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicJpaRepository extends JpaRepository<MusicEntity, Long> {
}
