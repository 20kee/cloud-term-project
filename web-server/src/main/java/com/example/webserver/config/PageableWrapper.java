package com.example.webserver.config;

import org.springframework.data.domain.Pageable;

public record PageableWrapper (
        String sort,
        Pageable pageable
) {}
