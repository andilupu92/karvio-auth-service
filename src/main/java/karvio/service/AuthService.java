package karvio.service;

import karvio.client.CarServiceClient;
import karvio.client.DocumentServiceClient;
import karvio.dto.*;
import karvio.entity.Role;
import karvio.entity.User;
import karvio.enums.AuthProvider;
import karvio.enums.RoleName;
import karvio.exception.ResourceNotFoundException;
import karvio.repository.RoleRepository;
import karvio.repository.UserRepository;
import karvio.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final DocumentServiceClient documentServiceClient;
    private final CarServiceClient carServiceClient;

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new UsernameNotFoundException("Email already registered!");
        }

        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        User user = new User();
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setProvider(AuthProvider.LOCAL);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);

        return "The user " + request.email() + " was created";
    }

    public TokenResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);

        return new TokenResponse(accessToken, refreshToken);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.getEmailFromToken(refreshToken);

            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String newAccessToken = jwtUtil.generateAccessToken(authentication);
            String newRefreshToken = jwtUtil.generateRefreshToken(authentication);

            return new RefreshTokenResponse(newAccessToken, newRefreshToken);
        }

        throw new RuntimeException("Refresh token is invalid or expired!");
    }

    @Transactional
    public void delete(Long userId) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }

            documentServiceClient.deleteAllDocumentsAndExpensesByUser(userId);
            carServiceClient.deleteAllCarsByUser(userId);
            userRepository.deleteById(userId);

        } catch (Exception e) {
            throw new RuntimeException("Deletion failed, rolled back", e);
        }

    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
