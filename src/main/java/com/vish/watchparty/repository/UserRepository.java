package com.vish.watchparty.repository;

import com.vish.watchparty.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByMobile(String mobile);
    Optional<User> findByEmail(String email);
}