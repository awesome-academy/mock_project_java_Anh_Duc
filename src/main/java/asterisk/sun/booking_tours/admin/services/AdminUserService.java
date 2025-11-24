package asterisk.sun.booking_tours.admin.services;

import java.util.List;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.admin.dto.user.FormCreateUserDTO;
import asterisk.sun.booking_tours.admin.dto.user.FormUpdateUserDTO;
import asterisk.sun.booking_tours.admin.dto.user.ListUserDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.domain.user.User;
import asterisk.sun.booking_tours.domain.user.UserService;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminUserService {
    private final UserService userService;

    public AdminUserService(UserService userService) {
        this.userService = userService;
    }

    public List<ListUserDTO> queryListUserByKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return MapperHelper.mapList(userService.searchByKeyword(keyword), ListUserDTO.class);
        }

        return MapperHelper.mapList(userService.findAll(), ListUserDTO.class);
    }

    public void createUser(FormCreateUserDTO formCreateUserDTO) {
        User user = MapperHelper.map(formCreateUserDTO, User.class);
        userService.save(user);
    }

    public FormUpdateUserDTO getUserById(Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        return MapperHelper.map(user, FormUpdateUserDTO.class);
    }

    public void updateUser(FormUpdateUserDTO formUpdateUserDTO) {
        User user = userService.findById(formUpdateUserDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + formUpdateUserDTO.getId()));

        if (user != null) {
            user.setUsername(formUpdateUserDTO.getUsername());
            user.setFirstName(formUpdateUserDTO.getFirstName());
            user.setLastName(formUpdateUserDTO.getLastName());
            user.setEmail(formUpdateUserDTO.getEmail());
            user.setPhone(formUpdateUserDTO.getPhone());
            user.setRole(formUpdateUserDTO.getRole());
            user.setStatus(formUpdateUserDTO.getStatus());
            user.setAvatarUrl(formUpdateUserDTO.getAvatarUrl());

            // Update password only if provided
            if (formUpdateUserDTO.getPassword() != null && !formUpdateUserDTO.getPassword().isEmpty()) {
                user.setPassword(formUpdateUserDTO.getPassword());
            }

            userService.save(user);
        }
    }

    public void deleteUser(Long id) {
        User user = userService.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        userService.delete(user);
    }
}
