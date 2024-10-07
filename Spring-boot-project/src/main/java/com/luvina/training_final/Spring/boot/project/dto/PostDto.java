package com.luvina.training_final.Spring.boot.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDto {
    private long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255,message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Pattern(regexp = "^([0-2][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4} ([01][0-9]|2[0-3]):[0-5][0-9]$",
            message = "Date must be in the format dd/MM/yyyy HH:mm")
    private String createAt;

    @Pattern(regexp = "^([0-2][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4} ([01][0-9]|2[0-3]):[0-5][0-9]$",
            message = "Date must be in the format dd/MM/yyyy HH:mm")
    private String updateAt;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Public|Private",message = "Status must be 'Public' or 'Private'")
    private String status;

    private long likeCount;

    private long commentCount;

    private List<String> image;

    @NotNull(message = "User ID is required")
    private long userId;
}
