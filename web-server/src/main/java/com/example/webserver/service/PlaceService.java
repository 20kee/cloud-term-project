package com.example.webserver.service;

import com.example.webserver.dto.place.GetPlacePagesResponse;
import com.example.webserver.dto.place.GetPlaceResponse;
import com.example.webserver.entity.Place;
import com.example.webserver.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    public Page<GetPlacePagesResponse> getPlaces(Pageable pageable) {
        return placeRepository.findPlacesWhereNearByPNU(pageable).map(Place::mapToGetPlacePagesResponse);
    }

    public GetPlaceResponse getPlace(Long id) {
        return placeRepository.findById(id).map(Place::mapToGetPlaceResponse).orElseThrow(() -> new RuntimeException("해당하는 식당을 찾을 수 없습니다."));
    }
}
