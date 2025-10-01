package itmo.infosecurity.lab1.controllers;

import itmo.infosecurity.lab1.dto.JwtAccessToken;
import itmo.infosecurity.lab1.dto.UserDto;
import itmo.infosecurity.lab1.dto.UserSignInDto;
import itmo.infosecurity.lab1.exceptions.UserNotFoundException;
import itmo.infosecurity.lab1.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid UserDto userDto) {
        try {
            JwtAccessToken token = userService.addUser(userDto);
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь с такой почтой уже существует!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody @Valid UserSignInDto userSignInDto) throws UserNotFoundException {
        JwtAccessToken token = userService.signIn(userSignInDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authHeader) throws UserNotFoundException {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                return ResponseEntity.ok(userService.refreshToken(authHeader.substring(7)));
            } catch (AuthenticationException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный refresh токен!");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невалидный access токен!");
    }
}
