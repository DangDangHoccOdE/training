package com.hoanghaidang.social_network.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FriendshipDto {
    @NotNull(message = "ReceiverId is required")
    private long receiverId;
}
