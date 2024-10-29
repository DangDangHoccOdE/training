package com.hoanghaidang.social_network.dto.request;

import com.hoanghaidang.social_network.entity.Status;
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

    @Size(max = 255,message = "Content must not exceed 255 characters")
    private String content;

    private Status status;

    private List<String> image;

}
