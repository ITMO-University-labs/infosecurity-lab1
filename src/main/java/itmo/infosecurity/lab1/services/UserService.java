package itmo.infosecurity.lab1.services;

import itmo.infosecurity.lab1.dto.*;
import itmo.infosecurity.lab1.entities.RefreshToken;
import itmo.infosecurity.lab1.entities.User;
import itmo.infosecurity.lab1.exceptions.InvalidRefreshTokenException;
import itmo.infosecurity.lab1.exceptions.UserExistsException;
import itmo.infosecurity.lab1.exceptions.UserNotFoundException;
import itmo.infosecurity.lab1.mappers.UserMapper;
import itmo.infosecurity.lab1.repositories.RefreshTokenRepository;
import itmo.infosecurity.lab1.repositories.UserRepository;
import itmo.infosecurity.lab1.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder;

    public JwtAccessToken addRefreshToken(User user) {
        JwtDto jwtDto = jwtService.generateAuthToken(user.getEmail());
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(user.getId());
        if (optionalRefreshToken.isEmpty()) {
            RefreshToken token = new RefreshToken();
            token.setUserId(user.getId());
            token.setToken(jwtDto.refreshToken());
            refreshTokenRepository.save(token);
        }

        return new JwtAccessToken(jwtDto.accessToken());
    }

    public JwtAccessToken addUser(UserRegistrationDto userRegistrationDto) throws UserExistsException {
        Optional<User> optionalUser = userRepository.findByEmail(userRegistrationDto.email());
        if (optionalUser.isPresent())
            throw new UserExistsException("Пользователь с такой почтой уже существует!");
        User user = userMapper.toEntity(userRegistrationDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return addRefreshToken(userRepository.save(user));
    }

    public JwtAccessToken signIn(UserSignInDto userDto) throws UserNotFoundException {
        User user = findByCredentials(userDto);
        return addRefreshToken(user);
    }

    public JwtAccessToken refreshToken(String token) throws UserNotFoundException, InvalidRefreshTokenException {
        String email = jwtService.getEmailFromToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(user.getId());
            if (optionalRefreshToken.isPresent()) {
                RefreshToken refreshToken = optionalRefreshToken.get();
                if (jwtService.validateJwtToken(refreshToken.getToken()))
                    return new JwtAccessToken(jwtService.refreshAuthToken(email));

                refreshTokenRepository.delete(refreshToken);
                throw new InvalidRefreshTokenException("Невалидный refresh токен!");
            }
        }

        throw new UserNotFoundException("Пользователь не найден!");
    }

    private User findByCredentials(UserSignInDto userSignInDto) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(userSignInDto.email());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(userSignInDto.password(), user.getPassword()))
                return user;
        }

        throw new UserNotFoundException("Неверный логин или пароль!");
    }

    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        userRepository.deleteUserByEmail(email);
    }
}
