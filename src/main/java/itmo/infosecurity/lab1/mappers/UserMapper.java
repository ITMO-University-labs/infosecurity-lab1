package itmo.infosecurity.lab1.mappers;

import itmo.infosecurity.lab1.dto.UserRegistrationDto;
import itmo.infosecurity.lab1.dto.UserResponseDto;
import itmo.infosecurity.lab1.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toEntity(UserRegistrationDto userRegistrationDto);
}
