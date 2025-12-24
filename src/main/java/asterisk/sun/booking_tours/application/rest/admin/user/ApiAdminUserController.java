package asterisk.sun.booking_tours.application.rest.admin.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.admin.user.UserAdminService;
import asterisk.sun.booking_tours.application.rest.admin.user.dto.ListUserResponseDTO;
import asterisk.sun.booking_tours.application.rest.common.dto.SuccessResponse;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/admin/users")
public class ApiAdminUserController {
    private final UserAdminService userAdminService;

    public ApiAdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<ListUserResponseDTO>>> getListUsers() {
        List<ListUserResponseDTO> users = userAdminService.queryListUserByKeywordApiAdmin(null);

        SuccessResponse<List<ListUserResponseDTO>> response = new SuccessResponse<>(
                200,
                "Get List Users Successfully",
                users
        );

        return ResponseEntity.ok(response);
    }
}
