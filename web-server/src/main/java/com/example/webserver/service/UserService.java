package com.example.webserver.service;

import com.example.webserver.dto.user.GetUserResponse;
import com.example.webserver.entity.User;
import com.example.webserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public GetUserResponse getUser(Long id) {
        return userRepository.findById(id).map(User::mapGetUserResponse).orElseThrow(() -> new RuntimeException("해당하는 사용자를 찾지 못했습니다."));
    }
}
