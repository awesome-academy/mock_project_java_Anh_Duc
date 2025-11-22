package asterisk.sun.booking_tours.module.user;

import java.util.List;

import org.springframework.stereotype.Service;
import asterisk.sun.booking_tours.module.user.projection.UserBasicProjection;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<UserBasicProjection> findAllBasic() {
        return userRepository.findAllBy();
    }

    public <T> List<T> findAllProjected(Class<T> type) {
        return userRepository.findAllProjectedBy(type);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
