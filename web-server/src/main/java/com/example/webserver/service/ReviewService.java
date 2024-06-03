package com.example.webserver.service;

import com.example.webserver.dto.init.RestaurantList;
import com.example.webserver.dto.init.SaveAllRequest;
import com.example.webserver.dto.init.SaveAllRequest.ReviewUsernameDto;
import com.example.webserver.dto.init.SaveAllRequest.ReviewUsernameDto.ReviewDto;
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
import java.util.Set;
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
    public void bulkSaveAll(List<ReviewUsernameDto> reviewUsernames, Map<String, RestaurantList.RestaurantInfo> restaurantInfo) {
        List<User> users = bulkSaveUser(reviewUsernames);
        System.out.println("유저 수 : " + users.size());
        List<Place> places = bulkSavePlace(reviewUsernames, restaurantInfo);
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
                        Place::getPlaceName,
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

    private List<Place> bulkSavePlace(List<ReviewUsernameDto> reviewUsernames, Map<String, RestaurantList.RestaurantInfo> maps) {
        //식당들의 중복된 이름 제거하고 얻기
        Set<String> placesName = reviewUsernames.stream()
                .flatMap(dto -> dto.reviewList().stream())
                .map(ReviewDto::restaurantName).collect(Collectors.toSet());

        System.out.println(placesName.size());
        List<Place> returns = new ArrayList<>();

        for (String place : placesName) {
            if (maps.containsKey(place)) {
                RestaurantList.RestaurantInfo info = maps.get(place);
                returns.add(Place.builder()
                        .isNearByPNU(true)
                        .tags(info.tags())
                                .reviewNumber(Long.parseLong(info.reviewCount().replace(",","")))
                                .averageScore(Double.parseDouble(info.averageScore()))
                        .averageNormScore(calculateAverageRating(reviewUsernames.stream()
                                .flatMap(reviewUsernameDto -> reviewUsernameDto.reviewList().stream())
                                .filter(reviewDto -> reviewDto.restaurantName().equals(place))
                                .toList(), ReviewDto::normalizedScore))
                        .placeName(place)
                        .address1(info.address1())
                        .address2(info.address2())
                        .build());
            }
            else {
                returns.add(new Place(place));
            }
        }

                /*
                //.filter(entry -> entry.getValue().size() >= 10) //리뷰가 10개 이상인 식당만
                .map(entry -> {
                    boolean isNearByPNU = maps.containsKey(entry.getKey());
                    if (isNearByPNU) return Place.builder()
                                    .isNearByPNU(isNearByPNU)
                                    .tags(maps.get(entry.getKey()).tags())
                                    .reviewNumber(Long.parseLong(maps.get(entry.getKey()).reviewCount().replace(",", "")))
                                    .averageScore(Double.parseDouble(maps.get(entry.getKey()).averageScore()))
                                    .averageNormScore(calculateAverageRating(entry.getValue(), ReviewDto::normalizedScore))
                                    .name(entry.getKey())
                                    .address1(maps.get(entry.getKey()).address1())
                                    .address2(maps.get(entry.getKey()).address2())
                                    .build();
                    else return new Place(entry.getKey(),
                            entry.getValue().size(),
                            calculateAverageRating(entry.getValue(), ReviewDto::reviewScore),
                            calculateAverageRating(entry.getValue(), ReviewDto::normalizedScore));
                        }
                )
                .collect(Collectors.toMap(
                        Place::getName,  // 중복을 제거할 기준인 Place의 이름
                        Function.identity(),  // Place 객체 자체를 값으로 사용
                        (existing, replacement) -> existing // 중복 발생 시 기존 값을 유지
                ))
                .toList();
                 */
        placeRepository.saveAll(returns);
        return returns;
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
            Place place = placeRepository.findByPlaceName(review.restaurantName());
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
        Place place = new Place(placeName);
        return placeRepository.save(place);
    }
}
