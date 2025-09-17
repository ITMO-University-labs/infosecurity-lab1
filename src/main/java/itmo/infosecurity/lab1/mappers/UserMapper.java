package itmo.infosecurity.lab1.mappers;

import itmo.infosecurity.lab1.dto.UserDto;
import itmo.infosecurity.lab1.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
