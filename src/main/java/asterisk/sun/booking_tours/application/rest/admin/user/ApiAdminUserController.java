package asterisk.sun.booking_tours.application.rest.admin.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.admin.user.UserAdminService;
import asterisk.sun.booking_tours.application.rest.admin.user.dto.ListUserResponseDTO;
import asterisk.sun.booking_tours.application.rest.common.dto.RestSuccessResponse;

import java.util.List;

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
    public ResponseEntity<RestSuccessResponse<List<ListUserResponseDTO>>> getListUsers() {
        List<ListUserResponseDTO> users = userAdminService.queryListUserByKeywordApiAdmin(null);

        RestSuccessResponse<List<ListUserResponseDTO>> response = new RestSuccessResponse<>(
                200,
                "Get List Users Successfully",
                users
        );

        return ResponseEntity.ok(response);
    }
}
