package com.reddit.backend.service;

import com.reddit.backend.dto.AuthResponse;
import com.reddit.backend.dto.LoginRequest;
import com.reddit.backend.dto.RegisterRequest;
import com.reddit.backend.exception.ActivationException;
import com.reddit.backend.model.AccountVerificationToken;
import com.reddit.backend.model.NotificationEmail;
import com.reddit.backend.model.User;
import com.reddit.backend.repository.TokenRepository;
import com.reddit.backend.repository.UserRepository;
import com.reddit.backend.security.JWTProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.reddit.backend.config.Constants.EMAIL_ACTIVATION;

@Service
@AllArgsConstructor
public class AuthService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    TokenRepository tokenRepository;
    MailService mailService;
    MailBuilder mailBuilder;
    AuthenticationManager authenticationManager;
    JWTProvider jwtProvider;

    @Transactional
    public void register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodePassword(registerRequest.getPassword()));
        user.setCreationDate(Instant.now());
        user.setAccountStatus(false);

        userRepository.save(user);

        String token = generateToken(user);

        String message = mailBuilder.build("Welcome to React-Spring-Reddit Clone. " +
                "Please visit the link below to activate you account : " + EMAIL_ACTIVATION + "/" + token);
        mailService.sendMail(new NotificationEmail("Please Activate Your Account", user.getEmail(), message));
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(user.getUsername()).orElseThrow( () ->
                new UsernameNotFoundException("User not found with username: " + user.getUsername()));
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authToken = jwtProvider.generateToken(authentication);
        return new AuthResponse(authToken, loginRequest.getUsername());
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        AccountVerificationToken verificationToken = new AccountVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        tokenRepository.save(verificationToken);
        return token;
    }

    public void verifyToken(String token) {
        Optional<AccountVerificationToken> verificationToken = tokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new ActivationException("Invalid activation token"));
        enableAccount(verificationToken.get());
    }

    public void enableAccount(AccountVerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ActivationException("User not found with username: " + username));
        user.setAccountStatus(true);
        userRepository.save(user);
    }
}
