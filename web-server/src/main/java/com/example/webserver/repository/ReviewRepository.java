package com.example.webserver.repository;

import com.example.webserver.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByPlaceId(Long placeId, Pageable pageable);

    Page<Review> findAllByUserId(Long userId, Pageable pageable);
}
