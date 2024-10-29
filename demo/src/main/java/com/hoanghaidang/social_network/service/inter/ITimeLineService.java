package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ITimeLineService {
    ResponseEntity<ApiResponse<Map<String,Object>>> timeline(String email, int page, int size);

}
