package asterisk.sun.booking_tours.application.rest.admin.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.admin.user.UserAdminService;
import asterisk.sun.booking_tours.application.api.common.dto.PaginatedResponse;
import asterisk.sun.booking_tours.application.rest.admin.user.dto.GetUsersRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.user.dto.ListUserResponseDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.user.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/admin/users")
public class ApiAdminUserController {
    private final UserAdminService userAdminService;

    public ApiAdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<ListUserResponseDTO>> getListUsers(GetUsersRequestDTO param) {
        Page<User> users = userAdminService.queryListUserByKeywordApiAdmin(param);
        List<ListUserResponseDTO> data = MapperHelper.mapList(users.getContent(), ListUserResponseDTO.class);

        PaginatedResponse<ListUserResponseDTO> response = new PaginatedResponse<>(
                HttpStatus.OK.value(),
                "Get List Users Successfully",
                data,
                users.getTotalElements(),
                users.getNumber() + 1,
                users.getSize());

        return ResponseEntity.ok(response);
    }
}
