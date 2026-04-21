package com.vish.watchparty.repository;

import com.vish.watchparty.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, String> {
    List<Movie> findAllByOrderByCreatedAtDesc();
    List<Movie> findByGroupTitleIgnoreCaseOrderByPartNumberAscCreatedAtAsc(String groupTitle);
}