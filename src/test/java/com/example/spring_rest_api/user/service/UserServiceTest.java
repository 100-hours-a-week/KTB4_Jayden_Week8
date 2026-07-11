package com.example.spring_rest_api.user.service;

import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.common.exception.RequestConflictException;
import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.repository.ImageFileRepository;
import com.example.spring_rest_api.user.entity.User;
import com.example.spring_rest_api.user.repository.UserQueryRepository;
import com.example.spring_rest_api.user.repository.UserRepository;
import com.example.spring_rest_api.user.service.request.UserCreateRequest;
import com.example.spring_rest_api.user.service.request.UserUpdateInfoRequest;
import com.example.spring_rest_api.user.service.request.UserUpdatePasswordRequest;
import com.example.spring_rest_api.user.service.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserQueryRepository userQueryRepository;
    @Mock
    ImageFileRepository imageFileRepository;

    @Test
    @DisplayName("회원 가입 성공")
    void createSuccessTest() {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "email@abcd.com",
                "1234!Abc",
                "nickname",
                null
        );
        String encodedPassword = "encodedPassword";
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(userRepository.findByNickname(request.getNickname())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(passwordEncoder.encode(request.getPassword())).willReturn(encodedPassword);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        //when
        UserResponse response = userService.create(request);

        //then
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(passwordEncoder, times(1)).encode(request.getPassword());

        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo(encodedPassword);
        assertThat(response.getNickname()).isEqualTo(request.getNickname());
        assertThat(response.getProfileImageUrl()).isEqualTo(request.getProfileImageUrl());
    }

    @Test
    @DisplayName("이메일 중복이면 회원 가입 실패")
    void createFailedEmailTest() {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "email@abcd.com",
                "1234!Abc",
                "nickname",
                null
        );
        User savedUser = User.create(
                "email@abcd.com",
                "encodedPassword",
                "saved",
                null
        );
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(savedUser));

        //when
        //then
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(RequestConflictException.class);

        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    @DisplayName("닉네임 중복이면 회원 가입 실패")
    void createFailedNicknameTest() {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "email@abcd.com",
                "1234!Abc",
                "nickname",
                null
        );
        User savedUser = User.create(
                "email2@abcd.com",
                "encodedPassword",
                "nickname",
                null
        );
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(userRepository.findByNickname(request.getNickname())).willReturn(Optional.of(savedUser));

        //when
        //then
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(RequestConflictException.class);

        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(userRepository, times(1)).findByNickname(request.getNickname());
    }

    @Test
    @DisplayName("패스워드 암호화 성공")
    void passwordEncodingTest() {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "email@abcd.com",
                "1234!Abc",
                "nickname",
                null
        );
        String encodedPassword = "encodedPassword";

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(userRepository.findByNickname(request.getNickname())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(passwordEncoder.encode(request.getPassword())).willReturn(encodedPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        //when
        userService.create(request);

        //then
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("사용자 조회 성공")
    void readSuccessTest() {
        //given
        User savedUser = User.create(
                "email@abcd.com",
                "encodedPassword",
                "nickname",
                null
        );
        given(userQueryRepository.findByIdWithProfileImage(anyLong())).willReturn(Optional.of(savedUser));

        //when
        UserResponse response = userService.read(1L);

        //then
        assertThat(response.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(response.getNickname()).isEqualTo(savedUser.getNickname());
        assertThat(response.getProfileImageUrl()).isNull();
    }

    @Test
    @DisplayName("사용자 조회 실패")
    void readFailedTest() {
        //given
        given(userQueryRepository.findByIdWithProfileImage(anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> userService.read(1L))
                .isInstanceOf(NotFoundException.class);
        verify(userQueryRepository, times(1)).findByIdWithProfileImage(anyLong());
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void updateInformationTest() {
        //given
        UserUpdateInfoRequest request = new UserUpdateInfoRequest(
                "change",
                "https://image1.jpg"
        );
        User savedUser = User.create(
                "email@abcd.com",
                "encodedPassword",
                "nickname",
                null
        );
        ImageFile file = ImageFile.createProfileImage(
                "https://image1.jpg",
                1L
        );
        given(userQueryRepository.findByIdWithProfileImage(anyLong())).willReturn(Optional.of(savedUser));
        given(userRepository.findByNickname(request.getNickname())).willReturn(Optional.empty());
        given(imageFileRepository.findByFilePath(anyString())).willReturn(Optional.of(file));

        //when
        UserResponse response = userService.updateInformation(1L, request);

        //then
        assertThat(response.getNickname()).isEqualTo(request.getNickname());
        assertThat(response.getProfileImageUrl()).isEqualTo(request.getProfileImageUrl());
    }

    @Test
    @DisplayName("사용자 정보 수정 - 사용자 조회 실패")
    void updateInformationNotFoundTest() {
        //given
        Long userId = 1L;

        given(userQueryRepository.findByIdWithProfileImage(userId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> userService.updateInformation(userId, any()))
                .isInstanceOf(NotFoundException.class);
        verify(userQueryRepository, times(1)).findByIdWithProfileImage(userId);
    }

    @Test
    @DisplayName("사용자 정보 수정 - 닉네임 중복 실패")
    void updateInformationConflictTest() {
        //given
        Long userId = 1L;
        UserUpdateInfoRequest request = new UserUpdateInfoRequest(
                "change",
                null
        );
        User updatedUser = User.create(
                "email1@abcd.com",
                "encodedPassword",
                "previous",
                null
        );
        User findUser = User.create(
                "email2@abcd.com",
                "encodedPassword",
                "change",
                null
        );
        User userSetId = setUserId(findUser, 2L);
        given(userQueryRepository.findByIdWithProfileImage(userId)).willReturn(Optional.of(updatedUser));
        given(userRepository.findByNickname(request.getNickname())).willReturn(Optional.of(userSetId));
        //when
        //then
        assertThatThrownBy(() -> userService.updateInformation(userId, request))
                .isInstanceOf(RequestConflictException.class);
        verify(userQueryRepository, times(1)).findByIdWithProfileImage(userId);
        verify(userRepository, times(1)).findByNickname(request.getNickname());
    }

    @Test
    @DisplayName("사용자 정보 수정 - 프로필만 업데이트 성공")
    void updateInformationProfileSuccessTest() {
        //given
        Long userId = 1L;
        UserUpdateInfoRequest request = new UserUpdateInfoRequest(
                "nickname",
                "https://image1.jpg"
        );
        User findUser = User.create(
                "email1@abcd.com",
                "encodedPassword",
                "nickname",
                null
        );
        ImageFile file = ImageFile.createProfileImage(
                "https://image1.jpg",
                1L
        );
        User idSetUser = setUserId(findUser, 1L);
        given(userQueryRepository.findByIdWithProfileImage(userId)).willReturn(Optional.of(idSetUser));
        given(userRepository.findByNickname(request.getNickname())).willReturn(Optional.of(idSetUser));
        given(imageFileRepository.findByFilePath(anyString())).willReturn(Optional.of(file));

        //when
        UserResponse response = userService.updateInformation(userId, request);
        //then
        assertThat(response.getNickname()).isEqualTo(request.getNickname());
        assertThat(response.getProfileImageUrl()).isEqualTo(request.getProfileImageUrl());
    }

    private User setUserId(User user, Long userId) {
        ReflectionTestUtils.setField(user, "userId", userId);
        return user;
    }

    @Test
    @DisplayName("회원 비밀번호 수정 성공")
    void updatePasswordSuccessTest() {
        //given
        Long userId = 1L;
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
                "AbcAbc1234!"
        );
        User findUser = User.create(
                "email@abcd.com",
                "encodedPassword",
                "nickname",
                null
        );
        given(userRepository.findById(userId)).willReturn(Optional.of(findUser));
        given(passwordEncoder.encode(request.getPassword())).willReturn("UpdateEncodedPassword");
        //when
        UserResponse response = userService.updatePassword(userId, request);

        //then
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        assertThat(response.getEmail()).isEqualTo(findUser.getEmail());
    }

    @Test
    @DisplayName("회원 비밀번호 수정 - 회원 조회 실패")
    void updatePasswordNotFoundTest() {
        //given
        Long userId = 1L;
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
                "AbcAbc1234!"
        );
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(NotFoundException.class);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteSuccessTest() {
        //given
        Long userId = 1L;

        User saved = User.create(
                "email@abcd.com",
                "encodedPassword",
                "nickname",
                null
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(saved));
        //when
        UserResponse response = userService.delete(userId);

        //then
        assertThat(response.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("회원 탈퇴 - 회원 조회 실패")
    void deleteNotFoundTest() {
        //given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(NotFoundException.class);
        verify(userRepository, times(1)).findById(userId);
    }
}