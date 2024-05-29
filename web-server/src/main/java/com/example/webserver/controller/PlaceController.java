package com.example.webserver.controller;

import com.example.webserver.dto.GetPlacePagesResponse;
import com.example.webserver.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping("/places")
    public String getPlaces(Model model, Pageable pageable) {
        Page<GetPlacePagesResponse> places = placeService.getPlaces(pageable);

        model.addAttribute("placePage", places);
        return "placeList";
    }
}
