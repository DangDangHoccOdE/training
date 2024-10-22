package com.hoanghaidang.social_network.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Builder
public class EditCommentDto {

    private List<String> image;

    private String content;
}
