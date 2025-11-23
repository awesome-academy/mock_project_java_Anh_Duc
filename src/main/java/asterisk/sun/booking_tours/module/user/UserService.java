package asterisk.sun.booking_tours.module.user;

import java.util.List;

import org.springframework.stereotype.Service;
import asterisk.sun.booking_tours.module.user.projection.UserBasicProjection;
import asterisk.sun.booking_tours.module.common.abtracts.BaseService;

@Service
public class UserService extends BaseService<User, Long, UserRepository> {

    public UserService(UserRepository userRepository) {
        super(userRepository);
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public List<UserBasicProjection> findAllBasic() {
        return repository.findAllBy();
    }

    public <T> List<T> findAllProjected(Class<T> type) {
        return repository.findAllProjectedBy(type);
    }
}
