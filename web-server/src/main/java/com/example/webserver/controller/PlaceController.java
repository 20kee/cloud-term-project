package com.example.webserver.controller;

import com.example.webserver.config.PageableWrapper;
import com.example.webserver.config.PlacePageable;
import com.example.webserver.config.ReviewPageable;
import com.example.webserver.dto.place.GetPlacePagesResponse;
import com.example.webserver.dto.place.GetPlaceResponse;
import com.example.webserver.dto.review.GetReviewsPagesResponse;
import com.example.webserver.service.PlaceService;
import com.example.webserver.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    private final ReviewService reviewService;

    @GetMapping("/places")
    public String getPlaces(Model model,
                            @PlacePageable PageableWrapper pageableWrapper) {
        Page<GetPlacePagesResponse> places = placeService.getPlaces(pageableWrapper.pageable());
        System.out.println(places.getTotalElements());
        model.addAttribute("placePage", places);
        model.addAttribute("previous",
                places.hasPrevious() ? places.previousPageable().getPageNumber() : 0);
        model.addAttribute("next",
                places.hasNext() ? places.nextPageable().getPageNumber() : places.getPageable().getPageNumber());
        model.addAttribute("current", places.getPageable().getPageNumber() + 1);
        model.addAttribute("currentSort", pageableWrapper.sort());
        return "placeList";
    }

    @GetMapping("/place/{id}")
    public String getPlace(Model model, @PathVariable("id") Long id, @ReviewPageable PageableWrapper pageableWrapper) {
        GetPlaceResponse placeResponse = placeService.getPlace(id);
        Page<GetReviewsPagesResponse> reviewResponse = reviewService.getReviewsByPlaceId(id, pageableWrapper.pageable());

        model.addAttribute("place", placeResponse);
        model.addAttribute("placeReviews", reviewResponse);
        model.addAttribute("previous",
                reviewResponse.hasPrevious() ? reviewResponse.previousPageable().getPageNumber() : 0);
        model.addAttribute("next",
                reviewResponse.hasNext() ?
                        reviewResponse.nextPageable().getPageNumber() :
                        reviewResponse.getPageable().getPageNumber());
        model.addAttribute("current", reviewResponse.getPageable().getPageNumber() + 1);
        model.addAttribute("currentSort", pageableWrapper.sort());
        return "placeWithReviews";
    }
}
