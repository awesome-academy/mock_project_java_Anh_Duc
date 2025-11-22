package asterisk.sun.booking_tours.admin.services;

import java.util.List;
import org.springframework.stereotype.Service;
import asterisk.sun.booking_tours.admin.dto.user.FormCreateUserDTO;
import asterisk.sun.booking_tours.admin.dto.user.ListUserDTO;
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
}
