package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.UserRequestDto;
import com.lite.thinking.app.application.dto.UserResponseDto;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Role;
import com.lite.thinking.app.domain.model.User;
import com.lite.thinking.app.domain.repository.RoleRepository;
import com.lite.thinking.app.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_whenIdentificationIsAvailable_savesUserWithEncodedPassword() {
        Role role = role(2L, "CLIENT");
        when(userRepository.existsByIdentification("123")).thenReturn(false);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponseDto response = userService.createUser(request("123", "Cliente", 2L, true));

        assertEquals(1L, response.getId());
        assertEquals("123", response.getIdentification());
        assertEquals("CLIENT", response.getRole().getName());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("encoded-password", captor.getValue().getPassword());
    }

    @Test
    void createUser_whenIdentificationExists_throwsEntityAlreadyExistsException() {
        when(userRepository.existsByIdentification("123")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> userService.createUser(request("123", "Cliente", 2L, true)));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void createUser_whenRoleDoesNotExist_throwsEntityNotFoundException() {
        when(userRepository.existsByIdentification("123")).thenReturn(false);
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.createUser(request("123", "Cliente", 2L, true)));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_whenExists_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "123", "Cliente", role(2L, "CLIENT"), true)));

        UserResponseDto response = userService.getUserById(1L);

        assertEquals("Cliente", response.getName());
    }

    @Test
    void getUserByIdentification_whenExists_returnsUser() {
        when(userRepository.findByIdentification("123"))
                .thenReturn(Optional.of(user(1L, "123", "Cliente", role(2L, "CLIENT"), true)));

        UserResponseDto response = userService.getUserByIdentification("123");

        assertEquals(1L, response.getId());
    }

    @Test
    void getAllUsers_returnsMappedUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                user(1L, "123", "Cliente", role(2L, "CLIENT"), true),
                user(2L, "456", "Admin", role(1L, "ADMIN"), false)
        ));

        List<UserResponseDto> response = userService.getAllUsers();

        assertEquals(2, response.size());
        assertFalse(response.get(1).isActive());
    }

    @Test
    void updateUser_whenExists_updatesDataRoleAndPassword() {
        Role role = role(1L, "ADMIN");
        User existingUser = user(1L, "123", "Cliente", role(2L, "CLIENT"), true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByIdentification("456")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secret123")).thenReturn("new-encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto response = userService.updateUser(1L, request("456", "Admin", 1L, false));

        assertEquals("456", response.getIdentification());
        assertEquals("ADMIN", response.getRole().getName());
        assertFalse(response.isActive());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("new-encoded-password", captor.getValue().getPassword());
    }

    @Test
    void updateUser_whenIdentificationBelongsToAnotherUser_throwsEntityAlreadyExistsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "123", "Cliente", role(2L, "CLIENT"), true)));
        when(userRepository.existsByIdentification("456")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> userService.updateUser(1L, request("456", "Admin", 1L, true)));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_whenExists_deletesUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void getUsersByRoleId_whenRoleExists_returnsUsers() {
        when(roleRepository.existsById(2L)).thenReturn(true);
        when(userRepository.findByRoleId(2L)).thenReturn(List.of(
                user(1L, "123", "Cliente", role(2L, "CLIENT"), true)
        ));

        List<UserResponseDto> response = userService.getUsersByRoleId(2L);

        assertEquals(1, response.size());
        assertEquals("CLIENT", response.getFirst().getRole().getName());
    }

    @Test
    void getUsersByRoleId_whenRoleDoesNotExist_throwsEntityNotFoundException() {
        when(roleRepository.existsById(2L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.getUsersByRoleId(2L));

        verify(userRepository, never()).findByRoleId(2L);
    }

    private UserRequestDto request(String identification, String name, Long roleId, Boolean isActive) {
        return UserRequestDto.builder()
                .identification(identification)
                .name(name)
                .email("user@test.com")
                .phone("300")
                .password("secret123")
                .roleId(roleId)
                .isActive(isActive)
                .build();
    }

    private User user(Long id, String identification, String name, Role role, boolean isActive) {
        return User.builder()
                .id(id)
                .identification(identification)
                .name(name)
                .email("user@test.com")
                .phone("300")
                .password("encoded")
                .role(role)
                .isActive(isActive)
                .build();
    }

    private Role role(Long id, String name) {
        return Role.builder()
                .id(id)
                .name(name)
                .isActive(true)
                .build();
    }
}
