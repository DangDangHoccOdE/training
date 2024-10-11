package com.hoanghaidang.social_network.service.inter;

import org.springframework.http.ResponseEntity;

public interface ITimeLineService {
    ResponseEntity<?> timeline(String email, int page, int size);

}
