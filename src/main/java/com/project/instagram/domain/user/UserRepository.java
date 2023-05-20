package com.project.instagram.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    //  JPA naming Query
    User findByUsername(String username);
}
