package com.hoanghaidang.social_network.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipDto {
    @NotNull(message = "ReceiverId is required")
    private long receiverId;
}
