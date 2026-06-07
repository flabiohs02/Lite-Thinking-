package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.AuthRequestDto;
import com.lite.thinking.app.application.dto.AuthResponseDto;
import com.lite.thinking.app.application.dto.UserResponseDto;
import com.lite.thinking.app.application.usecase.UserService;
import com.lite.thinking.app.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints para registro y login de usuarios")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserDetailsService userDetailsService;
        private final JwtService jwtService;
        private final UserService userService;

        public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                        JwtService jwtService, UserService userService) {
                this.authenticationManager = authenticationManager;
                this.userDetailsService = userDetailsService;
                this.jwtService = jwtService;
                this.userService = userService;
        }

        @PostMapping("/login")
        @Operation(summary = "Autenticar un usuario", description = "Recibe identificación y contraseña, valida credenciales y retorna un JWT token con datos del usuario.")
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
        public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getIdentification(),
                                                request.getPassword()));

                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getIdentification());
                String jwtToken = jwtService.generateToken(userDetails);

                UserResponseDto user = userService.getUserByIdentification(request.getIdentification());

                return ResponseEntity.ok(AuthResponseDto.builder()
                                .token(jwtToken)
                                .role(user.getRole().getName())
                                .name(user.getName())
                                .identification(user.getIdentification())
                                .build());
        }
}
