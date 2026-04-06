package com.acasado.opored.service.jpa;

import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthLoginRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.dto.auth.RefreshData;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.exception.AliasAlreadyRegisteredException;
import com.acasado.opored.exception.EmailAlreadyRegisteredException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.*;
import com.acasado.opored.security.BruteForceSecurityService;
import com.acasado.opored.security.RefreshTokenService;
import com.acasado.opored.util.JwtUtils;
import com.acasado.opored.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    private final AdministratorRepository administratorRepository;
    private final ModeratorRepository moderatorRepository;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final BruteForceSecurityService bruteForceSecurityService;
    private final RefreshTokenService refreshTokenService;

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final ProfessorRepository professorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().getName()));

        userEntity.getRole().getPermissions()
                        .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName().name())));

        return new User(userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorities);
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUser) {
        String username = authCreateUser.getEmail();
        RoleEnum role = RoleEnum.valueOf(authCreateUser.getRole());

        if (userRepository.findByEmail(username).isPresent()) {
            throw new EmailAlreadyRegisteredException("User with email " + username + " already exists");
        }
        if (userRepository.findByAlias(authCreateUser.getAlias()).isPresent()) {
            throw new AliasAlreadyRegisteredException("User with alias " + authCreateUser.getAlias() + " already exists");
        }

        // Alias validation
        if (!SecurityUtils.aliasValidation(authCreateUser.getAlias())) {
            throw new BadCredentialsException("Alias is not valid");
        }

        // Email validation
        if (!SecurityUtils.emailValidation(username)) {
            throw new BadCredentialsException("Email is not valid");
        }
        // Password validation
        if (!SecurityUtils.passwordValidation(authCreateUser.getPassword())) {
            throw new BadCredentialsException("Password is not valid");
        }

        UserEntity userSaved = switch (role.name()) {
            case "ADMIN" -> {
                AdministratorEntity administrator = new AdministratorEntity(
                        authCreateUser.getName(),
                        authCreateUser.getSurname(),
                        authCreateUser.getAlias(),
                        authCreateUser.getEmail(),
                        passwordEncoder.encode(authCreateUser.getPassword()),
                        new UserAccountStatus(true, true, true, true),
                        roleRepository.getRoleByName(role));
                yield administratorRepository.save(administrator);
            }
            case "PROFESSOR" -> {
                ProfessorEntity professor = new ProfessorEntity(
                        authCreateUser.getName(),
                        authCreateUser.getSurname(),
                        authCreateUser.getAlias(),
                        authCreateUser.getEmail(),
                        passwordEncoder.encode(authCreateUser.getPassword()),
                        new UserAccountStatus(true, true, true, true),
                        roleRepository.getRoleByName(role),
                        new LinkedHashSet<>());
                yield professorRepository.save(professor);
            }
            case "MODERATOR" -> {
                ModeratorEntity moderator = new ModeratorEntity(
                        authCreateUser.getName(),
                        authCreateUser.getSurname(),
                        authCreateUser.getAlias(),
                        authCreateUser.getEmail(),
                        passwordEncoder.encode(authCreateUser.getPassword()),
                        new UserAccountStatus(true, true, true, true),
                        roleRepository.getRoleByName(role));
                yield moderatorRepository.save(moderator);
            }
            case "STUDENT" -> {
                StudentEntity student = new StudentEntity(
                        authCreateUser.getName(),
                        authCreateUser.getSurname(),
                        authCreateUser.getAlias(),
                        authCreateUser.getEmail(),
                        passwordEncoder.encode(authCreateUser.getPassword()),
                        new UserAccountStatus(true, true, true, true),
                        roleRepository.getRoleByName(role));
                yield studentRepository.save(student);
            }
            default -> throw new BadCredentialsException("Invalid role");
        };

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + userSaved.getRole().getName()));

        userSaved.getRole().getPermissions()
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName().name())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userSaved.getId(), null, authorities);

        String accessToken = jwtUtils.createToken(authentication);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken((Integer) authentication.getPrincipal());

        return new AuthResponse(username, "User created successfully", accessToken, refreshToken.getToken(), 200);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.getUsername();
        String password = authLoginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken((Integer) authentication.getPrincipal());

        return new AuthResponse(username, "User logged in successfully", accessToken, refreshToken.getToken(), 200);
    }

    public Authentication authenticate(String email, String password) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        UserDetails userDetails = loadUserByUsername(email);

        if (bruteForceSecurityService.isBlocked(email)) {
            throw new BadCredentialsException("You are temporarily blocked due to too many failed login attempts.");
        }
        if (userDetails == null) {
            bruteForceSecurityService.loginFailed(email);
            throw new BadCredentialsException("Invalid email or password.");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            bruteForceSecurityService.loginFailed(email);
            throw new BadCredentialsException("Invalid password.");
        }
        // Restart the number of attempts if the user succesfully logs in
        bruteForceSecurityService.loginSucceeded(email);

        return new UsernamePasswordAuthenticationToken(userEntity.getId(), userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthResponse refreshToken(RefreshData refreshData) {

        RefreshTokenEntity refreshTokenEntity = refreshTokenService.verifyExpiration(refreshData.getRefreshToken());

        UserEntity user = refreshTokenEntity.getUser();

        Authentication authentication = buildAuthentication(user);

        String newAccessToken = jwtUtils.createToken(authentication);

        refreshTokenService.revoke(refreshTokenEntity);
        RefreshTokenEntity newRefresh = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                user.getEmail(),
                "User token refreshed",
                newAccessToken,
                newRefresh.getToken(),
                200
        );
    }

    public void logout(RefreshData refreshData) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.verifyExpiration(refreshData.getRefreshToken());
        refreshTokenService.revoke(refreshTokenEntity);
    }

    private Authentication buildAuthentication(UserEntity user) {
        UserDetails userDetails = loadUserByUsername(user.getEmail());

        return new UsernamePasswordAuthenticationToken(
                user.getId(),
                null,
                userDetails.getAuthorities()
        );
    }
}
