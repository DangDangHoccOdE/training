package com.hoanghaidang.social_network.mapper;

import com.hoanghaidang.social_network.dto.response.FriendshipResponse;
import com.hoanghaidang.social_network.entity.FriendShip;
import org.springframework.stereotype.Component;

@Component
public class FriendshipMapper {
    public FriendshipResponse toFriendship(FriendShip friendShip){
        FriendshipResponse friendshipResponse = new FriendshipResponse();
        friendshipResponse.setId(friendShip.getId());
        friendshipResponse.setStatus(friendShip.getStatus());

        return friendshipResponse;
    }
}
