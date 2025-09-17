package itmo.infosecurity.lab1.services;

import itmo.infosecurity.lab1.dto.JwtAccessToken;
import itmo.infosecurity.lab1.dto.JwtDto;
import itmo.infosecurity.lab1.dto.UserDto;
import itmo.infosecurity.lab1.dto.UserSignInDto;
import itmo.infosecurity.lab1.entities.RefreshToken;
import itmo.infosecurity.lab1.entities.User;
import itmo.infosecurity.lab1.mappers.UserMapper;
import itmo.infosecurity.lab1.repositories.RefreshTokenRepository;
import itmo.infosecurity.lab1.repositories.UserRepository;
import itmo.infosecurity.lab1.security.JwtService;
import itmo.infosecurity.lab1.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public JwtAccessToken connectTokensToUser(User user) {
        JwtDto jwtDto = jwtService.generateAuthToken(user.getEmail());
        if (user.getRefreshToken() == null) {
            RefreshToken token = new RefreshToken();
            token.setToken(jwtDto.refreshToken());
            token.setUser(user);
            user.setRefreshToken(token);
            userRepository.save(user);
        }

        return new JwtAccessToken(jwtDto.accessToken());
    }

    public JwtAccessToken addUser(UserDto userDto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.email());
        if (optionalUser.isPresent())
            throw new AuthenticationException();
        User user = userMapper.toEntity(userDto);
        user.setPassword(PasswordHasher.hashPassword(user.getPassword(), salt));
        return connectTokensToUser(userRepository.save(user));
    }

    public JwtAccessToken signIn(UserSignInDto userDto) throws AuthenticationException {
        User user = findByCredentials(userDto);
        return connectTokensToUser(user);
    }

    public JwtAccessToken refreshToken(String token) throws AuthenticationException {
        String email = jwtService.getEmailFromToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            RefreshToken refreshToken = user.getRefreshToken();
            if (refreshToken != null && jwtService.validateJwtToken(refreshToken.getToken()))
                return new JwtAccessToken(jwtService.refreshAuthToken(email));

            user.setRefreshToken(null);
            userRepository.save(user);
        }
        throw new AuthenticationException("Невалидный refresh токен");
    }

    private User findByCredentials(UserSignInDto userSignInDto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByEmail(userSignInDto.email());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (PasswordHasher.verifyPassword(userSignInDto.password(), user.getPassword(), salt))
                return user;
        }

        throw new AuthenticationException();
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }
}
