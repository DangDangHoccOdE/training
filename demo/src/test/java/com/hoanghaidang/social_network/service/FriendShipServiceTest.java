package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.FriendShipRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.FriendshipResponse;
import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.enums.FriendStatus;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.FriendshipMapper;
import com.hoanghaidang.social_network.service.impl.FriendShipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    @Mock
    private FriendshipMapper friendshipMapper;

    private User sender, receiver;
    private FriendShip friendShip;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        sender = createUser(1L, "a@gmail.com");
        receiver = createUser(2L, "b@gmail.com");
        friendShip = createFriendShip(1L, sender, receiver, FriendStatus.PENDING);
    }

    @Test
    void testFriendListByUser_Success(){
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("updateAt").descending());

        List<FriendShip> friendShips = List.of(friendShip);

        mockAuthenticationAndUser(receiver);
        when(friendShipRepository.findAllByUserAndStatus(receiver,FriendStatus.ACCEPTED,pageable)).thenReturn(new PageImpl<>(friendShips));
        when(friendshipMapper.toFriendship(any(),any())).thenReturn(new FriendshipResponse());

        ResponseEntity<ApiResponse<Map<String, Object>>> response = friendShipService.friendListByUser(authentication, page, size);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody().getData();
        assertNotNull(responseBody);

        // Check the response data
        List<FriendshipResponse> friends = (List<FriendshipResponse>) responseBody.get("friends");
        assertEquals(1, friends.size());  // We expect two friends in the list
    }

    @Test
    void testFriendRequestListByUser_Success() throws Exception {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        mockAuthenticationAndUser(receiver);
        friendShip.setStatus(FriendStatus.PENDING);
        List<FriendShip> pendingFriendShips = List.of(friendShip);

        Page<FriendShip> friendShipPage = new PageImpl<>(pendingFriendShips, pageable, pendingFriendShips.size());
        when(friendShipRepository.findAllRequestByUserAndStatus(any(), eq(FriendStatus.PENDING), eq(pageable)))
                .thenReturn(friendShipPage);

        when(friendshipMapper.toFriendship(any(), any())).thenReturn(new FriendshipResponse());

        ResponseEntity<ApiResponse<Map<String, Object>>> response = friendShipService.friendRequestListByUser(authentication, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody().getData();
        assertNotNull(responseBody);

        List<FriendshipResponse> friends = (List<FriendshipResponse>) responseBody.get("friends");
        assertEquals(1, friends.size());  // We expect two pending requests
    }

    @Test
    void testFriendRequestSentListByUser_Success() throws Exception {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        mockAuthenticationAndUser(sender);
        friendShip.setStatus(FriendStatus.PENDING);
        List<FriendShip> pendingFriendShips = List.of(friendShip);

        Page<FriendShip> friendShipPage = new PageImpl<>(pendingFriendShips, pageable, pendingFriendShips.size());
        when(friendShipRepository.findAllSenRequestByUserAndStatus(any(), eq(FriendStatus.PENDING), eq(pageable)))
                .thenReturn(friendShipPage);

        when(friendshipMapper.toFriendship(any(), any())).thenReturn(new FriendshipResponse());

        ResponseEntity<ApiResponse<Map<String, Object>>> response = friendShipService.friendRequestSentListByUser(authentication, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody().getData();
        assertNotNull(responseBody);

        List<FriendshipResponse> friends = (List<FriendshipResponse>) responseBody.get("friends");
        assertEquals(1, friends.size());  // We expect two pending requests
    }


    @Test
    void sendFriendRequest_Success() {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Send add friend is completed")
                .build();
        mockAuthenticationAndUser(sender);

        when(userRepository.findUserById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(friendShipRepository.findByUser1AndUser2(sender, receiver)).thenReturn(Optional.empty());
        when(friendShipRepository.findByUser1AndUser2(receiver, sender)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<Void>> response = friendShipService.sendFriendRequest(authentication, receiver.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        verify(friendShipRepository, times(1)).save(any(FriendShip.class));
    }

    @Test
    void testAcceptFriendship_NotFoundFriendship() {
        when(userRepository.findUserById(any())).thenReturn(Optional.of(receiver));
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

//    @Test
//    void testSendFriendship_FailDuplicateInvitations() {
//        mockAuthenticationAndUser(sender);
//        when(userRepository.findUserById(receiver.getId())).thenReturn(Optional.of(receiver));
//        when(friendShipRepository.findByUser1AndUser2(sender, receiver)).thenReturn(Optional.of(friendShip));
//        friendShip.setStatus(FriendStatus.PENDING);
//
//        CustomException customException = assertThrows(CustomException.class, () -> friendShipService.sendFriendRequest(authentication, receiver.getId()));
//
//        assertEquals("Send duplicate invitations!", customException.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST, customException.getStatus());
//    }

    @Test
    void testSendFriendship_UpdateStatusFriendship() {
        friendShip.setStatus(FriendStatus.DECLINED);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Send add friend is completed")
                .build();

        mockAuthenticationAndUser(sender);

        when(userRepository.findUserById(receiver.getId())).thenReturn(Optional.of(receiver));

        ResponseEntity<ApiResponse<Void>> response = friendShipService.sendFriendRequest(authentication, receiver.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        verify(friendShipRepository, times(1)).save(any(FriendShip.class));
    }

    @Test
    void testSendFriendship_FailStatusPending() {
        mockAuthenticationAndUser(sender);
        when(userRepository.findUserById(sender.getId())).thenReturn(Optional.of(sender));

        when(friendShipRepository.findByUser1AndUser2(sender, receiver)).thenReturn(Optional.of(friendShip));

        CustomException customException = assertThrows(CustomException.class, () -> friendShipService.sendFriendRequest(authentication, sender.getId()));

        assertEquals("Sender and receiver cannot be the same person", customException.getMessage());
    }

    @Test
    void testAcceptFriendRequest_FailNotOwner() {
        User other = createUser(3L, "o@gmail.com");

        when(userRepository.findUserById(any())).thenReturn(Optional.of(receiver));
        mockAuthenticationAndUser(other);
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> friendShipService.acceptFriendRequest(authentication, receiver.getId()));

        assertEquals("You do have not access", exception.getMessage());
    }

    @Test
    void testAcceptFriendRequest_Success() {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Add friend is completed")
                .build();

        friendShip.setStatus(FriendStatus.PENDING);
        mockAuthenticationAndUser(receiver);
        when(userRepository.findUserById(any())).thenReturn(Optional.of(receiver));
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));

        ResponseEntity<ApiResponse<Void>> response = friendShipService.acceptFriendRequest(authentication, friendShip.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FriendStatus.ACCEPTED, friendShip.getStatus());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        verify(friendShipRepository, times(1)).save(friendShip);
    }

    @Test
    void testDeclineFriendship_Success() {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Friendship declined successfully")
                .build();
        friendShip.setStatus(FriendStatus.PENDING);
        mockAuthenticationAndUser(receiver);

        when(userRepository.findUserById(any())).thenReturn(Optional.of(receiver));
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));

        ResponseEntity<ApiResponse<Void>> response = friendShipService.declineFriendShip(authentication, friendShip.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FriendStatus.DECLINED, friendShip.getStatus());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        verify(friendShipRepository, times(1)).save(friendShip);
    }

    @Test
    void testDeclineFriendship_FailStatusPending() {
        mockAuthenticationAndUser(receiver);
        friendShip.setStatus(FriendStatus.ACCEPTED);
        when(userRepository.findUserById(receiver.getId())).thenReturn(Optional.of(receiver));

        when(userRepository.findUserById(any())).thenReturn(Optional.of(receiver));
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));

        CustomException customException = assertThrows(CustomException.class, () -> friendShipService.declineFriendShip(authentication, friendShip.getId()));

        assertEquals("Operation failed, friend request not found", customException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST,customException.getStatus());
    }

    @Test
    void testDeleteFriendship_Success() {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Unfriended successfully")
                .build();
        friendShip.setStatus(FriendStatus.ACCEPTED);
        mockAuthenticationAndUser(receiver);

        when(userRepository.findUserById(any())).thenReturn(Optional.of(receiver));
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));

        ResponseEntity<ApiResponse<Void>> response = friendShipService.deleteFriendShip(authentication, receiver.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        verify(friendShipRepository).delete(friendShip);
    }

//    @Test
//    void testDeleteFriendship_FailAccessDenied() {
//        friendShip.setStatus(FriendStatus.ACCEPTED);
//        mockAuthenticationAndUser(new User());
//
//        when(userRepository.findUserById(any())).thenReturn(Optional.of(receiver));
//        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));
//        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(friendShip));
//
//        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> friendShipService.deleteFriendShip(authentication, friendShip.getId()));
//
//        assertEquals("You do have not access", exception.getMessage());
//    }

//    @Test
//    void testDeleteFriendship_FailNotAccept() {
//        mockAuthenticationAndUser(sender);
//        friendShip.setStatus(FriendStatus.PENDING);
//
//        when(userRepository.findUserById(any())).thenReturn(Optional.of(receiver));
//        when(friendShipRepository.findByUser1AndUser2(sender, receiver)).thenReturn(Optional.of(friendShip));
//        when(friendShipRepository.findByUser1AndUser2(receiver, sender)).thenReturn(Optional.of(friendShip));
//
//        CustomException exception = assertThrows(CustomException.class, () -> friendShipService.deleteFriendShip(authentication, friendShip.getId()));
//
//        assertEquals("Operation failed, Cannot unfriend", exception.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//    }

    // Helper methods
    private User createUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    private FriendShip createFriendShip(Long id, User user1, User user2, FriendStatus status) {
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


