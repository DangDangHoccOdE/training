package com.hoanghaidang.social_network.mapper;

import com.hoanghaidang.social_network.dto.response.FriendshipResponse;
import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FriendshipMapper {
    public FriendshipResponse toFriendship(FriendShip friendShip, User user) {
        User auth = friendShip.getUser1();

        if(user == auth){
            auth = friendShip.getUser2();
        }

        FriendshipResponse friendshipResponse = new FriendshipResponse();
        friendshipResponse.setUserId(auth.getId());
        friendshipResponse.setFullName(auth.getLastName()+" " +auth.getFirstName());
        friendshipResponse.setAvatar(auth.getAvatar());
        return friendshipResponse;
    }

    public FriendshipResponse getFriendshipSent(FriendShip friendShip, User user) {
        User auth = friendShip.getUser1();

        if(user != auth){
            auth = friendShip.getUser2();
        }

        FriendshipResponse friendshipResponse = new FriendshipResponse();
        friendshipResponse.setUserId(auth.getId());
        friendshipResponse.setFullName(auth.getLastName()+" " +auth.getFirstName());
        friendshipResponse.setAvatar(auth.getAvatar());
        return friendshipResponse;
    }
}
