package com.hoanghaidang.social_network.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadImageResponse {
    private List<String> images;
}
