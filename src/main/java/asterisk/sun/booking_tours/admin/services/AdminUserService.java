package asterisk.sun.booking_tours.admin.services;

import java.util.List;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.admin.dto.user.FormCreateUserDTO;
import asterisk.sun.booking_tours.admin.dto.user.FormUpdateUserDTO;
import asterisk.sun.booking_tours.admin.dto.user.ListUserDTO;
import asterisk.sun.booking_tours.admin.exceptions.NotFoundException;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.module.user.User;
import asterisk.sun.booking_tours.module.user.UserService;

@Service
public class AdminUserService {
    private final UserService userService;

    public AdminUserService(UserService userService) {
        this.userService = userService;
    }

    public List<ListUserDTO> getAllUsersForListing() {
        return MapperHelper.mapList(userService.findAllBasic(), ListUserDTO.class);
    }

    public void createUser(FormCreateUserDTO formCreateUserDTO) {
        User user = MapperHelper.map(formCreateUserDTO, User.class);
        userService.save(user);
    }

    public FormUpdateUserDTO getUserById(Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new NotFoundException("User with ID " + id + " not found");
        }
        return MapperHelper.map(user, FormUpdateUserDTO.class);
    }

    public void updateUser(FormUpdateUserDTO formUpdateUserDTO) {
        User user = userService.findById(formUpdateUserDTO.getId());
        if (user != null) {
            user.setUsername(formUpdateUserDTO.getUsername());
            user.setFirstName(formUpdateUserDTO.getFirstName());
            user.setLastName(formUpdateUserDTO.getLastName());
            user.setEmail(formUpdateUserDTO.getEmail());
            user.setPhone(formUpdateUserDTO.getPhone());
            user.setRole(formUpdateUserDTO.getRole());
            user.setStatus(formUpdateUserDTO.getStatus());
            user.setAvatarUrl(formUpdateUserDTO.getAvatarUrl());
            userService.save(user);
        }
    }

    public void deleteUser(Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new NotFoundException("User with ID " + id + " not found");
        }
        userService.deleteById(id);
    }
}
