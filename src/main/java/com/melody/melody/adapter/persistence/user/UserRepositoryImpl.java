package com.melody.melody.adapter.persistence.user;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        entity = jpaRepository.save(entity);
        return userMapper.toModel(entity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .filter(u -> !u.isWithdrawn())
                .map(userMapper::toModel);
    }

    @Override
    public Optional<User> findById(User.UserId id) {
        return jpaRepository.findById(id.getValue())
                .filter(u -> !u.isWithdrawn())
                .map(userMapper::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
