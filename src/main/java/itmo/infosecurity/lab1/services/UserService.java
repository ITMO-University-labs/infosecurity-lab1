package itmo.infosecurity.lab1.services;

import itmo.infosecurity.lab1.dto.JwtAccessToken;
import itmo.infosecurity.lab1.dto.JwtDto;
import itmo.infosecurity.lab1.dto.UserDto;
import itmo.infosecurity.lab1.dto.UserSignInDto;
import itmo.infosecurity.lab1.entities.RefreshToken;
import itmo.infosecurity.lab1.entities.User;
import itmo.infosecurity.lab1.exceptions.UserNotFoundException;
import itmo.infosecurity.lab1.mappers.UserMapper;
import itmo.infosecurity.lab1.repositories.RefreshTokenRepository;
import itmo.infosecurity.lab1.repositories.UserRepository;
import itmo.infosecurity.lab1.security.JwtService;
import itmo.infosecurity.lab1.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.password-salt}")
    private String salt;

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

    public JwtAccessToken addUser(UserDto userDto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.email());
        if (optionalUser.isPresent())
            throw new AuthenticationException();
        User user = userMapper.toEntity(userDto);
        user.setPassword(PasswordHasher.hashPassword(user.getPassword(), salt));
        return addRefreshToken(userRepository.save(user));
    }

    public JwtAccessToken signIn(UserSignInDto userDto) throws UserNotFoundException {
        User user = findByCredentials(userDto);
        return addRefreshToken(user);
    }

    public JwtAccessToken refreshToken(String token) throws AuthenticationException, UserNotFoundException {
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
                throw new AuthenticationException("Невалидный refresh токен");
            }
        }

        throw new UserNotFoundException("Пользователь не найден!");
    }

    private User findByCredentials(UserSignInDto userSignInDto) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(userSignInDto.email());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (PasswordHasher.verifyPassword(userSignInDto.password(), user.getPassword(), salt))
                return user;
        }

        throw new UserNotFoundException("Неверный логин или пароль!");
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        userRepository.deleteUserByEmail(email);
    }
}
