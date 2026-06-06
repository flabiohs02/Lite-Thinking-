package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.UserRequestDto;
import com.lite.thinking.app.application.dto.UserResponseDto;
import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto requestDto);
    UserResponseDto getUserById(Long id);
    UserResponseDto getUserByIdentification(String identification);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UserRequestDto requestDto);
    void deleteUser(Long id);
    List<UserResponseDto> getUsersByRoleId(Long roleId);
}
