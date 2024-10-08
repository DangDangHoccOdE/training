package com.hoanghaidang.social_network.aop;

import com.hoanghaidang.social_network.dao.FriendShipRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.utils.SecurityUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FriendshipAOP {
    private static final String ACCESS_DENIED_MESSAGE = "You do not have access!";
    private final FriendShipRepository friendShipRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public FriendshipAOP(FriendShipRepository friendShipRepository, UserRepository userRepository, SecurityUtils securityUtils) {
        this.friendShipRepository = friendShipRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.FriendshipController.sendFriendRequest(..)) && args(..,senderId,receiverId)", argNames = "senderId,receiverId")
    public void hasAccessSendFriendship(long senderId,long receiverId) throws AccessDeniedException {
        User user = userRepository.findUserById(senderId).get();
        if (securityUtils.hasNotAccessByUserId(user.getId()) || senderId == receiverId) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.FriendshipController.deleteFriendship(..)) && args(friendshipId))")
    public void hasAccessDeleteFriendship(long friendshipId) throws AccessDeniedException {
        FriendShip friendShip = friendShipRepository.findById(friendshipId).get();
        if (securityUtils.hasNotAccessByUserId(friendShip.getUser2().getId())
            || securityUtils.hasNotAccessByUserId(friendShip.getUser1().getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "(execution(* com.hoanghaidang.social_network.controller.FriendshipController.acceptFriend(..))" +
            " || execution(* com.hoanghaidang.social_network.controller.FriendshipController.declineFriendship(..)))"+
            "&& args(friendshipId)")
    public void hasAccessFriendship(long friendshipId) throws AccessDeniedException {
        FriendShip friendShip = friendShipRepository.findById(friendshipId).get();
        if (securityUtils.hasNotAccessByUserId(friendShip.getUser2().getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }
}
