package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.service.impl.TimeLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "Timeline Management", description = "APIs for managing Timeline")
public class TimeLineController {
    TimeLineService timeLineService;

    @Operation(summary = "Time line", description = "Time line")
    @GetMapping("/timeline")
    public ResponseEntity<Map<String,Object>> timeline(Authentication authentication,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size
    ) {
        return timeLineService.timeline(authentication.getName(), page,size);
    }
}
