package com.hoanghaidang.social_network.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Builder
public class EditCommentDto {

    private List<String> image;

    @Size(max = 10000,message = "Content must not exceed 2500 words")
    private String content;
}
