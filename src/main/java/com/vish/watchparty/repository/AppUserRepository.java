package com.vish.watchparty.repository;

import com.vish.watchparty.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByMobile(String mobile);
}