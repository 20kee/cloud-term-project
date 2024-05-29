package com.example.webserver.service;

import com.example.webserver.dto.SaveAllRequest;
import com.example.webserver.dto.SaveAllRequest.ReviewUsernameDto;
import com.example.webserver.dto.SaveAllRequest.ReviewUsernameDto.ReviewDto;
import com.example.webserver.entity.Place;
import com.example.webserver.entity.Review;
import com.example.webserver.entity.User;
import com.example.webserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final PlaceRepository placeRepository;

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final JdbcPlaceRepository jdbcPlaceRepository;

    private final JdbcReviewRepository jdbcReviewRepository;

    private final JdbcUserRepository jdbcUserRepository;

    @Transactional
    public void saveAll(SaveAllRequest request) {
        List<ReviewUsernameDto> reviewUsernames = request.reviews();
        for (ReviewUsernameDto reviewUsername : reviewUsernames) {
            User user = saveUser(reviewUsername.username(), getAverageScore(reviewUsername));
            saveAllReviews(user, reviewUsername.reviewList());
        }
    }

    @Transactional
    public void bulkSaveAll(List<ReviewUsernameDto> reviewUsernames) {
        List<User> users = bulkSaveUser(reviewUsernames);
        System.out.println("유저 수 : " + users.size());
        List<Place> places = bulkSavePlace(reviewUsernames);
        System.out.println("플레이스 수 : " + places.size());
        List<Review> reviews = bulkSaveReviews(users, places, reviewUsernames);
        System.out.println("리뷰 수 : " + reviews.size());
    }

    private List<Review> bulkSaveReviews(List<User> users, List<Place> places, List<ReviewUsernameDto> reviewUsernames) {
        Map<String, User> findUserByUsername = users.stream()
                .collect(Collectors.toMap(
                        User::getUsername,      // 맵의 키로 사용될 Place 객체의 name 필드
                        user -> user,  // 맵의 값으로 사용될 Place 객체의 averageScore 필드
                        (existingValue, newValue) -> newValue// 충돌 처리 규칙 (동일한 키가 있는 경우 새 값을 사용)
                ));
        Map<String, Place> findPlaceByPlaceName = places.stream()
                .collect(Collectors.toMap(
                        Place::getName,
                        place -> place,
                        (existingValue, newValue) -> newValue
                ));

        List<Review> reviews = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        for (ReviewUsernameDto dto : reviewUsernames) {
            for (ReviewDto review : dto.reviewList()) {
                reviews.add(Review.builder().user(findUserByUsername.get(dto.username()))
                        .place(findPlaceByPlaceName.get(review.restaurantName()))
                        .reviewScore(review.reviewScore())
                        .normReviewScore(review.normalizedScore())
                        .description(review.description())
                        .createdAt(LocalDate.parse(review.createdAt(), formatter)).build());
            }
        }

        jdbcReviewRepository.saveAll(reviews);

        return reviews;
    }

    private List<Place> bulkSavePlace(List<ReviewUsernameDto> reviewUsernames) {
        List<Place> places = reviewUsernames.stream()
                .flatMap(dto -> dto.reviewList().stream())
                .collect(Collectors.groupingBy(ReviewDto::restaurantName))
                .entrySet().stream()
                .map(entry -> new Place(
                        entry.getKey(),  // 레스토랑 이름
                        calculateAverageRating(entry.getValue(), ReviewDto::reviewScore),  // 해당 레스토랑의 평균 점수 계산
                        calculateAverageRating(entry.getValue(), ReviewDto::normalizedScore)
                ))
                .toList();
        placeRepository.saveAll(places);
        return places;
    }

    private List<User> bulkSaveUser(List<ReviewUsernameDto> reviewUsernames) {
        List<User> users = reviewUsernames.stream()
                .map(dto -> new User(
                        dto.username(),
                        calculateAverageRating(dto.reviewList(), ReviewDto::reviewScore)
                ))
                .collect(Collectors.toList());

        jdbcUserRepository.saveAll(users);
        return users;
    }

    private double calculateAverageRating(List<ReviewDto> reviews, ToDoubleFunction<ReviewDto> getter) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToDouble(getter::applyAsDouble)
                .average()
                .orElse(0.0);
    }

    private double getAverageScore(ReviewUsernameDto reviewUsername) {
        return reviewUsername.reviewList().stream()
                .mapToInt(ReviewUsernameDto.ReviewDto::reviewScore)
                .average().orElse(0);
    }

    private User saveUser(String username, double averageScore) {
        User user = new User(username, averageScore);
        return userRepository.save(user);
    }

    private void saveAllReviews(User user, List<ReviewUsernameDto.ReviewDto> reviews) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        for (ReviewUsernameDto.ReviewDto review : reviews) {
            Place place = placeRepository.findByName(review.restaurantName());
            if (place == null) place = savePlace(review.restaurantName());

           reviewRepository.save(Review.builder()
                   .description(review.description())
                   .reviewScore(review.reviewScore())
                   .createdAt(LocalDate.parse(review.createdAt(), formatter))
                   .place(place)
                   .user(user).build());
        }
    }

    private Place savePlace(String placeName) {
        Place place = new Place(placeName, 0.0, 0.0);
        return placeRepository.save(place);
    }
}
