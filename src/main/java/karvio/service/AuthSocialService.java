package karvio.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import karvio.dto.CustomUserDetails;
import karvio.dto.TokenRequest;
import karvio.dto.TokenResponse;
import karvio.entity.Role;
import karvio.entity.User;
import karvio.enums.AuthProvider;
import karvio.enums.RoleName;
import karvio.repository.RoleRepository;
import karvio.repository.UserRepository;
import karvio.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthSocialService {

    @Value("${webClient.id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;

    @Transactional
    public TokenResponse loginWithGoogle(TokenRequest request) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(request.token());

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            if (!userRepository.existsByEmail(email)) {
                User newUser = new User();
                newUser.setEmail(email);

                Role role = roleRepository.findByName(RoleName.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                String randomPassword = UUID.randomUUID().toString();
                newUser.setPassword(passwordEncoder.encode(randomPassword));
                newUser.setProvider(AuthProvider.GOOGLE);
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                newUser.setRoles(roles);

                userRepository.save(newUser);
            }

            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String newAccessToken = jwtUtil.generateAccessToken(authentication);
            String newRefreshToken = jwtUtil.generateRefreshToken(authentication);

            return new TokenResponse(newAccessToken, newRefreshToken);
        } else {
            throw new RuntimeException("Invalid Google Token");
        }
    }

    public TokenResponse loginWithApple(TokenRequest request) {
        return null;
    }
}
