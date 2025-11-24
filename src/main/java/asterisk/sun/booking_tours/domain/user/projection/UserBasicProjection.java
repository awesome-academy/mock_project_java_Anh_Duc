package asterisk.sun.booking_tours.domain.user.projection;

import asterisk.sun.booking_tours.domain.user.Role;
import asterisk.sun.booking_tours.domain.user.UserStatus;

public interface UserBasicProjection {
    Long getId();
    String getUsername();
    String getEmail();
    Role getRole();
    UserStatus getStatus();
}
