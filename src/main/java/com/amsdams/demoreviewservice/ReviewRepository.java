package com.amsdams.demoreviewservice;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {

    Optional<Review> findByProductId(Integer productId);
}
