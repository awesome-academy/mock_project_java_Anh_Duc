package asterisk.sun.booking_tours.admin.dto.user;

import asterisk.sun.booking_tours.module.user.Role;
import asterisk.sun.booking_tours.module.user.UserStatus;

public interface UserListProjection {
    Long getId();
    String getUsername();
    String getEmail();
    Role getRole();
    UserStatus getStatus();
}
