package com.hoanghaidang.social_network.dto.request;

import com.hoanghaidang.social_network.enums.PostStatus;
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

    @Size(max = 10000,message = "Title must not exceed 2500 words")
    private String content;

    private PostStatus postStatus;

    private List<String> image;

}
