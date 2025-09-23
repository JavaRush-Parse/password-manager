package ua.com.javarush.parse.m5.passwordmanager.mapper;


import org.mapstruct.Mapper;
import ua.com.javarush.parse.m5.passwordmanager.config.MapperConfig;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserRegistrationRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserResponseDto;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto dto);
}
