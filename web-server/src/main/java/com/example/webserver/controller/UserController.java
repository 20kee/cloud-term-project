package com.example.webserver.controller;

import com.example.webserver.dto.review.GetUserReviewsPagesResponse;
import com.example.webserver.dto.user.GetUserResponse;
import com.example.webserver.service.ReviewService;
import com.example.webserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final ReviewService reviewService;

    @GetMapping("/user/{id}")
    public String getUser(Model model, @PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        GetUserResponse userResponse = userService.getUser(id);
        Page<GetUserReviewsPagesResponse> reviewsResponse = reviewService.getReviewsByUserId(id, pageable);

        model.addAttribute("user", userResponse);
        model.addAttribute("userReviews", reviewsResponse);
        model.addAttribute("previous",
                reviewsResponse.hasPrevious() ? reviewsResponse.previousPageable().getPageNumber() : 0);
        model.addAttribute("next",
                reviewsResponse.hasNext() ?
                        reviewsResponse.nextPageable().getPageNumber() :
                        reviewsResponse.getPageable().getPageNumber());
        model.addAttribute("current", reviewsResponse.getPageable().getPageNumber() + 1);
        return "userWithReviews";
    }
}
