package com.melody.melody.adapter.persistence.user;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;
    private final UserMapper userMapper;

    @Override
    @CacheEvict(cacheNames = "userDetails", key = "#user.id.value")
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        entity = jpaRepository.save(entity);
        return userMapper.toModel(entity);
    }

    @Override
    public Optional<User> findByEmail(User.Email email) {
        return jpaRepository.findByEmail(email.getValue())
                .filter(u -> !u.isWithdrawn())
                .map(userMapper::toModel);
    }

    @Override
    public Optional<User> findById(Identity userId) {
        return jpaRepository.findById(userId.getValue())
                .filter(u -> !u.isWithdrawn())
                .map(userMapper::toModel);
    }

    @Override
    public boolean existsByEmail(User.Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }
}
