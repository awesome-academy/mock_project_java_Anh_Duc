package asterisk.sun.booking_tours.module.user.projection;

import asterisk.sun.booking_tours.module.user.Role;
import asterisk.sun.booking_tours.module.user.UserStatus;

public interface UserBasicProjection {
    Long getId();
    String getUsername();
    String getEmail();
    Role getRole();
    UserStatus getStatus();
}
