package com.hoanghaidang.social_network.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PostDto {
    @Size(max = 255,message = "Title must not exceed 255 characters")
    private String title;

    private String content;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Public|Private|Friends only",message = "Status must be 'Public' or 'Private' or 'Friends only'")
    private String status;

    private List<String> image;

}
