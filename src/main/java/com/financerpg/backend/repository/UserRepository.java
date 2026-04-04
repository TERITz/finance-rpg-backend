package com.financerpg.backend.repository;

import com.financerpg.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByLineUserId(String lineUserId);
    boolean existsByEmail(String email);

}
