package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.UserRequestDto;
import com.lite.thinking.app.application.dto.UserResponseDto;
import com.lite.thinking.app.application.mapper.UserMapper;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.User;
import com.lite.thinking.app.domain.model.Role;
import com.lite.thinking.app.domain.repository.UserRepository;
import com.lite.thinking.app.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        if (userRepository.existsByIdentification(requestDto.getIdentification())) {
            throw new EntityAlreadyExistsException("El usuario con identificación '" + requestDto.getIdentification() + "' ya existe.");
        }

        Role role = roleRepository.findById(requestDto.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("El rol con ID " + requestDto.getRoleId() + " no existe."));

        User user = UserMapper.toDomain(requestDto, role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El usuario con ID " + id + " no fue encontrado."));
        return UserMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByIdentification(String identification) {
        User user = userRepository.findByIdentification(identification)
                .orElseThrow(() -> new EntityNotFoundException("El usuario con identificación " + identification + " no fue encontrado."));
        return UserMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El usuario con ID " + id + " no fue encontrado."));

        if (!existingUser.getIdentification().equalsIgnoreCase(requestDto.getIdentification()) &&
                userRepository.existsByIdentification(requestDto.getIdentification())) {
            throw new EntityAlreadyExistsException("El usuario con identificación '" + requestDto.getIdentification() + "' ya existe.");
        }

        Role role = roleRepository.findById(requestDto.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("El rol con ID " + requestDto.getRoleId() + " no existe."));

        existingUser.setIdentification(requestDto.getIdentification());
        existingUser.setName(requestDto.getName());
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setPhone(requestDto.getPhone());
        existingUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        existingUser.setRole(role);
        if (requestDto.getIsActive() != null) {
            existingUser.setActive(requestDto.getIsActive());
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("El usuario con ID " + id + " no fue encontrado.");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByRoleId(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new EntityNotFoundException("El rol con ID " + roleId + " no existe.");
        }
        return userRepository.findByRoleId(roleId).stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
