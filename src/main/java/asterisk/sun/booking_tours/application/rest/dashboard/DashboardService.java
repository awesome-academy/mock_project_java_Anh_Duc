package asterisk.sun.booking_tours.application.rest.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.rest.dashboard.dto.UserCountDTO;
import asterisk.sun.booking_tours.core.user.UserRepository;
import asterisk.sun.booking_tours.core.user.UserStatus;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    public UserCountDTO getUserCount() {
        Long totalUsers = userRepository.count();

        Long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
        Long inactiveUsers = userRepository.countByStatus(UserStatus.INACTIVE);

        return new UserCountDTO(totalUsers, activeUsers, inactiveUsers);
    }
}
