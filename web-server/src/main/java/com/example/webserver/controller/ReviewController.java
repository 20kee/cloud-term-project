package com.example.webserver.controller;

import com.example.webserver.dto.SaveAllRequest;
import com.example.webserver.repository.ReviewRepository;
import com.example.webserver.service.ReviewService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    private final ReviewRepository reviewRepository;

    //public static final String JSON_NAME = "test.json";
    public static final String JSON_NAME = "user_reviews_dict_normalized.json";
    public void saveAll() {
        URL resource = getClass().getClassLoader().getResource(JSON_NAME);
        String filePath = resource.getFile();
        SaveAllRequest request = null;
        try (FileReader fr = new FileReader(filePath);
             BufferedReader br = new BufferedReader(fr)
        ) {
            request = new Gson().fromJson(br, SaveAllRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //reviewService.saveAll(request);
        reviewService.bulkSaveAll(request.reviews());
    }
}
