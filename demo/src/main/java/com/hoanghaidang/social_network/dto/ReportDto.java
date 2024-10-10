package com.hoanghaidang.social_network.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportDto {
    private int postCount;
    private int newFriendCount;
    private int newLikesCount;
    private int newCommentsCount;
}
