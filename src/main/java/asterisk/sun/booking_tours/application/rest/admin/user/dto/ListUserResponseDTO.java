package asterisk.sun.booking_tours.application.rest.admin.user.dto;

import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListUserResponseDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private UserStatus status;
}
