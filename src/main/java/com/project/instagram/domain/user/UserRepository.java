package com.project.instagram.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    //  JPA naming Query
    Optional<User> findByUsername(String username);
}
