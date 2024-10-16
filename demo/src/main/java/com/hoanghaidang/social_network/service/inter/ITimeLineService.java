package com.hoanghaidang.social_network.service.inter;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ITimeLineService {
    ResponseEntity<Map<String,Object>> timeline(String email, int page, int size);

}
