package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.RoleRequestDto;
import com.lite.thinking.app.application.dto.RoleResponseDto;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Role;
import com.lite.thinking.app.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void createRole_whenNameIsAvailable_savesRole() {
        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(1L);
            return role;
        });

        RoleResponseDto response = roleService.createRole(request("ADMIN", true));

        assertEquals(1L, response.getId());
        assertEquals("ADMIN", response.getName());
    }

    @Test
    void createRole_whenNameExists_throwsEntityAlreadyExistsException() {
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> roleService.createRole(request("ADMIN", true)));

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void getRoleById_whenExists_returnsRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role(1L, "ADMIN", true)));

        RoleResponseDto response = roleService.getRoleById(1L);

        assertEquals("ADMIN", response.getName());
    }

    @Test
    void getRoleByName_whenExists_returnsRole() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role(1L, "ADMIN", true)));

        RoleResponseDto response = roleService.getRoleByName("ADMIN");

        assertEquals(1L, response.getId());
    }

    @Test
    void getAllRoles_returnsMappedRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(
                role(1L, "ADMIN", true),
                role(2L, "CLIENT", false)
        ));

        List<RoleResponseDto> response = roleService.getAllRoles();

        assertEquals(2, response.size());
        assertFalse(response.get(1).isActive());
    }

    @Test
    void updateRole_whenNameChangesAndIsAvailable_updatesRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role(1L, "ADMIN", true)));
        when(roleRepository.existsByName("CLIENT")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoleResponseDto response = roleService.updateRole(1L, request("CLIENT", false));

        assertEquals("CLIENT", response.getName());
        assertFalse(response.isActive());

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());
        assertEquals("CLIENT", captor.getValue().getName());
    }

    @Test
    void updateRole_whenNameBelongsToAnotherRole_throwsEntityAlreadyExistsException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role(1L, "ADMIN", true)));
        when(roleRepository.existsByName("CLIENT")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> roleService.updateRole(1L, request("CLIENT", true)));

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void deleteRole_whenExists_deletesRole() {
        when(roleRepository.existsById(1L)).thenReturn(true);

        roleService.deleteRole(1L);

        verify(roleRepository).deleteById(1L);
    }

    @Test
    void deleteRole_whenDoesNotExist_throwsEntityNotFoundException() {
        when(roleRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> roleService.deleteRole(1L));

        verify(roleRepository, never()).deleteById(1L);
    }

    private RoleRequestDto request(String name, Boolean isActive) {
        return RoleRequestDto.builder()
                .name(name)
                .isActive(isActive)
                .build();
    }

    private Role role(Long id, String name, boolean isActive) {
        return Role.builder()
                .id(id)
                .name(name)
                .isActive(isActive)
                .build();
    }
}
