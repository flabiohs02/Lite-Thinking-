package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.RoleRequestDto;
import com.lite.thinking.app.application.dto.RoleResponseDto;
import java.util.List;

public interface RoleService {
    RoleResponseDto createRole(RoleRequestDto requestDto);
    RoleResponseDto getRoleById(Long id);
    RoleResponseDto getRoleByName(String name);
    List<RoleResponseDto> getAllRoles();
    RoleResponseDto updateRole(Long id, RoleRequestDto requestDto);
    void deleteRole(Long id);
}
