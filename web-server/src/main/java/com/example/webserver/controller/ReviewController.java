package com.example.webserver.controller;

import com.example.webserver.dto.init.RestaurantList;
import com.example.webserver.dto.init.SaveAllRequest;
import com.example.webserver.repository.ReviewRepository;
import com.example.webserver.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    private final ReviewRepository reviewRepository;

    //public static final String JSON_NAME = "test.json";
    public static final String REVIEW_JSON_NAME = "/user_reviews_dict_normalized.json";
    public static final String PLACE_JSON_NAME = "/restaurant.json";

    public static final String PLACE_REVIEW_JSON_NAME = "restaurant_review_dict.json";
    public void saveAll() {
        ObjectMapper mapper = new ObjectMapper();

        SaveAllRequest request = getSaveAllRequest(mapper);
        if (request == null) return;

        Map<String, RestaurantList.RestaurantInfo> restaurantList = getRestaurants(mapper);


        //reviewService.saveAll(request);
        reviewService.bulkSaveAll(request.reviews(), restaurantList);
    }

    private Map<String, RestaurantList.RestaurantInfo> getRestaurants(ObjectMapper mapper) {
        RestaurantList request = null;
        System.out.println("==========================================================");
        try (InputStream is = getClass().getResourceAsStream(PLACE_JSON_NAME);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)
        ) {
            request = mapper.readValue(br, RestaurantList.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return request.restaurants().stream()
                .collect(Collectors.toMap(RestaurantList.RestaurantInfo::name, restaurant -> restaurant));
    }

    private SaveAllRequest getSaveAllRequest(ObjectMapper mapper) {
        SaveAllRequest request = null;
        try (InputStream is = getClass().getResourceAsStream(REVIEW_JSON_NAME);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)
        ) {
            //request = new Gson().fromJson(br, SaveAllRequest.class);
            request = mapper.readValue(br, SaveAllRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return request;
    }
}
