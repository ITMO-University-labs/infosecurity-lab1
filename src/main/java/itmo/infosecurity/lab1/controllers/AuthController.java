package itmo.infosecurity.lab1.controllers;

import itmo.infosecurity.lab1.dto.JwtAccessToken;
import itmo.infosecurity.lab1.dto.UserRegistrationDto;
import itmo.infosecurity.lab1.dto.UserSignInDto;
import itmo.infosecurity.lab1.exceptions.InvalidRefreshTokenException;
import itmo.infosecurity.lab1.exceptions.UserExistsException;
import itmo.infosecurity.lab1.exceptions.UserNotFoundException;
import itmo.infosecurity.lab1.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid UserRegistrationDto userRegistrationDto) throws UserExistsException {
        JwtAccessToken token = userService.addUser(userRegistrationDto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody @Valid UserSignInDto userSignInDto) throws UserNotFoundException {
        JwtAccessToken token = userService.signIn(userSignInDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authHeader)
            throws UserNotFoundException, InvalidRefreshTokenException {
        return ResponseEntity.ok(userService.refreshToken(authHeader.substring(7)));
    }
}
