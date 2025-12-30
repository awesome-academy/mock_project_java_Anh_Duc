package asterisk.sun.booking_tours.application.rest.admin.user.dto;

import asterisk.sun.booking_tours.application.api.common.dto.PaginateRequest;
import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor

public class GetUsersRequestDTO extends PaginateRequest {
    private String keyword;
    private Role role;
    private UserStatus status;
}
