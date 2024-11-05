package com.hoanghaidang.social_network.dto.response;

import com.hoanghaidang.social_network.enums.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipResponse {
    private long userId;
    private String fullName;
    private String avatar;
}
