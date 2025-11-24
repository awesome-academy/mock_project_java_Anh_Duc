package asterisk.sun.booking_tours.core.user.projection;

import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.UserStatus;

public interface UserBasicProjection {
    Long getId();
    String getUsername();
    String getEmail();
    Role getRole();
    UserStatus getStatus();
}
