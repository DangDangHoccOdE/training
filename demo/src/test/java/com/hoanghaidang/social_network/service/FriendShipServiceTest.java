package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.FriendShipRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.impl.FriendShipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FriendShipServiceTest {
    @InjectMocks
    private FriendShipService friendShipService;
    @Mock
    private Authentication authentication;
    @Mock
    private FriendShipRepository friendShipRepository;
    @Mock
    private UserRepository userRepository;

    private User sender, receiver;
    private FriendShip friendShip;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        sender = createUser(1L, "a@gmail.com");
        receiver = createUser(2L, "b@gmail.com");
        friendShip = createFriendShip(1L, sender, receiver, "pending");
    }

    @Test
    void sendFriendRequest_Success() {
        mockAuthenticationAndUser(sender);

        when(userRepository.findUserById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(friendShipRepository.findByUser1AndUser2(sender, receiver)).thenReturn(Optional.empty());
        when(friendShipRepository.findByUser1AndUser2(receiver, sender)).thenReturn(Optional.empty());

        ResponseEntity<Notice> response = friendShipService.sendFriendRequest(authentication, receiver.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(friendShipRepository, times(1)).save(any(FriendShip.class));
    }

    @Test
    void testAcceptFriendship_NotFoundFriendship() {
        when(friendShipRepository.findById(friendShip.getId())).thenThrow(new CustomException("Friendship is not found", HttpStatus.NOT_FOUND));
        mockAuthenticationAndUser(sender);

        CustomException exception = assertThrows(CustomException.class, () -> friendShipService.acceptFriendRequest(authentication, friendShip.getId()));

        assertEquals("Friendship is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testSendFriendship_NotFoundUser() {
        when(authentication.getName()).thenReturn(sender.getEmail());
        when(userRepository.findByEmail(sender.getEmail())).thenThrow(new CustomException("User is not found", HttpStatus.NOT_FOUND));

        CustomException customException = assertThrows(CustomException.class, () -> friendShipService.sendFriendRequest(authentication, receiver.getId()));

        assertEquals("User is not found", customException.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, customException.getStatus());
    }

    @Test
    void testSendFriendship_FailDuplicateInvitations() {
        mockAuthenticationAndUser(sender);
        when(userRepository.findUserById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(friendShipRepository.findByUser1AndUser2(sender, receiver)).thenReturn(Optional.of(friendShip));
        friendShip.setStatus("pending");

        CustomException customException = assertThrows(CustomException.class, () -> friendShipService.sendFriendRequest(authentication, receiver.getId()));

        assertEquals("Send duplicate invitations!", customException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getStatus());
    }

    @Test
    void testSendFriendship_FailNotAccess() {
        mockAuthenticationAndUser(sender);
        when(userRepository.findUserById(sender.getId())).thenReturn(Optional.of(sender));

        when(friendShipRepository.findByUser1AndUser2(sender, receiver)).thenReturn(Optional.of(friendShip));

        AccessDeniedException customException = assertThrows(AccessDeniedException.class, () -> friendShipService.sendFriendRequest(authentication, sender.getId()));

        assertEquals("You do have not access", customException.getMessage());
    }

    @Test
    void testAcceptFriendRequest_FailNotOwner() {
        User other = createUser(3L, "o@gmail.com");

        when(authentication.getName()).thenReturn(other.getEmail());
        when(userRepository.findByEmail(other.getEmail())).thenReturn(Optional.of(other));
        when(friendShipRepository.findById(friendShip.getId())).thenReturn(Optional.of(friendShip));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> friendShipService.acceptFriendRequest(authentication, friendShip.getId()));

        assertEquals("You do have not access", exception.getMessage());
    }

    @Test
    void testAcceptFriendRequest_Success() {
        friendShip.setStatus("pending");
        mockAuthenticationAndUser(receiver);

        when(friendShipRepository.findById(friendShip.getId())).thenReturn(Optional.of(friendShip));

        ResponseEntity<Notice> response = friendShipService.acceptFriendRequest(authentication, friendShip.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("accepted", friendShip.getStatus());
        assertEquals("Add friend is completed", Objects.requireNonNull(response.getBody()).getMessage());
        verify(friendShipRepository, times(1)).save(friendShip);
    }

    @Test
    void testDeclineFriendship_Success() {
        friendShip.setStatus("pending");
        mockAuthenticationAndUser(receiver);

        when(friendShipRepository.findById(friendShip.getId())).thenReturn(Optional.of(friendShip));

        ResponseEntity<Notice> response = friendShipService.declineFriendShip(authentication, friendShip.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("declined", friendShip.getStatus());
        assertEquals("Friendship declined successfully", Objects.requireNonNull(response.getBody()).getMessage());
        verify(friendShipRepository, times(1)).save(friendShip);
    }

    @Test
    void testDeleteFriendship_Success() {
        friendShip.setStatus("accepted");
        mockAuthenticationAndUser(receiver);

        when(friendShipRepository.findById(friendShip.getId())).thenReturn(Optional.of(friendShip));

        ResponseEntity<Notice> response = friendShipService.deleteFriendShip(authentication, friendShip.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Unfriended successfully", Objects.requireNonNull(response.getBody()).getMessage());
        verify(friendShipRepository).delete(friendShip);
    }

    @Test
    void testDeleteFriendship_FailNotAccept() {
        mockAuthenticationAndUser(sender);
        friendShip.setStatus("pending");

        when(friendShipRepository.findById(friendShip.getId())).thenReturn(Optional.of(friendShip));

        CustomException exception = assertThrows(CustomException.class, () -> friendShipService.deleteFriendShip(authentication, friendShip.getId()));

        assertEquals("Operation failed, Cannot unfriend", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // Helper methods
    private User createUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    private FriendShip createFriendShip(Long id, User user1, User user2, String status) {
        FriendShip friendShip = new FriendShip();
        friendShip.setId(id);
        friendShip.setUser1(user1);
        friendShip.setUser2(user2);
        friendShip.setStatus(status);
        return friendShip;
    }

    private void mockAuthenticationAndUser(User user) {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }
}


