package sk.stuba.fei.uim.vsa.pr2.resources.responses.factories;

import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.UserDto;

import java.util.Collections;
import java.util.stream.Collectors;

public class UserFactory implements ResponseFactory<User, UserDto>{

    private static final CarFactory carFactory = new CarFactory();

    @Override
    public UserDto transformToDto(User entity) {
       UserDto userDto = new UserDto();
       userDto.setId(entity.getId());
       userDto.setEmail(entity.getEmail());
       userDto.setFirstName(entity.getFirstname());
       userDto.setLastName(entity.getLastname());
       if (entity.getCars() != null)
           userDto.setCars(entity.getCars().stream().map(carFactory::transformToDto).collect(Collectors.toList()));
       else
           userDto.setCars(Collections.emptyList());
       return userDto;
    }

    public UserDto transformToDtoWithoutLooping(User entity) {
        UserDto userDto = new UserDto();
        userDto.setId(entity.getId());
        userDto.setEmail(entity.getEmail());
        userDto.setFirstName(entity.getFirstname());
        userDto.setLastName(entity.getLastname());
        userDto.setCars(Collections.emptyList());
        return userDto;
    }


    @Override
    public User transformToEntity(UserDto dto) {
        User user =  new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstname(dto.getFirstName());
        user.setLastname(dto.getLastName());
        user.setCars(Collections.emptyList());
        return user;
    }
}
