package asterisk.sun.booking_tours.application.rest.admin.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.admin.user.UserAdminService;
import asterisk.sun.booking_tours.application.admin.user.dto.FormCreateUserDTO;
import asterisk.sun.booking_tours.application.admin.user.dto.FormUpdateUserDTO;
import asterisk.sun.booking_tours.application.api.common.dto.PaginatedResponse;
import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.rest.admin.user.dto.GetUsersRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.user.dto.ListUserResponseDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.user.User;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


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

    @PostMapping
    public ResponseEntity<SuccessResponse<String>> store(@Valid @RequestBody FormCreateUserDTO request) {
        userAdminService.createUser(request);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.CREATED.value(),
                "User created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> update(
            @PathVariable Long id,
            @Valid @RequestBody FormUpdateUserDTO request) {
        request.setId(id);
        userAdminService.updateUser(request);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "User updated successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> delete(@PathVariable Long id) {
        userAdminService.deleteUser(id);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "User deleted successfully");
        return ResponseEntity.ok(response);
    }
}
