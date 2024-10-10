package com.hoanghaidang.social_network.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendshipDto {
    @NotNull(message = "SenderId is required")
    private long senderId;

    @NotNull(message = "ReceiverId is required")
    private long receiverId;
}
