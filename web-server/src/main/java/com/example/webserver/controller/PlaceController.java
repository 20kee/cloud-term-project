package com.example.webserver.controller;

import com.example.webserver.dto.GetPlacePagesResponse;
import com.example.webserver.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping("/places")
    public String getPlaces(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<GetPlacePagesResponse> places = placeService.getPlaces(pageable);
        System.out.println(places.getTotalElements());
        model.addAttribute("placePage", places);
        model.addAttribute("previous",
                places.hasPrevious() ? places.previousPageable().getPageNumber() : 0);
        model.addAttribute("next",
                places.hasNext() ? places.nextPageable().getPageNumber() : places.getPageable().getPageNumber());
        return "placeList";
    }
}
