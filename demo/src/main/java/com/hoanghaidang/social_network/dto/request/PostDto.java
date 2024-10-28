package com.hoanghaidang.social_network.dto.request;

import com.hoanghaidang.social_network.Enum.Status;
import jakarta.validation.constraints.NotBlank;
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
    private Status status;

    private List<String> image;

}
