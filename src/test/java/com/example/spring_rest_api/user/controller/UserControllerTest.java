package com.example.spring_rest_api.user.controller;

import com.example.spring_rest_api.authorization.jwt.JwtProvider;
import com.example.spring_rest_api.common.config.SecurityConfig;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.common.exception.RequestConflictException;
import com.example.spring_rest_api.user.entity.User;
import com.example.spring_rest_api.user.service.UserService;
import com.example.spring_rest_api.user.service.request.UserCreateRequest;
import com.example.spring_rest_api.user.service.request.UserUpdateInfoRequest;
import com.example.spring_rest_api.user.service.request.UserUpdatePasswordRequest;
import com.example.spring_rest_api.user.service.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtProvider.class})
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("회원가입 성공 - 201")
    void createSuccessTest() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "abcd@abc.com",
                "Abcabc1234!",
                "nickname",
                null
        );
        given(userService.create(request)).willReturn(any(UserResponse.class));

        //when
        //then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("register_success"));

        verify(userService, times(1)).create(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("회원가입 이메일 공백 - 400")
    void createEmailBlankTest() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "",
                "Abcabc1234!",
                "nickname",
                null
        );

        //when
        //then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).create(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("회원가입 비밀번호 공백 - 400")
    void createPasswordBlankTest() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "abcd@abc.com",
                "",
                "nickname",
                null
        );

        //when
        //then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).create(any(UserCreateRequest.class));

    }

    @Test
    @DisplayName("회원가입 닉네임 공백 - 400")
    void createNicknameBlankTest() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "abcd@abc.com",
                "Abcabc1234!",
                "",
                null
        );

        //when
        //then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).create(any(UserCreateRequest.class));

    }

    @Test
    @DisplayName("회원가입 이메일 형식 - 400")
    void createEmailFormTest() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "abcd.com",
                "Abcabc1234!",
                "nickname",
                null
        );

        //when
        //then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).create(any(UserCreateRequest.class));

    }

    @Test
    @DisplayName("회원가입 비밀번호 형식 - 400")
    void createPasswordFormTest() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "abcd@abc.com",
                "abcabc123",
                "nickname",
                null
        );

        //when
        //then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).create(any(UserCreateRequest.class));

    }

    @Test
    @DisplayName("회원가입 닉네임 형식 - 400")
    void createNicknameFormTest() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "abcd@abc.com",
                "Abcabc1234!",
                "nickname0000000000",
                null
        );

        //when
        //then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).create(any(UserCreateRequest.class));

    }

    @Test
    @DisplayName("회원가입 이메일, 닉네임 중복 - 409")
    void createEmailConflictTest() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "abcd@abc.com",
                "Abcabc1234!",
                "nickname",
                null
        );
        given(userService.create(any(UserCreateRequest.class))).willThrow(new RequestConflictException("이메일, 닉네임 중복"));

        //when
        //then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(userService, times(1)).create(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("회원 조회 성공 - 200")
    void readSuccessTest() throws Exception {
        //given
        UserResponse response = UserResponse.from(User.create(
                "email@abc.com",
                "Abcabc1234!",
                "nickname",
                null
        ));
        String token = UUID.randomUUID().toString();
        given(userService.read(anyLong())).willReturn(response);
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        get("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("get_user_info_success"))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(response.getNickname()))
                .andExpect(jsonPath("$.data.profileImageUrl").isEmpty());

        verify(userService, times(1)).read(any(Long.class));
    }

    @Test
    @DisplayName("회원 조회 실패 - 404")
    void readNotFoundTest() throws Exception {
        //given
        String token = UUID.randomUUID().toString();
        given(userService.read(anyLong())).willThrow(new NotFoundException("USER_NOT_FOUND"));
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        get("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound());

        verify(userService, times(1)).read(any(Long.class));
    }

    @Test
    @DisplayName("회원 조회 토큰 인증 실패 - 401")
    void readUnauthorizedTest() throws Exception {
        //given
        String token = UUID.randomUUID().toString();
        given(jwtProvider.isAccessToken(token)).willReturn(false);

        //when
        //then
        mockMvc.perform(
                        get("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isUnauthorized());

        verify(userService, times(0)).read(any(Long.class));
    }

    @Test
    @DisplayName("회원 정보 수정 성공 200")
    void updateInformationSuccessTest() throws Exception {
        //given
        UserUpdateInfoRequest request = new UserUpdateInfoRequest(
                "nickname",
                null
        );

        UserResponse response = UserResponse.from(User.create(
                "email@abc.com",
                "Abcabc1234!",
                "change",
                null
        ));
        String token = UUID.randomUUID().toString();
        given(userService.updateInformation(anyLong(), any(UserUpdateInfoRequest.class))).willReturn(response);
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        patch("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_info_update_success"))
                .andExpect(jsonPath("$.data.nickname").value(response.getNickname()));

        verify(userService, times(1)).updateInformation(anyLong(), any(UserUpdateInfoRequest.class));
    }

    @Test
    @DisplayName("회원 정보 수정 - 토큰 인증 실패 401")
    void updateInformationUnauthorizedTest() throws Exception {
        //given
        String token = UUID.randomUUID().toString();
        given(jwtProvider.isAccessToken(token)).willReturn(false);

        //when
        //then
        mockMvc.perform(
                        patch("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(objectMapper.writeValueAsString(any(UserUpdateInfoRequest.class)))
                )
                .andExpect(status().isUnauthorized());
        verify(userService, times(0)).updateInformation(anyLong(), any(UserUpdateInfoRequest.class));

    }

    @Test
    @DisplayName("회원 정보 수정 - 회원 조회 실패 404")
    void updateInformationNotFoundTest() throws Exception {
        //given
        UserUpdateInfoRequest request = new UserUpdateInfoRequest(
                "nickname",
                null
        );
        String token = UUID.randomUUID().toString();
        given(userService.updateInformation(anyLong(), any(UserUpdateInfoRequest.class))).willThrow(new NotFoundException("USER_NOT_FOUND"));
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        patch("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateInformation(anyLong(), any(UserUpdateInfoRequest.class));
    }

    @Test
    @DisplayName("회원 정보 수정 - 닉네임 중복 409")
    void updateInformationConflictTest() throws Exception {
        //given
        UserUpdateInfoRequest request = new UserUpdateInfoRequest(
                "nickname",
                null
        );
        String token = UUID.randomUUID().toString();
        given(userService.updateInformation(anyLong(), any(UserUpdateInfoRequest.class))).willThrow(new RequestConflictException("이미 존재하는 닉네임입니다."));
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        patch("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(userService, times(1)).updateInformation(anyLong(), any(UserUpdateInfoRequest.class));
    }

    @Test
    @DisplayName("회원 정보 수정 - 닉네임 검증 실패 400")
    void updateInformationValidationTest() throws Exception {
        //given
        UserUpdateInfoRequest request = new UserUpdateInfoRequest(
                "nickname0000000000",
                null
        );
        String token = UUID.randomUUID().toString();
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        patch("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).updateInformation(anyLong(), any(UserUpdateInfoRequest.class));

    }

    @Test
    @DisplayName("회원 비밀번호 수정 성공 200")
    void updatePasswordSuccessTest() throws Exception {
        //given
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
                "Abc1234!"
        );
        UserResponse response = UserResponse.from(User.create(
                "email@abc.com",
                "Abc1234!",
                "change",
                null
        ));
        String token = UUID.randomUUID().toString();
        given(userService.updatePassword(anyLong(), any(UserUpdatePasswordRequest.class))).willReturn(response);
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        patch("/users/me/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_password_update_success"));

        verify(userService, times(1)).updatePassword(anyLong(), any(UserUpdatePasswordRequest.class));
    }

    @Test
    @DisplayName("회원 비밀번호 수정 - 토큰 인증 실패 401")
    void updatePasswordUnauthorizedTest() throws Exception {
        //given
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
                "Abc1234!"
        );
        String token = UUID.randomUUID().toString();
        given(jwtProvider.isAccessToken(token)).willReturn(false);

        //when
        //then
        mockMvc.perform(
                        patch("/users/me/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());

        verify(userService, times(0)).updatePassword(anyLong(), any(UserUpdatePasswordRequest.class));
    }

    @Test
    @DisplayName("회원 비밀번호 수정 - 회원 조회 실패 404")
    void updatePasswordNotFoundTest() throws Exception {
        //given
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
                "Abc1234!"
        );
        String token = UUID.randomUUID().toString();
        given(userService.updatePassword(anyLong(), any(UserUpdatePasswordRequest.class))).willThrow(new NotFoundException("USER_NOT_FOUND"));
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        patch("/users/me/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updatePassword(anyLong(), any(UserUpdatePasswordRequest.class));
    }

    @Test
    @DisplayName("회원 비밀번호 수정 - 비밀번호 검증 실패 400")
    void updatePasswordValidationTest() throws Exception {
        //given
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
                "abc12345"
        );
        String token = UUID.randomUUID().toString();
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        patch("/users/me/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).updatePassword(anyLong(), any(UserUpdatePasswordRequest.class));
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteSuccessTest() throws Exception {
        //given
        UserResponse response = UserResponse.from(User.create(
                                "email@abc.com",
                                "Abc1234!",
                                "nickname",
                                null
                        )
                        .delete()
        );
        String token = UUID.randomUUID().toString();
        given(userService.delete(anyLong())).willReturn(response);
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        delete("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_delete_success"));

        verify(userService, times(1)).delete(anyLong());
    }

    @Test
    @DisplayName("회원 탈퇴 - 토큰 인증 실패")
    void deleteUnauthorizedTest() throws Exception {
        //given
        String token = UUID.randomUUID().toString();
        given(jwtProvider.isAccessToken(token)).willReturn(false);

        //when
        //then
        mockMvc.perform(
                        delete("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isUnauthorized());

        verify(userService, times(0)).delete(anyLong());
    }

    @Test
    @DisplayName("회원 탈퇴 - 회원 조회 실패")
    void deleteNotFoundTest() throws Exception {
        //given
        String token = UUID.randomUUID().toString();
        given(userService.delete(anyLong())).willThrow(new NotFoundException("USER_NOT_FOUND"));
        given(jwtProvider.isAccessToken(token)).willReturn(true);

        //when
        //then
        mockMvc.perform(
                        delete("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound());

        verify(userService, times(1)).delete(anyLong());
    }
}